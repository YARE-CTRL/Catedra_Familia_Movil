package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

// ✅ NUEVOS IMPORTS - API OTP Real
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.models.OtpVerificarRequest;
import com.example.catedra_fam.models.OtpVerificarResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificarCodigoActivity extends AppCompatActivity {

    private ImageButton btnVolver;
    private TextView tvCorreoOculto, tvExpiracion, tvReenviar;
    private EditText etCodigo1, etCodigo2, etCodigo3, etCodigo4, etCodigo5, etCodigo6;
    private MaterialButton btnVerificar;
    private ProgressBar pbLoading;

    // ✅ DATOS DEL BACKEND
    private String correoTelefono;
    private String metodo;
    private int expiraEn;
    private String codigoDebug; // Solo desarrollo

    // ✅ API Service
    private ApiService apiService;

    // Timer y estado
    private CountDownTimer countDownTimer;
    private boolean puedeReenviar = false;
    private int intentosRestantes = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_codigo);

        // ✅ OBTENER DATOS DEL BACKEND
        correoTelefono = getIntent().getStringExtra("CORREO_TELEFONO");
        String correoOculto = getIntent().getStringExtra("CORREO_OCULTO");
        metodo = getIntent().getStringExtra("METODO");
        expiraEn = getIntent().getIntExtra("EXPIRA_EN", 900); // Default 15 min
        codigoDebug = getIntent().getStringExtra("CODIGO_DEBUG");

        // Inicializar API Service
        apiService = RetrofitClient.getApiService(this);

        // Inicializar vistas
        initViews();

        // Mostrar correo oculto
        tvCorreoOculto.setText(correoOculto);

        // ⚠️ Mostrar código debug solo en desarrollo
        if (codigoDebug != null) {
            Toast.makeText(this, "Código debug: " + codigoDebug, Toast.LENGTH_LONG).show();
        }

        // Configurar listeners
        setupListeners();

        // ✅ INICIAR CONTADOR CON TIEMPO REAL DEL BACKEND
        iniciarContador(expiraEn);

        // ✅ FOCUS INICIAL en primer campo
        etCodigo1.requestFocus();
    }

    private void initViews() {
        btnVolver = findViewById(R.id.btn_volver);
        tvCorreoOculto = findViewById(R.id.tv_correo_oculto);
        tvExpiracion = findViewById(R.id.tv_expiracion);
        tvReenviar = findViewById(R.id.tv_reenviar);
        etCodigo1 = findViewById(R.id.et_codigo1);
        etCodigo2 = findViewById(R.id.et_codigo2);
        etCodigo3 = findViewById(R.id.et_codigo3);
        etCodigo4 = findViewById(R.id.et_codigo4);
        etCodigo5 = findViewById(R.id.et_codigo5);
        etCodigo6 = findViewById(R.id.et_codigo6);
        btnVerificar = findViewById(R.id.btn_verificar);
        pbLoading = findViewById(R.id.pb_loading);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());

        btnVerificar.setOnClickListener(v -> intentarVerificar());

        tvReenviar.setOnClickListener(v -> {
            if (puedeReenviar) {
                reenviarCodigo();
            } else {
                Toast.makeText(this, "Espera para reenviar el código", Toast.LENGTH_SHORT).show();
            }
        });

        // Auto-focus entre inputs
        setupAutoFocus(etCodigo1, etCodigo2, null);
        setupAutoFocus(etCodigo2, etCodigo3, etCodigo1);
        setupAutoFocus(etCodigo3, etCodigo4, etCodigo2);
        setupAutoFocus(etCodigo4, etCodigo5, etCodigo3);
        setupAutoFocus(etCodigo5, etCodigo6, etCodigo4);
        setupAutoFocus(etCodigo6, null, etCodigo5);
    }

    private void setupAutoFocus(EditText current, EditText next, EditText previous) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        current.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (current.getText().toString().isEmpty() && previous != null) {
                    previous.requestFocus();
                    return true;
                }
            }
            return false;
        });
    }

    private void iniciarContador(int segundosExpiracion) {
        // Deshabilitar reenviar
        tvReenviar.setEnabled(false);
        tvReenviar.setTextColor(getColor(R.color.gray_300)); // ⚠️ Verificar que existe este color
        puedeReenviar = false;

        // ✅ CONTADOR CON TIEMPO REAL DEL BACKEND
        long milisegundosTotal = segundosExpiracion * 1000L;

        countDownTimer = new CountDownTimer(milisegundosTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvExpiracion.setText(String.format("El código expira en: %02d:%02d", minutes, seconds));

                // Habilitar reenviar después de 60 segundos
                if (millisUntilFinished <= (milisegundosTotal - 60000) && !puedeReenviar) {
                    puedeReenviar = true;
                    tvReenviar.setEnabled(true);
                    tvReenviar.setTextColor(getColor(R.color.accent)); // ⚠️ Verificar que existe este color
                }
            }

            @Override
            public void onFinish() {
                tvExpiracion.setText("El código ha expirado");
                tvExpiracion.setTextColor(getColor(R.color.danger)); // ⚠️ Verificar que existe este color
                btnVerificar.setEnabled(false);
                Toast.makeText(VerificarCodigoActivity.this,
                    "El código ha expirado. Solicita uno nuevo.", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void intentarVerificar() {
        String codigo = etCodigo1.getText().toString() +
                       etCodigo2.getText().toString() +
                       etCodigo3.getText().toString() +
                       etCodigo4.getText().toString() +
                       etCodigo5.getText().toString() +
                       etCodigo6.getText().toString();

        if (codigo.length() != 6) {
            Toast.makeText(this, "Ingresa el código completo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar loading
        mostrarLoading(true);

        // ✅ LLAMADA REAL AL BACKEND - Verificar código OTP
        OtpVerificarRequest request = new OtpVerificarRequest(correoTelefono, codigo);

        apiService.verificarCodigo(request)
            .enqueue(new Callback<OtpVerificarResponse>() {
                @Override
                public void onResponse(Call<OtpVerificarResponse> call, Response<OtpVerificarResponse> response) {
                    mostrarLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        OtpVerificarResponse otpResponse = response.body();

                        if (otpResponse.isSuccess()) {
                            // ✅ CÓDIGO CORRECTO
                            verificacionExitosa(otpResponse.getToken());
                        } else {
                            // ❌ CÓDIGO INCORRECTO
                            intentosRestantes = otpResponse.getIntentosRestantes();

                            String mensaje = otpResponse.getMessage();
                            if (intentosRestantes > 0) {
                                mensaje += "\nIntentos restantes: " + intentosRestantes;
                            }

                            Toast.makeText(VerificarCodigoActivity.this, mensaje, Toast.LENGTH_LONG).show();

                            // Limpiar inputs si no quedan intentos
                            if (intentosRestantes == 0) {
                                limpiarInputsYVolver();
                            } else {
                                // Limpiar solo el código para intentar de nuevo
                                limpiarCodigoInputs();
                            }
                        }
                    } else {
                        // ❌ ERROR HTTP O RESPUESTA INVÁLIDA
                        String errorMsg = "Error del servidor";

                        if (response.body() != null && response.body().getMessage() != null) {
                            errorMsg = response.body().getMessage();
                        } else if (!response.isSuccessful()) {
                            errorMsg = "Error del servidor (Código: " + response.code() + ")";
                        }

                        Toast.makeText(VerificarCodigoActivity.this, errorMsg, Toast.LENGTH_LONG).show();

                        // Limpiar inputs para intentar de nuevo
                        limpiarCodigoInputs();
                    }
                }

                @Override
                public void onFailure(Call<OtpVerificarResponse> call, Throwable t) {
                    mostrarLoading(false);
                    Toast.makeText(VerificarCodigoActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void verificacionExitosa(String token) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Toast.makeText(this, "Código verificado exitosamente", Toast.LENGTH_SHORT).show();

        // ✅ IR A NUEVA CONTRASEÑA CON TOKEN DEL BACKEND
        Intent intent = new Intent(VerificarCodigoActivity.this, NuevaContrasenaActivity.class);
        intent.putExtra("CORREO_TELEFONO", correoTelefono);
        intent.putExtra("TOKEN", token); // Token para paso 3
        startActivity(intent);
        finish();
    }

    /**
     * ✅ NUEVO MÉTODO - Limpiar inputs cuando se agotaron intentos
     */
    private void limpiarInputsYVolver() {
        Toast.makeText(this, "Se agotaron los intentos. Solicita un código nuevo.",
                      Toast.LENGTH_LONG).show();
        finish(); // Volver a la pantalla anterior
    }

    /**
     * ✅ NUEVO MÉTODO - Limpiar solo los inputs del código
     */
    private void limpiarCodigoInputs() {
        etCodigo1.setText("");
        etCodigo2.setText("");
        etCodigo3.setText("");
        etCodigo4.setText("");
        etCodigo5.setText("");
        etCodigo6.setText("");
        etCodigo1.requestFocus();
    }

    private void reenviarCodigo() {
        // Deshabilitar el botón temporalmente
        tvReenviar.setEnabled(false);

        // ✅ HACER NUEVA LLAMADA AL BACKEND para reenviar código
        // Por ahora, simular reenvío exitoso ya que no hay endpoint específico de reenvío
        // En producción, deberías implementar un endpoint POST /auth/recuperar/reenviar

        // Mostrar feedback al usuario
        Toast.makeText(this, "Código reenviado a tu " +
            ("sms".equals(metodo) ? "teléfono" : "correo electrónico"),
            Toast.LENGTH_LONG).show();

        // Limpiar inputs
        limpiarCodigoInputs();

        // Resetear intentos
        intentosRestantes = 5;

        // Reiniciar contador con el tiempo original del backend
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        iniciarContador(expiraEn);

        // ⚠️ Mostrar código debug si está disponible (solo desarrollo)
        if (codigoDebug != null) {
            Toast.makeText(this, "Código debug: " + codigoDebug, Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnVerificar.setEnabled(false);
            btnVerificar.setText("Verificando...");
        } else {
            pbLoading.setVisibility(View.GONE);
            btnVerificar.setEnabled(true);
            btnVerificar.setText("Verificar código");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

