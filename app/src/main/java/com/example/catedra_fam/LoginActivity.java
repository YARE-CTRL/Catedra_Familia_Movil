package com.example.catedra_fam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etCorreo, etContrasena;
    private CheckBox cbRecordar;
    private MaterialButton btnIngresar, btnAyuda;
    private TextView tvOlvidasteContrasena;
    private ProgressBar pbLoading;
    private View llBannerOffline;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "CatedraFamiliaPrefs";
    private static final String KEY_CORREO = "correo";
    private static final String KEY_RECORDAR = "recordar_sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas
        initViews();

        // Cargar credenciales guardadas si existe
        cargarCredencialesGuardadas();

        // Configurar listeners
        setupListeners();

        // Verificar conectividad (simulado)
        verificarConectividad();
    }

    private void initViews() {
        etCorreo = findViewById(R.id.et_correo);
        etContrasena = findViewById(R.id.et_contrasena);
        cbRecordar = findViewById(R.id.cb_recordar);
        btnIngresar = findViewById(R.id.btn_ingresar);
        btnAyuda = findViewById(R.id.btn_ayuda);
        tvOlvidasteContrasena = findViewById(R.id.tv_olvidaste_contrasena);
        pbLoading = findViewById(R.id.pb_loading);
        llBannerOffline = findViewById(R.id.ll_banner_offline);
    }

    private void setupListeners() {
        btnIngresar.setOnClickListener(v -> intentarLogin());

        tvOlvidasteContrasena.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecuperarContrasenaActivity.class);
            startActivity(intent);
        });

        btnAyuda.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SoporteActivity.class);
            startActivity(intent);
        });
    }

    private void cargarCredencialesGuardadas() {
        boolean recordar = prefs.getBoolean(KEY_RECORDAR, false);
        if (recordar) {
            String correoGuardado = prefs.getString(KEY_CORREO, "");
            etCorreo.setText(correoGuardado);
            cbRecordar.setChecked(true);
        }
    }

    private void intentarLogin() {
        // ============================================
        // MODO DESARROLLO - SIN VALIDACIONES
        // ============================================
        // Para desarrollo, ir directo a MainActivity
        // sin validar credenciales

        Toast.makeText(this, "¡Bienvenido! (Modo desarrollo)", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        /* CÓDIGO ORIGINAL CON VALIDACIONES (comentado para desarrollo)

        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        // Validaciones
        if (!validarCampos(correo, contrasena)) {
            return;
        }

        // Mostrar loading
        mostrarLoading(true);

        // Simular llamada a API (2 segundos)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Simular login exitoso
            loginExitoso(correo);
        }, 2000);

        */
    }

    private boolean validarCampos(String correo, String contrasena) {
        // Validar correo
        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError(getString(R.string.campo_vacio));
            etCorreo.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError(getString(R.string.email_invalido));
            etCorreo.requestFocus();
            return false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(contrasena)) {
            etContrasena.setError(getString(R.string.campo_vacio));
            etContrasena.requestFocus();
            return false;
        }

        if (contrasena.length() < 8) {
            etContrasena.setError(getString(R.string.contrasena_corta));
            etContrasena.requestFocus();
            return false;
        }

        return true;
    }

    private void loginExitoso(String correo) {
        mostrarLoading(false);

        // Guardar credenciales si "Recordar sesión" está marcado
        if (cbRecordar.isChecked()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_CORREO, correo);
            editor.putBoolean(KEY_RECORDAR, true);
            editor.apply();
        } else {
            // Limpiar credenciales guardadas
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(KEY_CORREO);
            editor.putBoolean(KEY_RECORDAR, false);
            editor.apply();
        }

        // Simular detección de primer ingreso (debe_cambiar_contrasena)
        // En un caso real, esto vendría del backend
        boolean debeCambiarContrasena = correo.contains("nuevo");

        if (debeCambiarContrasena) {
            // Redirigir a cambiar contraseña obligatorio
            Intent intent = new Intent(LoginActivity.this, CambiarContrasenaActivity.class);
            intent.putExtra("ES_OBLIGATORIO", true);
            intent.putExtra("CORREO", correo);
            startActivity(intent);
            finish();
        } else {
            // Ir al dashboard (MainActivity demo)
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnIngresar.setEnabled(false);
            btnIngresar.setText(R.string.iniciando_sesion);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnIngresar.setEnabled(true);
            btnIngresar.setText(R.string.btn_ingresar);
        }
    }

    private void verificarConectividad() {
        // Simulación: mostrar banner offline al azar
        // En producción, usar ConnectivityManager
        boolean hayConexion = true; // Cambiar a false para probar

        if (!hayConexion) {
            llBannerOffline.setVisibility(View.VISIBLE);
        } else {
            llBannerOffline.setVisibility(View.GONE);
        }
    }
}

