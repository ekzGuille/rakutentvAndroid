package com.android.ermo.rakutentvapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.adaptadores.RecyclerAdaptadorPeliculas;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    private RecyclerAdaptadorPeliculas adaptadorPeliculas;
    private RecyclerView recyclerView;

    private Button btnPeliculas;
    private Button btnBuscar;
    private Button btnPerfil;
    private Spinner spinnerBusqueda;
    private Spinner spinnerGenero;
    private TextView searchBar;

    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();
    private final String PATH_FOTO = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieFotos/";
    private final String PATH_CARATULA = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieCaratula/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);


        btnPeliculas = (Button) findViewById(R.id.btnPeliculas);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnPerfil = (Button) findViewById(R.id.btnPerfil);

        spinnerBusqueda = (Spinner) findViewById(R.id.spinnerBusqueda);
        spinnerGenero = (Spinner) findViewById(R.id.spinnerGenero);

        searchBar = (TextView) findViewById(R.id.searchBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPeliculas);

        getSupportActionBar().setTitle("Busca tu pelicula");

        cargarSpinners();

        spinnerBusqueda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    searchBar.setVisibility(View.VISIBLE);
                    spinnerGenero.setVisibility(View.GONE);
                } else {
                    spinnerGenero.setVisibility(View.VISIBLE);
                    searchBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    cargarPeliculas(position);
                }
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

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PerfilActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

        if (getIntent().hasExtra("usuario")) {
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

    private void cargarSpinners() {
        List<String> valoresSpinnerBuscar = new ArrayList<String>();
        valoresSpinnerBuscar.add("Selecciona el tipo de busqueda");
        valoresSpinnerBuscar.add("Buscar por titulo");
        valoresSpinnerBuscar.add("Buscar por géneros");

        ArrayAdapter<String> spAdapterB = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerBuscar);
        spinnerBusqueda.setAdapter(spAdapterB);

        List<String> valoresSpinnerGenero = new ArrayList<String>();
        valoresSpinnerGenero.add("Selecciona un genero");
        valoresSpinnerGenero.add("Acción");
        valoresSpinnerGenero.add("Animación");
        valoresSpinnerGenero.add("Ciencia Ficción");
        valoresSpinnerGenero.add("Cine Español");
        valoresSpinnerGenero.add("Comedia");
        valoresSpinnerGenero.add("Thriller");
        valoresSpinnerGenero.add("Romántico");

        ArrayAdapter<String> spAdapterG = new ArrayAdapter<String>(this, R.layout.my_spinner, valoresSpinnerGenero);
        spinnerGenero.setAdapter(spAdapterG);
    }

    private void cargarPeliculas(int position) {

        HashMap<String, String> parametros = new HashMap<String, String>();

        //TODO
        parametros.put("ACTION", "Pelicula.listAll");
        parametros.put("GENERO", String.valueOf(position));

//        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
//        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

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
                    Toast.makeText(ListaPeliculasActivity.getInstance().getBaseContext(), "Lista incorrecta. ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
    }
}
