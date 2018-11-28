package com.android.ermo.rakutentvapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.adaptadores.RecyclerAdaptadorPeliculas;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;
import com.google.android.gms.appindexing.AndroidAppUri;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    private RecyclerAdaptadorPeliculas adaptadorPeliculas;
    private RecyclerView recyclerView;

    private Button btnPeliculas;
    private Button btnBuscar;
    private Button btnPerfil;
    private Spinner spinnerPerfil;


    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();
    private final String PATH_FOTO = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieFotos/";
    private final String PATH_CARATULA = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieCaratula/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        btnPeliculas = (Button) findViewById(R.id.btnPeliculas);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnPerfil = (Button) findViewById(R.id.btnPerfil);
        spinnerPerfil = (Spinner) findViewById(R.id.spinnerPerfil);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPeliculas);

        if (RakutenData.getUsuario() != null) {

            getSupportActionBar().setTitle("Mi perfil");


            cargarSpinner();

            spinnerPerfil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cargarPeliculas(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnPeliculas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), ListaPeliculasActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), FiltroActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(getBaseContext(), "Loggeate para poder acceder a tu perfil", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);

        if (getIntent().hasExtra("usuario") || RakutenData.getUsuario() != null) {
            menu.findItem(R.id.entrar).setVisible(false);
            menu.findItem(R.id.salir).setVisible(true);
        } else {
            menu.findItem(R.id.entrar).setVisible(true);
            menu.findItem(R.id.salir).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.entrar:
            case R.id.salir:

                RakutenData.setUsuario(null);

                SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPreferences.edit();

                editor.remove("userName");
                editor.remove("email");
                editor.remove("contreasena");

                editor.apply();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    private void cargarSpinner() {
        final List<String> valoresSpinner = new ArrayList<String>();
        valoresSpinner.add("Que películas buscas?");
        valoresSpinner.add("Mis favoritas");
        valoresSpinner.add("Mis compradas");
        valoresSpinner.add("Mis votadas");

        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinner) {
//            @Override
//            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                return super.getDropDownView(position + 1, convertView, parent);
//            }
//
//            @Override
//            public int getCount() {
//                return valoresSpinner.size() - 1;
//
//            }
        };
        spinnerPerfil.setAdapter(spAdapter);
    }

    private void cargarPeliculas(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        final int PELICULAS_FAVORITAS = 1;
        final int PELICULAS_COMPRADAS = 2;
        final int PELICULAS_VOTADAS = 3;

        switch (position) {
            case PELICULAS_FAVORITAS:
                parametros.put("ACTION", "Pelicula.listFavoritas");
                parametros.put("USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));

                break;


            case PELICULAS_COMPRADAS:
                parametros.put("ACTION", "Pelicula.listCompradas");
                parametros.put("USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));

                break;


            case PELICULAS_VOTADAS:
                parametros.put("ACTION", "Pelicula.listPuntuadas");
                parametros.put("USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));

                break;
        }

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

    }


    class TareaSegundoPlano extends AsyncTask<String, Integer, Boolean> {
        private ArrayList<Pelicula> listaPeliculas = null;
        private HashMap<String, String> parametros = null;


        public TareaSegundoPlano(HashMap<String, String> parametros) {
            this.parametros = parametros;
        }

        /*
         * doInBackground().
         * Contendrá el código principal de nuestra tarea.
         * */
        @Override
        protected Boolean doInBackground(String... params) {
            // URL
            String url_select = params[0];
            try {
                Post post = new Post();

                JSONArray result = post.getServerDataPost(parametros, url_select);
                listaPeliculas = Pelicula.getArrayListFromJSon(result);
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                //messageUser = "Error al conectar con el servidor. ";
            }

            return true;
        }

        /*
         * onPostExecute().
         * Se ejecutará cuando finalice nuestra tarea, o dicho de otra forma,
         * tras la finalización del método doInBackground().
         * */
        @Override
        protected void onPostExecute(Boolean resp) {
            try {
                if (resp && listaPeliculas != null && listaPeliculas.size() > 0) {
                    for (Pelicula pelicula : listaPeliculas) {
                        pelicula.setCaratulaPeli(PATH_CARATULA + pelicula.getCaratulaPeli());
                        pelicula.setFotoPeli(PATH_FOTO + pelicula.getFotoPeli());
                    }
                    adaptadorPeliculas = new RecyclerAdaptadorPeliculas(getBaseContext(), listaPeliculas);
                    recyclerView.setAdapter(adaptadorPeliculas);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                } else {
                    if (listaPeliculas.size() == 0) {
                        Toast.makeText(ListaPeliculasActivity.getInstance().getBaseContext(), "No se han encontrado peliculas. ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListaPeliculasActivity.getInstance().getBaseContext(), "Lista incorrecta. ", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
    }


}
