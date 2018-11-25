package com.android.ermo.rakutentvapp.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ermo.rakutentvapp.R;
import com.android.ermo.rakutentvapp.beans.Pelicula;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdaptadorPeliculas extends RecyclerView.Adapter<RecyclerAdaptadorPeliculas.RecyclerViewHolder> {

    private Context context;
    private ArrayList<Pelicula> items;

    public RecyclerAdaptadorPeliculas(Context context, ArrayList<Pelicula> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {

        Pelicula pelicula = items.get(position);
        holder.titulo.setText(pelicula.getTituloPeli());
        holder.mediaPuntuaciones.setText(String.valueOf(pelicula.getMediaValoraciones()));
        holder.vecesPuntuado.setText(String.valueOf(pelicula.getValoracionesTotales()));

        Glide.with(context).asBitmap().load(pelicula.getCaratulaPeli()).into(holder.caratula);

        holder.lineaerLayoutPadre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RakutenData.setPeliculaSeleccionada(items.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView caratula;
        TextView titulo;
        TextView mediaPuntuaciones;
        TextView vecesPuntuado;
        LinearLayout lineaerLayoutPadre;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            caratula = itemView.findViewById(R.id.caratula);
            titulo = itemView.findViewById(R.id.titulo);
            mediaPuntuaciones = itemView.findViewById(R.id.mediaPuntuaciones);
            vecesPuntuado = itemView.findViewById(R.id.vecesPuntuado);
            lineaerLayoutPadre = itemView.findViewById(R.id.linearLayoutPadre);
        }
    }


}
