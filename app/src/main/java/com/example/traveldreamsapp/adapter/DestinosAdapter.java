package com.example.traveldreamsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.models.Destinos;

import java.util.List;

public class DestinosAdapter extends RecyclerView.Adapter<DestinosAdapter.ViewHolder> {

    private final List<Destinos> destinos;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Destinos destino);
    }

    public DestinosAdapter(List<Destinos> destinos, Context context, OnItemClickListener listener) {
        this.destinos = destinos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destino, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destinos destino = destinos.get(position);

        Log.d("DEBUG_DESTINO", "Nombre: " + destino.getNombre_Destino()
                + " | Fecha: " + destino.getFecha_salida()
                + " | Cupos: " + destino.getCantidad_Disponible()
                + " | Precio: " + destino.getPrecio_Destino());

        holder.tv_nombre_Destino.setText(destino.getNombre_Destino());

        String fechaLimpia = destino.getFecha_salida();
        if (fechaLimpia != null && fechaLimpia.contains("T")) {
            fechaLimpia = fechaLimpia.split("T")[0];
        }
        holder.tv_fecha_salida.setText(String.format("Fecha de Salida: %s", fechaLimpia));

        holder.tv_cupos.setText(String.format("Cupos: %d", destino.getCantidad_Disponible()));
        holder.tv_precio.setText(String.format("USD %.2f", destino.getPrecio_Destino()));

        if (destino.getImage() != null && !destino.getImage().isEmpty()) {
            Glide.with(context).load(destino.getImage()).into(holder.iv_image);
        } else {
            holder.iv_image.setImageResource(R.drawable.ic_launcher_background);
        }

        // Mostrar "AGOTADO" si no hay cupos
        if (destino.getCantidad_Disponible() == 0) {
            holder.btn_comprar.setEnabled(false);
            holder.btn_comprar.setText("AGOTADO");
            holder.btn_comprar.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.btn_comprar.setAlpha(0.6f); // Más visual
        }

        // Clicks
        holder.btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && destino.getCantidad_Disponible() > 0) {
                    listener.onItemClick(destino);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && destino.getCantidad_Disponible() > 0) {
                    listener.onItemClick(destino);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button btn_comprar;
        private final ImageView iv_image;
        private final TextView tv_nombre_Destino;
        private final TextView tv_fecha_salida;
        private final TextView tv_cupos;
        private final TextView tv_precio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_nombre_Destino = itemView.findViewById(R.id.tv_nombre_Destino);
            tv_fecha_salida = itemView.findViewById(R.id.tv_fecha_salida);
            tv_cupos = itemView.findViewById(R.id.tv_cupos);
            tv_precio = itemView.findViewById(R.id.tv_precio);
            btn_comprar = itemView.findViewById(R.id.btn_comprar);
        }
    }
}
