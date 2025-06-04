package com.example.traveldreamsapp.ui.destinos.detalles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.traveldreamsapp.R;
import com.example.traveldreamsapp.models.Destinos;

public class DestinosDetallesFragment extends Fragment {

    private ImageView destDetImg;
    private TextView destDetTxtTitulo;
    private TextView destDetTxtDescripcion;
    private TextView destDetFechaSalida;
    private TextView destDetTxtPrecioUnit;
    private TextView destDetTxtCantidad;
    private Button destDetBtnRestar;
    private Button destDetBtnSumar;
    private TextView destDetTxtCupos;
    private TextView destDetTxtTotal;
    private Button destDetBtnAgregarCarrito;

    private Destinos currentDestino;
    private int cantidadSeleccionada = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_destinos_detalles, container, false);

        destDetImg = root.findViewById(R.id.destDetImg);
        destDetTxtTitulo = root.findViewById(R.id.destDetTxtTitulo);
        destDetTxtDescripcion = root.findViewById(R.id.destDetTxtDescripcion);
        destDetFechaSalida = root.findViewById(R.id.destDetFechaSalida);
        destDetTxtPrecioUnit = root.findViewById(R.id.destDetTxtPrecioUnit);
        destDetTxtCantidad = root.findViewById(R.id.destDetTxtCantidad);
        destDetBtnRestar = root.findViewById(R.id.destDetBtnRestar);
        destDetBtnSumar = root.findViewById(R.id.destDetBtnSumar);
        destDetTxtCupos = root.findViewById(R.id.destDetTxtCupos);
        destDetTxtTotal = root.findViewById(R.id.destDetTxtTotal);
        destDetBtnAgregarCarrito = root.findViewById(R.id.destDetBtnAgregarCarrito);

        if (getArguments() != null) {
            currentDestino = getArguments().getParcelable("destino_seleccionado");
            if (currentDestino != null) {
                displayDestinoDetails(currentDestino);
                setupQuantityControls();
            }
        }

        destDetBtnAgregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDestino != null) {
                    Toast.makeText(getContext(), "Agregado " + cantidadSeleccionada + " de " + currentDestino.getNombre_Destino() + " al carrito.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No se pudo agregar al carrito. Destino no válido.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void displayDestinoDetails(Destinos destino) {
        destDetTxtTitulo.setText(destino.getNombre_Destino());
        destDetTxtDescripcion.setText(destino.getDescripcion());
        destDetFechaSalida.setText("Fecha de Salida: " + destino.getFecha_salida());
        destDetTxtPrecioUnit.setText(String.format("Precio por persona: $%.2f", destino.getPrecio_Destino()));
        destDetTxtCupos.setText("Cupos disponibles: " + destino.getCantidad_Disponible());

        if (destino.getImage() != null && !destino.getImage().isEmpty()) {
            Glide.with(this)
                    .load(destino.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(destDetImg);
        } else {
            destDetImg.setImageResource(R.drawable.ic_launcher_background);
        }

        updateTotal();
    }

    private void setupQuantityControls() {
        destDetTxtCantidad.setText(String.valueOf(cantidadSeleccionada));

        destDetBtnRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadSeleccionada > 1) {
                    cantidadSeleccionada--;
                    destDetTxtCantidad.setText(String.valueOf(cantidadSeleccionada));
                    updateTotal();
                }
            }
        });

        destDetBtnSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDestino != null && cantidadSeleccionada < currentDestino.getCantidad_Disponible()) {
                    cantidadSeleccionada++;
                    destDetTxtCantidad.setText(String.valueOf(cantidadSeleccionada));
                    updateTotal();
                } else if (currentDestino != null) {
                    Toast.makeText(getContext(), "No hay más cupos disponibles.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotal() {
        if (currentDestino != null) {
            double total = cantidadSeleccionada * currentDestino.getPrecio_Destino();
            destDetTxtTotal.setText(String.format("Total: $%.2f", total));
        }
    }
}