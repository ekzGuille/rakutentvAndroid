package com.android.ermo.rakutentvapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends Activity {
    private EditText edtEmail;
    private EditText edtContrasena;
    private Button btnLogin;
    private CheckBox checkRecordar;

    private static final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();

    /*Patrón Singleton*/
    private static LoginActivity loginActivity;

    public static LoginActivity getInstance() {
        return loginActivity;
    }


    /*Fin*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginActivity = this;

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtContrasena = (EditText) findViewById(R.id.edtContrasena);
        checkRecordar = (CheckBox) findViewById(R.id.checkRecordar);
        btnLogin = (Button) findViewById(R.id.btnEnviar);


        SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);

        String userName = userPreferences.getString("userName", "");
        String email = userPreferences.getString("email", "");
        String contrasena = userPreferences.getString("contrasena", "");

        if (userName.equals("") || email.equals("") || contrasena.equals("")) {

            btnLogin.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ejecutarEnvioLogin(edtEmail.getText().toString(), edtContrasena.getText().toString());
                }
            });
        } else {
            ejecutarEnvioLogin(userName, contrasena);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

                    if (checkRecordar.isChecked()) {

                        SharedPreferences userPreferences = getSharedPreferences("informacion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userPreferences.edit();

                        editor.putString("userName", usuario.getUsername());
                        editor.putString("email", usuario.getEmail());
                        editor.putString("contrasena", usuario.getContrasena());

                        editor.apply();
                    }

                    Intent intent = new Intent(LoginActivity.this, ListaPeliculasActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "No se ha encontrado el usuario", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
