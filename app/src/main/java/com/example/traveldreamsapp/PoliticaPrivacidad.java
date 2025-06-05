package com.example.traveldreamsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Importación necesaria
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat; // Importación necesaria

public class PoliticaPrivacidad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidad);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView privacyPolicyTextView = findViewById(R.id.privacy_policy_content);

        String htmlString = getString(R.string.txtprivacity);
        CharSequence formattedText = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_LEGACY);
        privacyPolicyTextView.setText(formattedText);

        Button btnVolverRegistro = findViewById(R.id.btnBackReg);
        btnVolverRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PoliticaPrivacidad.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}