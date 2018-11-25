package com.android.ermo.rakutentvapp.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Pelicula {

    private int idPelicula;
    private String tituloPeli;
    private String resumenPeli;
    private String caratulaPeli;
    private String fechaEstreno;
    private int duracionPeli;
    private double precioPeli;
    private int valoracionesTotales;
    private double mediaValoraciones;


    private final static String ID_PELICULA = "idPelicula";
    private final static String TITULO = "tituloPeli";
    private final static String RESUMEN = "resumenPeli";
    private final static String FOTO = "caratulaPeli";
    private final static String FECHA = "fechaEstreno";
    private final static String DURACION = "duracionPeli";
    private final static String PRECIO = "precioPeli";
    private final static String VALORACIONES_TOTALES = "valoracionesTotales";
    private final static String MEDIA_VALORACIONES = "mediaValoraciones";

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTituloPeli() {
        return tituloPeli;
    }

    public void setTituloPeli(String tituloPeli) {
        this.tituloPeli = tituloPeli;
    }

    public String getResumenPeli() {
        return resumenPeli;
    }

    public void setResumenPeli(String resumenPeli) {
        this.resumenPeli = resumenPeli;
    }

    public String getCaratulaPeli() {
        return caratulaPeli;
    }

    public void setCaratulaPeli(String caratulaPeli) {
        this.caratulaPeli = caratulaPeli;
    }

    public String getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(String fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public int getDuracionPeli() {
        return duracionPeli;
    }

    public void setDuracionPeli(int duracionPeli) {
        this.duracionPeli = duracionPeli;
    }

    public double getPrecioPeli() {
        return precioPeli;
    }

    public void setPrecioPeli(double precioPeli) {
        this.precioPeli = precioPeli;
    }

    public int getValoracionesTotales() {
        return valoracionesTotales;
    }

    public void setValoracionesTotales(int valoracionesTotales) {
        this.valoracionesTotales = valoracionesTotales;
    }

    public double getMediaValoraciones() {
        return mediaValoraciones;
    }

    public void setMediaValoraciones(double mediaValoraciones) {
        this.mediaValoraciones = mediaValoraciones;
    }

    public static ArrayList<Pelicula> getArrayListFromJSon(JSONArray datos) {
        ArrayList<Pelicula> lista = null;
        Pelicula pelicula = null;
        try {
            if (datos != null && datos.length() > 0) {
                lista = new ArrayList<Pelicula>();
            }
            for (int i = 0; i < datos.length(); i++) {
                JSONObject json_data = datos.getJSONObject(i);
                pelicula = new Pelicula();
                pelicula.setIdPelicula(json_data.getInt(ID_PELICULA));
                pelicula.setTituloPeli(
                        json_data.getString(TITULO));
                pelicula.setResumenPeli(json_data.getString(RESUMEN));
                pelicula.setCaratulaPeli(json_data.getString(FOTO));
                pelicula.setFechaEstreno(json_data.getString(FECHA));
                pelicula.setDuracionPeli(json_data.getInt(DURACION));
                pelicula.setPrecioPeli(json_data.getDouble(PRECIO));
                pelicula.setValoracionesTotales(json_data.getInt(VALORACIONES_TOTALES));
                pelicula.setMediaValoraciones(json_data.getDouble(MEDIA_VALORACIONES));
                lista.add(pelicula);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lista;

    }
}
