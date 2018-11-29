package com.android.ermo.rakutentvapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.adaptadores.RecyclerAdaptadorPeliculas;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class ListaPeliculasActivity extends AppCompatActivity {
    private ArrayList<Pelicula> m_pelis = new ArrayList<Pelicula>();
    private RecyclerAdaptadorPeliculas adaptadorPeliculas;
    private RecyclerView recyclerView;
    private TextView tx;
    private TextView textSaludo;
    private Button btnPeliculas;
    private Button btnBuscar;
    private Button btnPerfil;
    private Button triggerGrid;

    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();
    private final String PATH_FOTO = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieFotos/";
    private final String PATH_CARATULA = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieCaratula/";

    private static ListaPeliculasActivity listaPeliculasActivity = null;

    public static ListaPeliculasActivity getInstance() {
        return listaPeliculasActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_peliculas);
        listaPeliculasActivity = this;

        getSupportActionBar().setTitle("Cartelera");

        tx = (TextView) findViewById(R.id.textLoggedUser);
        textSaludo = (TextView) findViewById(R.id.textSaludo);
        btnPeliculas = (Button) findViewById(R.id.btnPeliculas);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnPerfil = (Button) findViewById(R.id.btnPerfil);
        triggerGrid = (Button) findViewById(R.id.triggerGrid);

        SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPreferences.edit();

        if (userPreferences.getString("VISTA", "").equals("")) {
            editor.putString("VISTA", "listView");
        } else {
            if (userPreferences.getString("VISTA", "").equals("listView")) {
                RakutenData.setListView(true);
                triggerGrid.setText("Mostrar GridView");

            } else if (userPreferences.getString("VISTA", "").equals("gridView")) {
                RakutenData.setListView(false);
                triggerGrid.setText("Mostrar ListView");
            }
        }
        editor.apply();


        triggerGrid.setOnClickListener(new View.OnClickListener() {
            SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userPreferences.edit();

            @Override
            public void onClick(View v) {
                if (RakutenData.isListView()) {
                    editor.putString("VISTA", "gridView");
                    triggerGrid.setText("Mostar GridView");
                    RakutenData.setListView(false);

                } else {
                    editor.putString("VISTA", "listView");
                    triggerGrid.setText("Mostrar ListView");
                    RakutenData.setListView(true);
                }
                editor.apply();

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FiltroActivity.class);
                startActivity(intent);
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PerfilActivity.class);
                startActivity(intent);
            }
        });


        if (getIntent().hasExtra("usuario") || RakutenData.getUsuario() != null) {
            Usuario usuario = null;
            if (getIntent().hasExtra("usuario")) {
                usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
            } else if (RakutenData.getUsuario() != null) {
                usuario = RakutenData.getUsuario();
            }

            textSaludo.setText("Bienvenido de nuevo ");
            tx.setText(" " + usuario.getUsername());

        } else {
            textSaludo.setText("Modo invitado");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPeliculas);

        HashMap<String, String> parametros = new HashMap<String, String>();
        parametros.put("ACTION", "Pelicula.listAllMejorVotadas");

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    if (RakutenData.isListView()) {
                        adaptadorPeliculas = new RecyclerAdaptadorPeliculas(getBaseContext(), listaPeliculas);
                        recyclerView.setAdapter(adaptadorPeliculas);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    } else {
                        adaptadorPeliculas = new RecyclerAdaptadorPeliculas(getBaseContext(), listaPeliculas);
                        recyclerView.setAdapter(adaptadorPeliculas);
                        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
                    }
                } else {
                    Toast.makeText(ListaPeliculasActivity.getInstance().getBaseContext(), "Lista incorrecta. ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
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


}
