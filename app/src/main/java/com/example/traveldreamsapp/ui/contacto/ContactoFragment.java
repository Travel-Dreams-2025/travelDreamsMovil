package com.example.traveldreamsapp.ui.contacto;

import android.os.Bundle;
import android.text.TextUtils; // Importar TextUtils para isEmpty y isEmailValid
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

    // Límites de longitud para los campos de texto
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 254; // Longitud máxima estándar para emails
    private static final int MAX_MESSAGE_LENGTH = 1000; // Puedes ajustar este valor

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editTextName = binding.editTextName;
        editTextEmail = binding.editTextEmail;
        editTextMessage = binding.editTextMessage;
        buttonSubmit = binding.buttonSubmit;

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener y sanitizar los datos de entrada
                String name = sanitizeInput(editTextName.getText().toString().trim());
                String email = sanitizeInput(editTextEmail.getText().toString().trim());
                String message = sanitizeInput(editTextMessage.getText().toString().trim());

                boolean isValid = true;

                // Limpiar errores previos
                editTextName.setError(null);
                editTextEmail.setError(null);
                editTextMessage.setError(null);

                // --- Validaciones ---

                // Validación de nombre: no vacío y longitud máxima
                if (TextUtils.isEmpty(name)) {
                    editTextName.setError("El nombre es obligatorio");
                    isValid = false;
                } else if (name.length() > MAX_NAME_LENGTH) {
                    editTextName.setError("El nombre es demasiado largo (máx. " + MAX_NAME_LENGTH + " caracteres)");
                    isValid = false;
                }

                // Validación de email: no vacío, formato válido y longitud máxima
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("El correo electrónico es obligatorio");
                    isValid = false;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Introduce un correo electrónico válido");
                    isValid = false;
                } else if (email.length() > MAX_EMAIL_LENGTH) {
                    editTextEmail.setError("El correo electrónico es demasiado largo (máx. " + MAX_EMAIL_LENGTH + " caracteres)");
                    isValid = false;
                }

                // Validación de mensaje: no vacío y longitud máxima
                if (TextUtils.isEmpty(message)) {
                    editTextMessage.setError("El mensaje es obligatorio");
                    isValid = false;
                } else if (message.length() > MAX_MESSAGE_LENGTH) {
                    editTextMessage.setError("El mensaje es demasiado largo (máx. " + MAX_MESSAGE_LENGTH + " caracteres)");
                    isValid = false;
                }

                // --- Lógica de Envío ---

                if (isValid) {
                    // Si todos los campos son válidos, procesa el envío del mensaje.
                    // Aquí va la lógica  para enviar los datos .
                    // la validación mas robusta va en el servidor.**

                    // Simulación de envío
                    String mensajeConfirmacion = "Mensaje enviado (simulado):\n" +
                            "Nombre: " + name + "\n" +
                            "Email: " + email + "\n" +
                            "Mensaje: " + message;
                    Toast.makeText(getContext(), mensajeConfirmacion, Toast.LENGTH_LONG).show();

                    // Limpiar campos después de un envío exitoso
                    editTextName.setText("");
                    editTextEmail.setText("");
                    editTextMessage.setText("");
                } else {
                    // Si hay errores, notifica al usuario
                    Toast.makeText(getContext(), "Por favor, corrige los errores en el formulario.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    /**
     * Método básico para sanitizar la entrada de texto y mitigar ataques XSS.
     * Reemplaza caracteres especiales con sus entidades HTML correspondientes.
     * La sanitización robusta se hace en el backend.
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Libera la referencia al binding
    }
}