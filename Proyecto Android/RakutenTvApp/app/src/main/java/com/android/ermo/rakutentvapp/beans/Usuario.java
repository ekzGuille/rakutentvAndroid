package com.android.ermo.rakutentvapp.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {

    private int idUsuario;
    private String username;
    private String email;
    private String contrasena;

    private final static String ID_USUARIO = "idUsuario";
    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String CONTRASENA = "contrasena";

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setPass(String pass) {
        this.contrasena = pass;
    }

    public static ArrayList<Usuario> getArrayListFromJSon(JSONArray datos) {
        ArrayList<Usuario> lista = null;
        Usuario usuario = null;
        try {
            if (datos != null && datos.length() > 0) {
                lista = new ArrayList<Usuario>();
            }
            for (int i = 0; i < datos.length(); i++) {
                JSONObject json_data = datos.getJSONObject(i);
                usuario = new Usuario();
                usuario.setIdUsuario(json_data.getInt(ID_USUARIO));
                usuario.setUsername(json_data.getString(USERNAME));
                usuario.setEmail(json_data.getString(EMAIL));
                usuario.setPass(json_data.getString(CONTRASENA));
                lista.add(usuario);
            }
        } catch (JSONException e) {
            //TODO ARREGLAR
            e.printStackTrace();
        }
        return lista;

    }

}
