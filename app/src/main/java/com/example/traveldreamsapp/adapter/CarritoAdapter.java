package com.example.traveldreamsapp.adapter;

import android.content.Context;
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
import com.example.traveldreamsapp.models.Carrito;
import java.util.List;
import android.widget.ImageButton;
import java.util.Locale;


public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private final List<Carrito> carritoItems;
    private final Context context;
    private final OnCarritoActionListener listener;

    public interface OnCarritoActionListener {
        void onEliminarItem(int idCompra);
        void onActualizarCantidad(int idCompra, int nuevaCantidad);
        void onActualizarFecha(int idCompra, String nuevaFecha);
    }

    public CarritoAdapter(List<Carrito> carritoItems, Context context, OnCarritoActionListener listener) {
        this.carritoItems = carritoItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Carrito item = carritoItems.get(position);

        holder.tvNombreDestino.setText(item.getNombre_Destino());

        // Limpiar la hora y quedarse solo con la fecha
        String fechaLimpia = item.getFecha_salida();
        if (fechaLimpia != null && fechaLimpia.contains("T")) {
            fechaLimpia = fechaLimpia.split("T")[0];
        }
        holder.tvFechaSalida.setText(fechaLimpia);

        holder.tvPrecioUnitario.setText(String.format(Locale.US, "USD %.2f", item.getPrecio_Destino()));
        holder.tvSubTotal.setText(String.format(Locale.US, "USD %.2f", item.getSubTotal()));
        holder.tvCantidad.setText(String.valueOf(item.getCantidad()));

        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Glide.with(context).load(item.getImage()).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Listeners para los botones
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarItem(item.getId_compra());
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            int nuevaCantidad = item.getCantidad() - 1;
            if (nuevaCantidad > 0) {
                if (listener != null) {
                    listener.onActualizarCantidad(item.getId_compra(), nuevaCantidad);
                }
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int nuevaCantidad = item.getCantidad() + 1;
            if (listener != null) {
                listener.onActualizarCantidad(item.getId_compra(), nuevaCantidad);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carritoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView ivImage;
        public final TextView tvNombreDestino;
        public final TextView tvFechaSalida;
        public final TextView tvPrecioUnitario;
        public final TextView tvSubTotal;
        public final TextView tvCantidad;
        public final Button btnMinus;
        public final Button btnPlus;
        public final ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvNombreDestino = itemView.findViewById(R.id.tv_nombre_Destino);
            tvFechaSalida = itemView.findViewById(R.id.tv_fecha_salida);
            tvPrecioUnitario = itemView.findViewById(R.id.tv_precio);
            tvSubTotal = itemView.findViewById(R.id.txt_precio_sub_total);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}
