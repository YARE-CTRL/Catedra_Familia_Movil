package com.example.catedra_fam;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SoporteActivity extends AppCompatActivity {

    private ImageButton btnVolver;
    private MaterialButton btnEmail, btnWhatsApp;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        initViews();
        setupListeners();
        mostrarVersion();
    }

    private void initViews() {
        btnVolver = findViewById(R.id.btn_volver);
        btnEmail = findViewById(R.id.btn_email);
        btnWhatsApp = findViewById(R.id.btn_whatsapp);
        tvVersion = findViewById(R.id.tv_version);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());

        btnEmail.setOnClickListener(v -> abrirEmail());

        btnWhatsApp.setOnClickListener(v -> abrirWhatsApp());
    }

    private void abrirEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getString(R.string.email_soporte)));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Soporte - Cátedra de Familia");
        intent.putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con:");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No tienes aplicación de correo instalada", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirWhatsApp() {
        String telefono = "573107392818"; // Formato internacional
        String mensaje = "Hola, necesito ayuda con la app Cátedra de Familia";

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/" + telefono + "?text=" +
                Uri.encode(mensaje)));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No tienes WhatsApp instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText(getString(R.string.version_app, version));
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setText(getString(R.string.version_app, "1.0.0"));
        }
    }
}

