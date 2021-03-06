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
import com.android.ermo.rakutentvapp.beans.Respuesta;
import com.android.ermo.rakutentvapp.beans.Usuario;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.android.ermo.rakutentvapp.tools.IPGetter;
import com.android.ermo.rakutentvapp.tools.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;

import java.util.ArrayList;
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
                    parametros.put("ACTION", "Puntuar.addPuntuacion");
                    parametros.put("ID_USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("ID_PELICULA", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    parametros.put("PUNTUACION", String.valueOf((int)(rating)));

                    TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

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
                    return false;
                }
            });
        }

        btnMarcarFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RakutenData.getUsuario() != null) {
                    HashMap<String, String> parametros = new HashMap<String, String>();
                    parametros.put("ACTION", "MarcarFav.marcarFavorito");
                    parametros.put("ID_USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("ID_PELICULA", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

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
                    parametros.put("ACTION", "MarcarFav.quitarFavorito");
                    parametros.put("ID_USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("ID_PELICULA", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

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
                    parametros.put("ACTION", "Comprar.comprar");
                    parametros.put("ID_USUARIO", String.valueOf(RakutenData.getUsuario().getIdUsuario()));
                    parametros.put("ID_PELICULA", String.valueOf(RakutenData.getPeliculaSeleccionada().getIdPelicula()));
                    parametros.put("PRECIO", String.valueOf(RakutenData.getPeliculaSeleccionada().getPrecioPeli()));
                    TareaSegundoPlano tarea = new TareaSegundoPlano(parametros);
                    tarea.execute("http://" + IP_LOCAL_SERVIDOR + ":8080/RakutenTV/Controller");

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
        private ArrayList<Respuesta> lstResp = null;

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
            lstResp = Respuesta.getArrayListFromJSon(result);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean && lstResp != null && lstResp.size() > 0) {
                Respuesta respuesta = lstResp.get(0);

                if(respuesta.getDescRespuesta().equals("addPuntuacion")){
                    if(respuesta.getRespuesta() == 1){
                        Toast.makeText(getBaseContext(), "Votación realizada", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "No se ha podido realizar la puntuación", Toast.LENGTH_SHORT).show();
                    }
                }else if (respuesta.getDescRespuesta().equals("modPuntuacion")){
                    if(respuesta.getRespuesta() == 1){
                        Toast.makeText(getBaseContext(), "Votación actualizada", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "No se ha podido actualizar la puntuación", Toast.LENGTH_SHORT).show();
                    }
                }else if(respuesta.getDescRespuesta().equals("addFavorito")){
                    if(respuesta.getRespuesta() == 1){
                        Toast.makeText(getBaseContext(), "Añadida a favoritos", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "No se ha podido añadir a favoritos", Toast.LENGTH_SHORT).show();
                    }
                }else if (respuesta.getDescRespuesta().equals("remFavorito")){
                    if(respuesta.getRespuesta() == 1){
                        Toast.makeText(getBaseContext(), "Quitado de favoritos", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "No se ha podido quitar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                }else if(respuesta.getDescRespuesta().equals("addCompra")){
                    if(respuesta.getRespuesta() == 1){
                        Toast.makeText(getBaseContext(), "Pelicula comprada", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "No se ha podido comprar la pelicula", Toast.LENGTH_SHORT).show();
                    }
                }else if (respuesta.getDescRespuesta().equals("yaCompra")){
                    if(respuesta.getRespuesta() == 1){
                        Toast.makeText(getBaseContext(), "La pelicula ya está comprada", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "La pelicula ya está comprada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


}
