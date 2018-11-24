package com.android.ermo.rakutentvapp.threads;

import android.os.Handler;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.LoginActivity;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceLogin {
    private final static Handler manejador = new Handler();
    private static String messageUser = null;
    private static ArrayList<Usuario> listaUsuarios = null;
    private static Thread threadLogin = null;

    public static void accionLogin(final String user, final String pass) {
        threadLogin = new Thread() {
            public void run() {
                JSONArray datos = null;
                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("Usuario", user);
                parametros.put("Contrasena", pass);
                // Llamada a Servidor Web PHP
                try {
                    Post post = new Post();
                    datos = post.getServerDataPost(parametros, "http://ofertasalbertoakkari.readyrunners.x10host.com/loginGroupon.php");
                    listaUsuarios = Usuario.getArrayListFromJSon(datos);
                } catch (Exception e) {
                    messageUser = "Error al conectar con el servidor. ";
                }
                manejador.post(proceso);
            }
        };
        threadLogin.start();
    }

    private final static Runnable proceso = new Runnable() {
        public void run() {
            try {
                if (listaUsuarios != null && listaUsuarios.size() > 0) {
                    Usuario usuario = listaUsuarios.get(0);
                    if (usuario.getIdUsuario() > 0) {
                        RakutenData.setUsuario(usuario);
                        Toast.makeText(LoginActivity.getInstance().getBaseContext(), "" +
                                "Usuario correcto. ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.getInstance().getBaseContext(), "" +
                            "Usuario incorrecto. ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.getInstance().getBaseContext(),
                            messageUser, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
        }
    };
}