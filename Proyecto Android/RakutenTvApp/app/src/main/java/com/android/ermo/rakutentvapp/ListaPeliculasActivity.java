package com.android.ermo.rakutentvapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.adaptadores.RecyclerAdaptadorPeliculas;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class ListaPeliculasActivity extends Activity {
    private ArrayList<Pelicula> m_pelis = new ArrayList<Pelicula>();
    private RecyclerAdaptadorPeliculas adaptadorPeliculas;
    private RecyclerView recyclerView;
    private TextView tx;

    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();
    private final String PATH_FOTO = "http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/images/peliculas/movieCaratula/";

    private static ListaPeliculasActivity listaPeliculasActivity = null;
    public static ListaPeliculasActivity getInstance() {
        return listaPeliculasActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_peliculas);
        listaPeliculasActivity = this;

        tx = (TextView) findViewById(R.id.textLoggedUser);

        if(getIntent().hasExtra("usuario")) {
            Usuario usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");

            tx.setText(" "+usuario.getUsername());
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPeliculas);

        HashMap<String, String> parametros = new HashMap<String, String>();
        parametros.put("ACTION", "Pelicula.listAll");

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        pelicula.setCaratulaPeli(PATH_FOTO + pelicula.getCaratulaPeli());
                    }
                    adaptadorPeliculas = new RecyclerAdaptadorPeliculas(getBaseContext(),listaPeliculas);
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
