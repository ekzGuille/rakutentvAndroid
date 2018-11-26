package com.android.ermo.rakutentvapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class InfoPeliculaActivity extends AppCompatActivity {

    LinearLayout layoutImgFondo;
    ImageView caratula;
    TextView tituloPeli;
    TextView resumenPeli;
    TextView anioEstreno;
    TextView mediaPuntuaciones;
    TextView vecesPuntuado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pelicula);

        layoutImgFondo = (LinearLayout) findViewById(R.id.imagenFondo);
        caratula = (ImageView) findViewById(R.id.caratula);
        tituloPeli = (TextView) findViewById(R.id.tituloPeli);
        resumenPeli = (TextView) findViewById(R.id.resumenPeli);
        anioEstreno = (TextView) findViewById(R.id.anioEstreno);
        mediaPuntuaciones = (TextView) findViewById(R.id.mediaPuntuaciones);
        vecesPuntuado = (TextView) findViewById(R.id.vecesPuntuado);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Pelicula pelicula = null;
        if (getIntent().hasExtra("pelicula")) {
            Pelicula pelicula = (Pelicula) getIntent().getExtras().getSerializable("pelicula");


            Glide.with(this).asBitmap().load(pelicula.getCaratulaPeli()).into(caratula);

            Glide.with(this).load(pelicula.getFotoPeli()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    layoutImgFondo.setBackground(resource);
                }
            });

            tituloPeli.setText(pelicula.getTituloPeli());
            resumenPeli.setText(pelicula.getResumenPeli());
            anioEstreno.setText("(" + pelicula.getFechaEstreno().substring(pelicula.getFechaEstreno().length() - 4, pelicula.getFechaEstreno().length()) + ")");
            vecesPuntuado.setText(String.valueOf(pelicula.getValoracionesTotales()));
            mediaPuntuaciones.setText(String.valueOf(pelicula.getMediaValoraciones()));

            getSupportActionBar().setTitle(pelicula.getTituloPeli());


        }


    }
}
