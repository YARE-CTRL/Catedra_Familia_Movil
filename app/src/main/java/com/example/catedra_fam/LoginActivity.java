package com.example.catedra_fam;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.LoginRequest;
import com.example.catedra_fam.models.LoginResponse;
import com.example.catedra_fam.models.DeviceInfo;
import com.example.catedra_fam.utils.DialogHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Constante para solicitud de permiso de notificaciones
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1002;

    private TextInputEditText etDocumento, etContrasena;
    private CheckBox cbRecordar;
    private MaterialButton btnIngresar, btnAyuda;
    private TextView tvOlvidasteContrasena;
    private ProgressBar pbLoading;
    private View llBannerOffline;

    private SharedPreferences prefs;
    private ApiService apiService;

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_DOCUMENTO = "numero_documento";
    private static final String KEY_RECORDAR = "recordar_sesion";
    private static final String KEY_TOKEN = "AUTH_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        apiService = RetrofitClient.getApiService(this);

        initViews();
        cargarCredencialesGuardadas();
        setupListeners();
        verificarConectividad();
        
        // 🔥 Solicitar permiso de notificaciones para Android 13+
        solicitarPermisoNotificaciones();
    }

    private void initViews() {
        etDocumento = findViewById(R.id.et_documento);
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
            String documentoGuardado = prefs.getString(KEY_DOCUMENTO, "");
            etDocumento.setText(documentoGuardado);
            cbRecordar.setChecked(true);
        }
    }

    private void intentarLogin() {
        String documento = etDocumento.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        
        if (!validarCampos(documento, contrasena)) {
            return;
        }
        
        mostrarLoading(true);
        
        // DEBUG: Mostrar credenciales en LogCat
        Log.d("LoginActivity", "Intentando login con:");
        Log.d("LoginActivity", "Documento: " + documento);
        Log.d("LoginActivity", "Contraseña: " + contrasena);
        
        LoginRequest request = new LoginRequest(documento, contrasena);
        
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                mostrarLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess()) {
                        guardarSesion(loginResponse, documento);
                        
                        // Guardar estudiantes del login si existen
                        if (loginResponse.getEstudiantes() != null && !loginResponse.getEstudiantes().isEmpty()) {
                            guardarEstudiantesLogin(loginResponse.getEstudiantes());
                        }
                        
                        loginExitoso(loginResponse.getUser().getNombreCompleto());
                    } else {
                        DialogHelper.showErrorDialog(LoginActivity.this, "Credenciales incorrectas");
                    }
                } else {
                    // Manejar error HTTP
                    String errorMsg = "Error en el servidor";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMsg = "Error: " + response.code() + " - " + errorBody;
                        }
                    } catch (Exception e) {
                        errorMsg = "Error: " + response.code();
                    }
                    DialogHelper.showErrorDialog(LoginActivity.this, errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mostrarLoading(false);
                String errorMsg = "Error de conexión";
                if (t.getMessage() != null) {
                    errorMsg = "Error de conexión: " + t.getMessage();
                }
                DialogHelper.showErrorDialog(LoginActivity.this, errorMsg);
            }
        });
    }

    private void guardarSesion(LoginResponse loginData, String documento) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, loginData.getToken());

        if (loginData.getUser() != null) {
            editor.putInt("acudiente_id", loginData.getUser().getId());
            editor.putString("nombre_usuario", loginData.getUser().getNombreCompleto());
            editor.putString("correo_usuario", loginData.getUser().getEmail());
        }

        if (cbRecordar.isChecked()) {
            editor.putString(KEY_DOCUMENTO, documento);
            editor.putBoolean(KEY_RECORDAR, true);
        } else {
            editor.remove(KEY_DOCUMENTO);
            editor.putBoolean(KEY_RECORDAR, false);
        }

        editor.apply();
    }

    private void guardarEstudiantesLogin(java.util.List<LoginResponse.EstudianteLogin> estudiantes) {
        // Guardar información de estudiantes para uso offline
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("estudiantes_count", estudiantes.size());
        
        for (int i = 0; i < estudiantes.size(); i++) {
            LoginResponse.EstudianteLogin est = estudiantes.get(i);
            String prefix = "estudiante_" + i + "_";
            editor.putInt(prefix + "id", est.getId());
            editor.putString(prefix + "nombre", est.getFirstName());
            editor.putString(prefix + "apellido", est.getLastName());
            editor.putString(prefix + "nombre_completo", est.getNombreCompleto());
            
            if (est.getCurso() != null) {
                editor.putString(prefix + "curso_nombre", est.getCurso().getName());
                editor.putString(prefix + "curso_grado", est.getCurso().getGrado());
                editor.putString(prefix + "curso_jornada", est.getCurso().getJornada());
            }
        }
        
        editor.apply();
    }

    private boolean validarCampos(String documento, String contrasena) {
        if (TextUtils.isEmpty(documento)) {
            etDocumento.setError("El número de documento es obligatorio");
            etDocumento.requestFocus();
            return false;
        }

        if (documento.length() < 6) {
            etDocumento.setError("Número de documento inválido (mínimo 6 dígitos)");
            etDocumento.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(contrasena)) {
            etContrasena.setError("La contraseña es obligatoria");
            etContrasena.requestFocus();
            return false;
        }

        if (contrasena.length() < 8) {
            etContrasena.setError("Contraseña debe tener al menos 8 caracteres");
            etContrasena.requestFocus();
            return false;
        }

        return true;
    }

    private void loginExitoso(String nombreAcudiente) {
        // Registrar token FCM después del login exitoso
        registrarFCMTokenDespuesDeLogin();
        
        // Navegar directamente sin mostrar dialog para evitar WindowLeak
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("MENSAJE_BIENVENIDA", "¡Bienvenido " + nombreAcudiente + "!");
        startActivity(intent);
        finish();
    }

    private void registrarFCMTokenDespuesDeLogin() {
        // Obtener token FCM guardado
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String fcmToken = prefs.getString("FCM_TOKEN", null);
        
        if (fcmToken != null) {
            // 📱 MOSTRAR TOKEN EN LOGIN - PARA DESARROLLO
            Log.d("TOKEN_FCM_COPIAR", "====================================");
            Log.d("TOKEN_FCM_COPIAR", "TOKEN FCM DESPUÉS DE LOGIN:");
            Log.d("TOKEN_FCM_COPIAR", fcmToken);
            Log.d("TOKEN_FCM_COPIAR", "====================================");
            
            // ✅ Crear información del dispositivo con campo plataforma actualizado
            DeviceInfo deviceInfo = new DeviceInfo(
                fcmToken,
                android.os.Build.MODEL,           // dispositivo: "moto e40"
                "Android",                        // ✅ plataforma: "Android" (capitalizado)
                "android",                        // sistemaOperativo: "android" (legacy)
                "1.0.0"                          // versionApp: "1.0.0"
            );
            
            // Registrar token en backend
            apiService.registerFCMToken("Bearer " + prefs.getString("AUTH_TOKEN", ""), deviceInfo)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            android.util.Log.d("LoginActivity", "✅ FCM Token registrado exitosamente en backend");
                        } else {
                            android.util.Log.e("LoginActivity", "⚠️ Error registrando FCM token: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        android.util.Log.e("LoginActivity", "Error de red registrando FCM token", t);
                    }
                });
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnIngresar.setEnabled(false);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnIngresar.setEnabled(true);
        }
    }

    private void verificarConectividad() {
        // Ocultar banner offline por defecto
        llBannerOffline.setVisibility(View.GONE);
    }

    /**
     * 🔥 Solicita permiso de notificaciones para Android 13+ (API 33+)
     * Este método es CRÍTICO para que las notificaciones funcionen en Android 13+
     */
    private void solicitarPermisoNotificaciones() {
        Log.d("LoginActivity", "=== VERIFICACIÓN PERMISO NOTIFICACIONES ===");
        Log.d("LoginActivity", "📱 Android Version: " + Build.VERSION.SDK_INT);
        Log.d("LoginActivity", "📱 Android Release: " + Build.VERSION.RELEASE);
        Log.d("LoginActivity", "📱 API Level requerido: " + Build.VERSION_CODES.TIRAMISU + " (Android 13)");

        // Solo para Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("LoginActivity", "✅ Android 13+ detectado, verificando permiso...");

            // Verificar si el permiso ya está concedido
            int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
            Log.d("LoginActivity", "📋 Estado permiso: " + (permissionStatus == PackageManager.PERMISSION_GRANTED ? "CONCEDIDO" : "DENEGADO"));

            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {

                Log.d("LoginActivity", "🔔 Solicitando permiso de notificaciones...");

                // Mostrar explicación (opcional pero recomendado)
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.POST_NOTIFICATIONS);
                Log.d("LoginActivity", "📝 shouldShowRationale: " + shouldShowRationale);

                if (shouldShowRationale) {

                    Log.d("LoginActivity", "💬 Mostrando diálogo explicativo...");
                    // Mostrar diálogo explicativo
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Permiso de Notificaciones Requerido")
                        .setMessage("Para recibir alertas importantes sobre tareas y eventos de tus hijos, necesitamos tu permiso para enviar notificaciones.")
                        .setPositiveButton("Conceder", (dialog, which) -> {
                            Log.d("LoginActivity", "✅ Usuario aceptó, solicitando permiso...");
                            // Solicitar permiso
                            ActivityCompat.requestPermissions(
                                this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            Log.w("LoginActivity", "❌ Usuario canceló permiso de notificaciones");
                            DialogHelper.showInfoDialog(this, "Permiso de Notificaciones", "No podrás recibir notificaciones importantes");
                        })
                        .show();
                } else {
                    // Solicitar permiso directamente (sin explicación)
                    Log.d("LoginActivity", "🔔 Solicitando permiso directamente (sin diálogo)...");
                    ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    );
                }
            } else {
                Log.d("LoginActivity", "✅ Permiso de notificaciones ya concedido");
            }
        } else {
            // Para Android < 13, el permiso no se solicita en runtime
            Log.d("LoginActivity", "⚠️ Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ") - El permiso de notificaciones NO requiere solicitud en runtime");
            Log.d("LoginActivity", "✅ Las notificaciones se habilitan automáticamente desde el AndroidManifest.xml");
        }
        Log.d("LoginActivity", "=== FIN VERIFICACIÓN ===");
    }

    /**
     * 🔥 Maneja la respuesta del usuario a la solicitud de permiso
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("LoginActivity", "✅ Permiso de notificaciones concedido");
                DialogHelper.showSuccessDialog(this, "¡Gracias! Ahora recibirás notificaciones importantes");

            } else {
                Log.w("LoginActivity", "❌ Permiso de notificaciones denegado");
                DialogHelper.showInfoDialog(this, "Permiso de Notificaciones", "No podrás recibir notificaciones importantes. Puedes activarlas en Configuración.");
            }
        }
    }
}

