package com.example.catedra_fam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.LoginRequest;
import com.example.catedra_fam.api.LoginResponse;
import com.example.catedra_fam.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

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
    private static final String KEY_TOKEN = "auth_token";

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
        // Obtener documento y contraseña
        String documento = etCorreo.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        // Validaciones básicas
        if (TextUtils.isEmpty(documento)) {
            etCorreo.setError("El documento es requerido");
            etCorreo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etContrasena.setError("La contraseña es requerida");
            etContrasena.requestFocus();
            return;
        }

        // Mostrar loading
        mostrarLoading(true);

        // Crear request de login
        LoginRequest loginRequest = new LoginRequest(documento, password);

        // Obtener ApiService y hacer la llamada
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<LoginResponse> call = apiService.loginMovil(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                mostrarLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess() && loginResponse.getToken() != null) {
                        // Login exitoso
                        Log.d(TAG, "Login exitoso: " + loginResponse.getUser().getFirstName());
                        guardarToken(loginResponse.getToken());
                        loginExitoso(documento, loginResponse);
                    } else {
                        // Error en la respuesta
                        String mensaje = loginResponse.getMessage() != null ? 
                            loginResponse.getMessage() : "Error al iniciar sesión";
                        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error de login: " + mensaje);
                    }
                } else {
                    // Error HTTP
                    String errorMsg = "Error al conectar con el servidor (Código: " + response.code() + ")";
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mostrarLoading(false);
                String errorMsg = "Error de conexión: " + t.getMessage();
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error de conexión", t);
            }
        });
    }

    private void guardarToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        Log.d(TAG, "Token guardado exitosamente");
    }

    private void loginExitoso(String documento, LoginResponse loginResponse) {
        // Guardar credenciales si "Recordar sesión" está marcado
        if (cbRecordar.isChecked()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_CORREO, documento);
            editor.putBoolean(KEY_RECORDAR, true);
            editor.apply();
        } else {
            // Limpiar credenciales guardadas
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(KEY_CORREO);
            editor.putBoolean(KEY_RECORDAR, false);
            editor.apply();
        }

        // Verificar si debe cambiar contraseña
        boolean debeCambiarContrasena = loginResponse.getUser() != null && 
                                        loginResponse.getUser().isMustChangePassword();

        if (debeCambiarContrasena) {
            // Redirigir a cambiar contraseña obligatorio
            Intent intent = new Intent(LoginActivity.this, CambiarContrasenaActivity.class);
            intent.putExtra("ES_OBLIGATORIO", true);
            intent.putExtra("CORREO", documento);
            startActivity(intent);
            finish();
        } else {
            // Ir al dashboard (MainActivity)
            String firstName = loginResponse.getUser().getFirstName() != null ? 
                              loginResponse.getUser().getFirstName() : "";
            String lastName = loginResponse.getUser().getLastName() != null ? 
                             loginResponse.getUser().getLastName() : "";
            String nombreUsuario = (firstName + " " + lastName).trim();
            
            if (!nombreUsuario.isEmpty()) {
                Toast.makeText(this, "¡Bienvenido " + nombreUsuario + "!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            }
            
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

