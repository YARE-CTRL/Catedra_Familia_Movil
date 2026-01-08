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

public class CambiarContrasenaActivity extends AppCompatActivity {

    private TextInputEditText etContrasenaActual, etNuevaContrasena, etConfirmarContrasena;
    private MaterialButton btnActualizar;
    private ProgressBar pbLoading;
    private TextView tvCheck8chars, tvCheckMayuscula, tvCheckNumero, tvCheckSimbolo;

    private boolean esObligatorio = false;
    private String correo = "";

    // Patrones de validación
    private static final Pattern PATTERN_MAYUSCULA = Pattern.compile(".*[A-Z].*");
    private static final Pattern PATTERN_NUMERO = Pattern.compile(".*[0-9].*");
    private static final Pattern PATTERN_SIMBOLO = Pattern.compile(".*[@#$%&*].*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        // Obtener extras
        esObligatorio = getIntent().getBooleanExtra("ES_OBLIGATORIO", false);
        correo = getIntent().getStringExtra("CORREO");

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        etContrasenaActual = findViewById(R.id.et_contrasena_actual);
        etNuevaContrasena = findViewById(R.id.et_nueva_contrasena);
        etConfirmarContrasena = findViewById(R.id.et_confirmar_contrasena);
        btnActualizar = findViewById(R.id.btn_actualizar);
        pbLoading = findViewById(R.id.pb_loading);

        // Checks de requisitos
        tvCheck8chars = findViewById(R.id.tv_check_8chars);
        tvCheckMayuscula = findViewById(R.id.tv_check_mayuscula);
        tvCheckNumero = findViewById(R.id.tv_check_numero);
        tvCheckSimbolo = findViewById(R.id.tv_check_simbolo);
    }

    private void setupListeners() {
        // TextWatcher para validar en tiempo real
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

        btnActualizar.setOnClickListener(v -> intentarActualizar());
    }

    private void validarRequisitosEnTiempoReal(String password) {
        // 8+ caracteres
        if (password.length() >= 8) {
            tvCheck8chars.setTextColor(getColor(R.color.success));
        } else {
            tvCheck8chars.setTextColor(getColor(R.color.gray_300));
        }

        // Mayúscula
        if (PATTERN_MAYUSCULA.matcher(password).matches()) {
            tvCheckMayuscula.setTextColor(getColor(R.color.success));
        } else {
            tvCheckMayuscula.setTextColor(getColor(R.color.gray_300));
        }

        // Número
        if (PATTERN_NUMERO.matcher(password).matches()) {
            tvCheckNumero.setTextColor(getColor(R.color.success));
        } else {
            tvCheckNumero.setTextColor(getColor(R.color.gray_300));
        }

        // Símbolo
        if (PATTERN_SIMBOLO.matcher(password).matches()) {
            tvCheckSimbolo.setTextColor(getColor(R.color.success));
        } else {
            tvCheckSimbolo.setTextColor(getColor(R.color.gray_300));
        }
    }

    private void intentarActualizar() {
        String actual = etContrasenaActual.getText().toString().trim();
        String nueva = etNuevaContrasena.getText().toString().trim();
        String confirmar = etConfirmarContrasena.getText().toString().trim();

        // Validaciones
        if (!validarCampos(actual, nueva, confirmar)) {
            return;
        }

        // Mostrar loading
        mostrarLoading(true);

        // Simular llamada a API (2 segundos)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            actualizacionExitosa();
        }, 2000);
    }

    private boolean validarCampos(String actual, String nueva, String confirmar) {
        // Validar contraseña actual
        if (actual.isEmpty()) {
            etContrasenaActual.setError(getString(R.string.campo_vacio));
            etContrasenaActual.requestFocus();
            return false;
        }

        // Validar nueva contraseña
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

        // Validar confirmación
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

    private void actualizacionExitosa() {
        mostrarLoading(false);
        Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();

        // Si era obligatorio, ir al dashboard
        // Si no, volver atrás
        if (esObligatorio) {
            Intent intent = new Intent(CambiarContrasenaActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnActualizar.setEnabled(false);
            btnActualizar.setText(R.string.actualizando);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnActualizar.setEnabled(true);
            btnActualizar.setText(R.string.btn_actualizar_contrasena);
        }
    }
}

