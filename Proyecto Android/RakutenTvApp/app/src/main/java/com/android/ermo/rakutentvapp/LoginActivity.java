package com.android.ermo.rakutentvapp;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    private EditText edtPass;
    private Button btnLogin;

    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();

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
        edtPass = (EditText) findViewById(R.id.edtPass);


        btnLogin = (Button) findViewById(R.id.btnEnviar);
        btnLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                /*ServiceLogin.accionLogin(edtEmail.getText().toString(), edtPass.getText().toString());*/
                HashMap<String, String> parametros = new HashMap<String, String>();
                // CLAVE------VALOR
                parametros.put("ACTION", "User.login");
                parametros.put("USER", edtEmail.getText().toString());
                parametros.put("PASS", edtPass.getText().toString());

                TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/AndroidAsynktaskBack/Controller");
            }
        });
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
//                    Toast.makeText(LoginActivity.this, "Usuario Correcto!!" + "\nId=" + cliente.getIdUsuario() + "\nEmail=" + cliente.getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, ListaOfertasActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "No se ha encontrado el usuario", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
