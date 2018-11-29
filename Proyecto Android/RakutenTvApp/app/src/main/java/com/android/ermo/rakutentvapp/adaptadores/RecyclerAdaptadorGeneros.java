package com.android.ermo.rakutentvapp.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ermo.rakutentvapp.FiltroActivity;
import com.android.ermo.rakutentvapp.InfoPeliculaActivity;
import com.android.ermo.rakutentvapp.R;
import com.android.ermo.rakutentvapp.beans.Genero;
import com.android.ermo.rakutentvapp.datos.RakutenData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdaptadorGeneros extends RecyclerView.Adapter<RecyclerAdaptadorGeneros.RecyclerViewHolder> {

    private Context context;
    private ArrayList<Genero> items;

    public RecyclerAdaptadorGeneros(Context context, ArrayList<Genero> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (RakutenData.isListView()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_grid, parent, false);
        }
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {

        final Genero genero = items.get(position);
        holder.titulo.setText(genero.getDescGenero());

        Glide.with(context).asBitmap().load(genero.getFotoGenero()).into(holder.caratula);

        holder.lineaerLayoutPadre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView caratula;
        ImageView imageEstrellas;
        TextView titulo;
        TextView mediaPuntuaciones;
        TextView vecesPuntuado;
        TextView precio;
        TextView anio;
        LinearLayout lineaerLayoutPadre;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            caratula = itemView.findViewById(R.id.caratula);
            titulo = itemView.findViewById(R.id.titulo);

            imageEstrellas = itemView.findViewById(R.id.imageEstrellas);
            imageEstrellas.setVisibility(View.GONE);

            mediaPuntuaciones = itemView.findViewById(R.id.mediaPuntuaciones);
            mediaPuntuaciones.setVisibility(View.GONE);

            vecesPuntuado = itemView.findViewById(R.id.vecesPuntuado);
            vecesPuntuado.setVisibility(View.GONE);

            precio = itemView.findViewById(R.id.precio);
            precio.setVisibility(View.GONE);

            anio = itemView.findViewById(R.id.anio);
            anio.setVisibility(View.GONE);

            lineaerLayoutPadre = itemView.findViewById(R.id.linearLayoutPadre);
        }
    }


}
