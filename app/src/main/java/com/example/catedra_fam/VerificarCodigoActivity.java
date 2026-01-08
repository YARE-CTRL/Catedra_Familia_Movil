package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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

public class VerificarCodigoActivity extends AppCompatActivity {

    private ImageButton btnVolver;
    private TextView tvCorreoOculto, tvExpiracion, tvReenviar;
    private EditText etCodigo1, etCodigo2, etCodigo3, etCodigo4, etCodigo5, etCodigo6;
    private MaterialButton btnVerificar;
    private ProgressBar pbLoading;

    private String correoTelefono;
    private CountDownTimer countDownTimer;
    private boolean puedeReenviar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_codigo);

        // Obtener datos
        correoTelefono = getIntent().getStringExtra("CORREO_TELEFONO");
        String correoOculto = getIntent().getStringExtra("CORREO_OCULTO");

        // Inicializar vistas
        initViews();

        // Mostrar correo oculto
        tvCorreoOculto.setText(correoOculto);

        // Configurar listeners
        setupListeners();

        // Iniciar contador
        iniciarContador();

        // Focus en primer input
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

    private void iniciarContador() {
        // Deshabilitar reenviar
        tvReenviar.setEnabled(false);
        tvReenviar.setTextColor(getColor(R.color.gray_300));
        puedeReenviar = false;

        // Contador de 15 minutos
        countDownTimer = new CountDownTimer(15 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvExpiracion.setText(String.format("El código expira en: %02d:%02d", minutes, seconds));

                // Habilitar reenviar después de 60 segundos
                if (millisUntilFinished <= 14 * 60 * 1000 && !puedeReenviar) {
                    puedeReenviar = true;
                    tvReenviar.setEnabled(true);
                    tvReenviar.setTextColor(getColor(R.color.accent));
                }
            }

            @Override
            public void onFinish() {
                tvExpiracion.setText("El código ha expirado");
                tvExpiracion.setTextColor(getColor(R.color.danger));
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

        // Simular verificación (2 segundos)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            verificacionExitosa();
        }, 2000);
    }

    private void verificacionExitosa() {
        mostrarLoading(false);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Toast.makeText(this, "Código verificado", Toast.LENGTH_SHORT).show();

        // Ir a nueva contraseña
        Intent intent = new Intent(VerificarCodigoActivity.this, NuevaContrasenaActivity.class);
        intent.putExtra("CORREO_TELEFONO", correoTelefono);
        startActivity(intent);
        finish();
    }

    private void reenviarCodigo() {
        Toast.makeText(this, "Código reenviado", Toast.LENGTH_SHORT).show();

        // Limpiar inputs
        etCodigo1.setText("");
        etCodigo2.setText("");
        etCodigo3.setText("");
        etCodigo4.setText("");
        etCodigo5.setText("");
        etCodigo6.setText("");
        etCodigo1.requestFocus();

        // Reiniciar contador
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        iniciarContador();
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            pbLoading.setVisibility(View.VISIBLE);
            btnVerificar.setEnabled(false);
            btnVerificar.setText(R.string.verificando);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnVerificar.setEnabled(true);
            btnVerificar.setText(R.string.btn_verificar_codigo);
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

