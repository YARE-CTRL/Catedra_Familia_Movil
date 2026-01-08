package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class NuevaContrasenaActivity extends AppCompatActivity {

    private TextInputEditText etNuevaContrasena, etConfirmarContrasena;
    private MaterialButton btnGuardar;
    private ProgressBar pbLoading;
    private TextView tvCheck8chars, tvCheckMayuscula, tvCheckNumero, tvCheckSimbolo;

    private String correoTelefono;

    private static final Pattern PATTERN_MAYUSCULA = Pattern.compile(".*[A-Z].*");
    private static final Pattern PATTERN_NUMERO = Pattern.compile(".*[0-9].*");
    private static final Pattern PATTERN_SIMBOLO = Pattern.compile(".*[@#$%&*].*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_contrasena);

        correoTelefono = getIntent().getStringExtra("CORREO_TELEFONO");

        initViews();
        setupListeners();
    }

    private void initViews() {
        etNuevaContrasena = findViewById(R.id.et_nueva_contrasena);
        etConfirmarContrasena = findViewById(R.id.et_confirmar_contrasena);
        btnGuardar = findViewById(R.id.btn_guardar);
        pbLoading = findViewById(R.id.pb_loading);

        tvCheck8chars = findViewById(R.id.tv_check_8chars);
        tvCheckMayuscula = findViewById(R.id.tv_check_mayuscula);
        tvCheckNumero = findViewById(R.id.tv_check_numero);
        tvCheckSimbolo = findViewById(R.id.tv_check_simbolo);
    }

    private void setupListeners() {
        etNuevaContrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarRequisitosEnTiempoReal(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnGuardar.setOnClickListener(v -> intentarGuardar());
    }

    private void validarRequisitosEnTiempoReal(String password) {
        if (password.length() >= 8) {
            tvCheck8chars.setTextColor(getColor(R.color.success));
        } else {
            tvCheck8chars.setTextColor(getColor(R.color.gray_300));
        }

        if (PATTERN_MAYUSCULA.matcher(password).matches()) {
            tvCheckMayuscula.setTextColor(getColor(R.color.success));
        } else {
            tvCheckMayuscula.setTextColor(getColor(R.color.gray_300));
        }

        if (PATTERN_NUMERO.matcher(password).matches()) {
            tvCheckNumero.setTextColor(getColor(R.color.success));
        } else {
            tvCheckNumero.setTextColor(getColor(R.color.gray_300));
        }

        if (PATTERN_SIMBOLO.matcher(password).matches()) {
            tvCheckSimbolo.setTextColor(getColor(R.color.success));
        } else {
            tvCheckSimbolo.setTextColor(getColor(R.color.gray_300));
        }
    }

    private void intentarGuardar() {
        String nueva = etNuevaContrasena.getText().toString().trim();
        String confirmar = etConfirmarContrasena.getText().toString().trim();

        if (!validarCampos(nueva, confirmar)) {
            return;
        }

        mostrarLoading(true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            guardarExitoso();
        }, 2000);
    }

    private boolean validarCampos(String nueva, String confirmar) {
        if (nueva.isEmpty()) {
            etNuevaContrasena.setError(getString(R.string.campo_vacio));
            etNuevaContrasena.requestFocus();
            return false;
        }

        if (!esContrasenaSegura(nueva)) {
            etNuevaContrasena.setError("No cumple los requisitos de seguridad");
            etNuevaContrasena.requestFocus();
            return false;
        }

        if (confirmar.isEmpty()) {
            etConfirmarContrasena.setError(getString(R.string.campo_vacio));
            etConfirmarContrasena.requestFocus();
            return false;
        }

        if (!nueva.equals(confirmar)) {
            etConfirmarContrasena.setError(getString(R.string.contrasenas_no_coinciden));
            etConfirmarContrasena.requestFocus();
            return false;
        }

        return true;
    }

    private boolean esContrasenaSegura(String password) {
        return password.length() >= 8 &&
               PATTERN_MAYUSCULA.matcher(password).matches() &&
               PATTERN_NUMERO.matcher(password).matches() &&
               PATTERN_SIMBOLO.matcher(password).matches();
    }

    private void guardarExitoso() {
        mostrarLoading(false);
        Toast.makeText(this, "Contraseña restablecida exitosamente", Toast.LENGTH_SHORT).show();

        // Volver al login
        Intent intent = new Intent(NuevaContrasenaActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnGuardar.setEnabled(false);
            btnGuardar.setText(R.string.actualizando);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnGuardar.setEnabled(true);
            btnGuardar.setText(R.string.btn_guardar_contrasena);
        }
    }
}

