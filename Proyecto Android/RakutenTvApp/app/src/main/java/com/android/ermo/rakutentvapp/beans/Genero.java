package com.android.ermo.rakutentvapp.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Genero {

    private int idGenero;
    private String descGenero;
    private String fotoGenero;

    public Genero() {
    }

    public Genero(int idGenero, String descGenero, String fotoGenero) {
        this.idGenero = idGenero;
        this.descGenero = descGenero;
        this.fotoGenero = fotoGenero;
    }

    public int getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }

    public String getDescGenero() {
        return descGenero;
    }

    public void setDescGenero(String descGenero) {
        this.descGenero = descGenero;
    }

    public String getFotoGenero() {
        return fotoGenero;
    }

    public void setFotoGenero(String fotoGenero) {
        this.fotoGenero = fotoGenero;
    }

    public static ArrayList<Genero> getArrayListFromJSon(JSONArray datos) {
        ArrayList<Genero> lista = null;
        Genero genero = null;
        try {
            if (datos != null && datos.length() > 0) {
                lista = new ArrayList<Genero>();
            }
            for (int i = 0; i < datos.length(); i++) {
                JSONObject json_data = datos.getJSONObject(i);
                genero = new Genero();
                genero.setIdGenero(json_data.getInt("idGenero"));
                genero.setDescGenero(json_data.getString("descGenero"));
                lista.add(genero);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lista;

    }
}
