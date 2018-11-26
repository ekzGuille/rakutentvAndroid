package com.android.ermo.rakutentvapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;

import java.util.HashMap;

public class InfoPeliculaActivity extends AppCompatActivity {

    LinearLayout layoutImgFondo;
    ImageView caratula;
    TextView tituloPeli;
    TextView resumenPeli;
    TextView anioEstreno;
    TextView mediaPuntuaciones;
    TextView vecesPuntuado;
    TextView precio;
    Button btnMarcarFav;
    Button btnVerTrailer;
    Button btnComprar;
    RatingBar ratingBar;

    private final String IP_LOCAL_SERVIDOR = IPGetter.getInstance().getIP();


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
        precio = (TextView) findViewById(R.id.precio);
        btnMarcarFav = (Button) findViewById(R.id.btnMarcarFav);
        btnVerTrailer = (Button) findViewById(R.id.btnVerTrailer);
        btnComprar = (Button) findViewById(R.id.btnComprar);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);


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
            precio.setText(String.valueOf(pelicula.getPrecioPeli())+" €");

            getSupportActionBar().setTitle(pelicula.getTituloPeli());

        }

        if (RakutenData.getUsuario() != null) {
            ratingBar.setIsIndicator(false);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("ACTION", "Pelicula.puntuar");
                    parametros.put("usuario", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("pelicula", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    parametros.put("puntuacion", String.valueOf(rating));
                    //TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    //tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

                    //Hacerlo en el onPostExecute
                    Toast.makeText(getBaseContext(), "Votación realizada", Toast.LENGTH_SHORT).show();

                    //ratingBar.setIsIndicator(true);
                }
            });

//            if (ratingBar.isIndicator()) {
//                ratingBar.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        Toast.makeText(getBaseContext(), "Puedes volver a votar", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//                ratingBar.setIsIndicator(false);
//            }

        } else {
            ratingBar.setIsIndicator(true);
            ratingBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(getBaseContext(), "No puedes votar en modo invitado", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        btnMarcarFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RakutenData.getUsuario() != null) {
                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("ACTION", "Pelicula.marcarFavorito");
                    parametros.put("usuario", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("pelicula", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    //TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    //tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

                    //Hacerlo en el onPostExecute
                    Toast.makeText(getBaseContext(), "Pelicula añadida a favoritos", Toast.LENGTH_SHORT).show();
                    btnMarcarFav.setBackgroundResource(R.drawable.corazon_on);
                } else {
                    Toast.makeText(getBaseContext(), "No puedes añadir a favoritos en modo invitado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMarcarFav.setOnLongClickListener(new View.OnLongClickListener()

        {
            @Override
            public boolean onLongClick(View v) {

                if (RakutenData.getUsuario() != null) {
                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("ACTION", "Pelicula.quitarFavorito");
                    parametros.put("usuario", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("pelicula", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    //TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    //tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

                    //Hacerlo en el onPostExecute
                    Toast.makeText(getBaseContext(), "Pelicula retirada de favoritos", Toast.LENGTH_SHORT).show();
                    btnMarcarFav.setBackgroundResource(R.drawable.corazon_off);
                }
                return true;
            }
        });


        btnVerTrailer.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intentYoutube = new Intent();
                intentYoutube.setAction(Intent.ACTION_VIEW);
                intentYoutube.setData(Uri.parse(RakutenData.getPeliculaSeleccionada().getTrailer()));

                startActivity(intentYoutube);
            }
        });

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RakutenData.getUsuario() != null) {
                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("ACTION", "Pelicula.comprar");
                    parametros.put("usuario", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("pelicula", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    //TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    //tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

                    //Hacerlo en el onPostExecute
                    Toast.makeText(getBaseContext(), "Pelicula comprarda", Toast.LENGTH_SHORT).show();
                    btnComprar.setBackgroundResource(R.drawable.carro_on);
                } else {
                    Toast.makeText(getBaseContext(), "No puedes comprar en modo invitado", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class TareaSegundoPlano extends AsyncTask<String, Integer, Boolean> {
        private HashMap<String, String> parametros;

        public TareaSegundoPlano(HashMap<String, String> parametros) {
            this.parametros = parametros;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            // Importantísimo la clase "POST":
            // Hacer una petición al servidor y recuperar la respuesta en JSON.
            Post post = new Post();
            JSONArray result = post.getServerDataPost(parametros, url);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getBaseContext(), "Votación realizada", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
