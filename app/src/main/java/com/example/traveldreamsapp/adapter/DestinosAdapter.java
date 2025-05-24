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

import com.bumptech.glide.Glide; //Permite mostrar imagenes desde URLs
import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.models.Destinos;

import java.util.List;

public class DestinosAdapter extends RecyclerView.Adapter<DestinosAdapter.ViewHolder> {

    private final List<Destinos> destinos;
    private final Context context;

    public DestinosAdapter(List<Destinos> destinos, Context context) {
        this.destinos = destinos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destino, parent, false);
        return new ViewHolder(view);
    }

    //@Override
    //public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    //    holder.tv_nombre_Destino.setText(destinos.get(position).getNombre_Destino());
    //    holder.tv_fecha_salida.setText(String.format("Fecha de Salida: %s", destinos.get(position).getFecha_salida()));
    //    holder.tv_cupos.setText(String.format("Cupos: %d", destinos.get(position).getCantidad_Disponible()));
    //    holder.tv_precio.setText(String.format("USD %.2f", destinos.get(position).getPrecio_Destino()));
    //    Glide.with(context).load(destinos.get(position).getImage()).into(holder.iv_image);
    //}

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destinos destino = destinos.get(position); // Sacamos esto una vez y lo reutilizamos

        // 🧠 Log para ver qué datos llegan realmente
        Log.d("DEBUG_DESTINO", "Nombre: " + destino.getNombre_Destino()
                + " | Fecha: " + destino.getFecha_salida()
                + " | Cupos: " + destino.getCantidad_Disponible()
                + " | Precio: " + destino.getPrecio_Destino());

        // Mostramos los datos
        holder.tv_nombre_Destino.setText(destino.getNombre_Destino());

        // Opcional: limpiar la hora y quedarte solo con la fecha si querés
        String fechaLimpia = destino.getFecha_salida().split("T")[0];
        holder.tv_fecha_salida.setText(String.format("Fecha de Salida: %s", fechaLimpia));

        holder.tv_cupos.setText(String.format("Cupos: %d", destino.getCantidad_Disponible()));
        holder.tv_precio.setText(String.format("USD %.2f", destino.getPrecio_Destino()));
        Glide.with(context).load(destino.getImage()).into(holder.iv_image);
    }




    @Override
    public int getItemCount() {
        return destinos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button btn_comprar;
        private final ImageView iv_image;
        private final TextView tv_nombre_Destino;
        private final TextView tv_fecha_salida; // Nuevo campo
        private final TextView tv_cupos; // Nuevo campo
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