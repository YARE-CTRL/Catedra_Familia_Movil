package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

// ✅ NUEVOS IMPORTS - API OTP Real
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.models.OtpSolicitarRequest;
import com.example.catedra_fam.models.OtpSolicitarResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    private ImageButton btnVolver;
    private TextInputEditText etCorreoTelefono;
    private MaterialButton btnEnviarCodigo;
    private ProgressBar pbLoading;

    // ✅ NUEVO - API Service para OTP real
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        // Inicializar API Service
        apiService = RetrofitClient.getApiService(this);

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

        // ✅ LLAMADA REAL AL BACKEND - Solicitar código OTP
        OtpSolicitarRequest request = new OtpSolicitarRequest(correoTelefono);

        apiService.solicitarRecuperacion(request)
            .enqueue(new Callback<OtpSolicitarResponse>() {
                @Override
                public void onResponse(Call<OtpSolicitarResponse> call, Response<OtpSolicitarResponse> response) {
                    mostrarLoading(false);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Éxito - código enviado
                        OtpSolicitarResponse otpResponse = response.body();
                        envioExitoso(correoTelefono, otpResponse);
                    } else {
                        // Error del servidor
                        String mensaje = "Error al enviar código";
                        if (response.body() != null && response.body().getMessage() != null) {
                            mensaje = response.body().getMessage();
                        }
                        Toast.makeText(RecuperarContrasenaActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        btnEnviarCodigo.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<OtpSolicitarResponse> call, Throwable t) {
                    mostrarLoading(false);
                    Toast.makeText(RecuperarContrasenaActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    btnEnviarCodigo.setEnabled(true);
                }
            });
    }

    private void envioExitoso(String correoTelefono, OtpSolicitarResponse otpResponse) {
        // Ocultar parte del correo/teléfono
        String correoOculto = ocultarCorreo(correoTelefono);

        // Mensaje con información del método usado
        String metodo = otpResponse.getMetodo();
        String mensajeMetodo = "sms".equals(metodo) ? "teléfono" : "correo electrónico";
        Toast.makeText(this, "Código enviado a tu " + mensajeMetodo, Toast.LENGTH_SHORT).show();

        // ✅ NAVEGACIÓN CON DATOS REALES DEL BACKEND
        Intent intent = new Intent(RecuperarContrasenaActivity.this, VerificarCodigoActivity.class);
        intent.putExtra("CORREO_TELEFONO", correoTelefono);
        intent.putExtra("CORREO_OCULTO", correoOculto);
        intent.putExtra("METODO", otpResponse.getMetodo());
        intent.putExtra("EXPIRA_EN", otpResponse.getExpiraEn());

        // ⚠️ Solo en desarrollo - pasar código debug si existe
        if (otpResponse.getCodigoDebug() != null) {
            intent.putExtra("CODIGO_DEBUG", otpResponse.getCodigoDebug());
        }

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

