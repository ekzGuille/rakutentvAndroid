package com.android.ermo.rakutentvapp.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Respuesta {
    private String descRespuesta;
    private int respuesta;

    public Respuesta() {
    }

    public String getDescRespuesta() {
        return descRespuesta;
    }

    public void setDescRespuesta(String descRespuesta) {
        this.descRespuesta = descRespuesta;
    }

    public int getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(int respuesta) {
        this.respuesta = respuesta;
    }

    public Respuesta(String descRespuesta, int respuesta) {
        this.descRespuesta = descRespuesta;
        this.respuesta = respuesta;
    }

    public static ArrayList<Respuesta> getArrayListFromJSon(JSONArray datos) {
        ArrayList<Respuesta> lst = null;
        Respuesta resp = null;
        try {
            if (datos != null && datos.length() > 0) {
                lst = new ArrayList<>();
            }
            for (int i = 0; i < datos.length(); i++) {
                JSONObject json_data = datos.getJSONObject(i);
                resp = new Respuesta();
                resp.setRespuesta(json_data.getInt("respuesta"));
                resp.setDescRespuesta(json_data.getString("descRespuesta"));
                lst.add(resp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lst;
    }
}
