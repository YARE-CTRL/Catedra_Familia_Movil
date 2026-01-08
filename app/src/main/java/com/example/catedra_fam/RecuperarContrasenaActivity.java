package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    private ImageButton btnVolver;
    private TextInputEditText etCorreoTelefono;
    private MaterialButton btnEnviarCodigo;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        btnVolver = findViewById(R.id.btn_volver);
        etCorreoTelefono = findViewById(R.id.et_correo_telefono);
        btnEnviarCodigo = findViewById(R.id.btn_enviar_codigo);
        pbLoading = findViewById(R.id.pb_loading);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());

        btnEnviarCodigo.setOnClickListener(v -> intentarEnviarCodigo());
    }

    private void intentarEnviarCodigo() {
        String correoTelefono = etCorreoTelefono.getText().toString().trim();

        // Validar campo
        if (TextUtils.isEmpty(correoTelefono)) {
            etCorreoTelefono.setError(getString(R.string.campo_vacio));
            etCorreoTelefono.requestFocus();
            return;
        }

        // Validar formato (email o teléfono)
        boolean esEmail = Patterns.EMAIL_ADDRESS.matcher(correoTelefono).matches();
        boolean esTelefono = correoTelefono.matches("\\d{10}"); // 10 dígitos

        if (!esEmail && !esTelefono) {
            etCorreoTelefono.setError("Ingresa un correo o teléfono válido");
            etCorreoTelefono.requestFocus();
            return;
        }

        // Mostrar loading
        mostrarLoading(true);

        // Simular envío de código (2 segundos)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            envioExitoso(correoTelefono);
        }, 2000);
    }

    private void envioExitoso(String correoTelefono) {
        mostrarLoading(false);

        // Ocultar parte del correo/teléfono
        String correoOculto = ocultarCorreo(correoTelefono);

        Toast.makeText(this, "Código enviado exitosamente", Toast.LENGTH_SHORT).show();

        // Ir a verificar código
        Intent intent = new Intent(RecuperarContrasenaActivity.this, VerificarCodigoActivity.class);
        intent.putExtra("CORREO_TELEFONO", correoTelefono);
        intent.putExtra("CORREO_OCULTO", correoOculto);
        startActivity(intent);
    }

    private String ocultarCorreo(String correoTelefono) {
        if (correoTelefono.contains("@")) {
            // Es email
            String[] partes = correoTelefono.split("@");
            String usuario = partes[0];
            String dominio = partes[1];

            if (usuario.length() <= 4) {
                return usuario.charAt(0) + "***@" + dominio;
            } else {
                return usuario.substring(0, 4) + "***@" + dominio;
            }
        } else {
            // Es teléfono
            return correoTelefono.substring(0, 3) + " *** " +
                   correoTelefono.substring(correoTelefono.length() - 2);
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnEnviarCodigo.setEnabled(false);
            btnEnviarCodigo.setText(R.string.enviando);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnEnviarCodigo.setEnabled(true);
            btnEnviarCodigo.setText(R.string.btn_enviar_codigo);
        }
    }
}

