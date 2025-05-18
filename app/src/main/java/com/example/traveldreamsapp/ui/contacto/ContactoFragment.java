package com.example.traveldreamsapp.ui.contacto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.traveldreamsapp.databinding.FragmentContactoBinding;

public class ContactoFragment extends Fragment {

    private FragmentContactoBinding binding;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextMessage;
    private Button buttonSubmit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar las vistas usando binding
        editTextName = binding.editTextName;
        editTextEmail = binding.editTextEmail;
        editTextMessage = binding.editTextMessage;
        buttonSubmit = binding.buttonSubmit;

        // Configurar el OnClickListener para el botón "Enviar"
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto de los campos
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String message = editTextMessage.getText().toString().trim();

                // Aquí puedes agregar la lógica para enviar el mensaje
                // Por ejemplo, podrías mostrar un Toast con los datos ingresados
                String mensaje = "Nombre: " + name + "\nEmail: " + email + "\nMensaje: " + message;
                Toast.makeText(getContext(), "Mensaje enviado (simulado):\n" + mensaje, Toast.LENGTH_SHORT).show();

                // Limpiar los campos después de "enviar"
                editTextName.setText("");
                editTextEmail.setText("");
                editTextMessage.setText("");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}