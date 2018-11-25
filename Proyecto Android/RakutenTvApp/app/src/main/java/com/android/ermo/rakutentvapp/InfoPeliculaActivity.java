package com.android.ermo.rakutentvapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.ermo.rakutentvapp.beans.Pelicula;

public class InfoPeliculaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pelicula);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Pelicula pelicula = null;
        if(getIntent().hasExtra("pelicula")) {
            pelicula = (Pelicula) getIntent().getExtras().getSerializable("pelicula");
        }


    }
}
