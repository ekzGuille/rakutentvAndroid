package com.android.ermo.rakutentvapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.adaptadores.RecyclerAdaptadorPeliculas;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class Splashscreen extends AppCompatActivity {

    private static final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);

        String userName = userPreferences.getString("userName", "");
        String email = userPreferences.getString("email", "");
        String contrasena = userPreferences.getString("contrasena", "");

        Intent intent = new Intent(this.getBaseContext(), ListaPeliculasActivity.class);
        startActivity(intent);

        finish();

//        if (userName.equals("") || email.equals("") || contrasena.equals("")) {
//            Intent intent = new Intent(this.getBaseContext(), LoginActivity.class);
//            startActivity(intent);
//        } else {
//            ejecutarEnvioLogin(userName, contrasena);
//        }

    }


    public void ejecutarEnvioLogin(String usuario, String contrasena) {
        /*ServiceLogin.accionLogin(edtEmail.getText().toString(), edtPass.getText().toString());*/
        HashMap<String, String> parametros = new HashMap<String, String>();
        // CLAVE------VALOR
        parametros.put("ACTION", "Usuario.login");
        parametros.put("userMail", usuario);
        parametros.put("contrasena", contrasena);

        TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
        tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");
    }

    class TareaSegundoPlano extends AsyncTask<String, Integer, Boolean> {
        private HashMap<String, String> parametros;
        private ArrayList<Usuario> listaUsuarios;

        public TareaSegundoPlano(HashMap<String, String> parametros) {
            this.parametros = parametros;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];//http://localhost:8080/AndroidAsynktaskBack/Controller
            // Importantísimo la clase "POST":
            // Hacer una petición al servidor y recuperar la respuesta en JSON.
            Post post = new Post();
            JSONArray result = post.getServerDataPost(parametros, url);
            listaUsuarios = Usuario.getArrayListFromJSon(result);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                if (listaUsuarios != null && listaUsuarios.size() > 0) {
                    Usuario usuario = listaUsuarios.get(0);

                    Intent intent = new Intent(Splashscreen.this, ListaPeliculasActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }


}
