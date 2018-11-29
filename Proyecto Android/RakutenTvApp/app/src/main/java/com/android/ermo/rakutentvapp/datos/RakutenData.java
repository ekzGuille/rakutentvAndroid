package com.android.ermo.rakutentvapp.datos;


import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.beans.Usuario;

import java.util.ArrayList;


public class RakutenData {
    private static Usuario usuario;
    private static ArrayList<Pelicula> lstPelicula;
    private static Pelicula peliculaSeleccionada;
    private static boolean listView;

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        RakutenData.usuario = usuario;
    }

    public static ArrayList<Pelicula> getLstPelicula() {
        return lstPelicula;
    }

    public static void setLstPelicula(ArrayList<Pelicula> lstPelicula) {
        RakutenData.lstPelicula = lstPelicula;
    }

    public static Pelicula getPeliculaSeleccionada() {
        return peliculaSeleccionada;
    }

    public static void setPeliculaSeleccionada(Pelicula peliculaSeleccionada) {
        RakutenData.peliculaSeleccionada = peliculaSeleccionada;
    }

    public static boolean isListView() {
        return listView;
    }

    public static void setListView(boolean listView) {
        RakutenData.listView = listView;
    }
}
