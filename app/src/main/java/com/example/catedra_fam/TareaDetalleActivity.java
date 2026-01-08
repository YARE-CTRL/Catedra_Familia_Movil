package com.example.catedra_fam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TareaDetalleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvFrecuencia, tvFechaVencimiento, tvParaBoletin, tvDescripcion;
    private TextView tvEstadoTarea;
    private MaterialCardView cardEvidencia, cardCalificacion;
    private EditText etEvidencia;
    private MaterialButton btnFoto, btnGaleria, btnEnviar;
    private RecyclerView rvArchivos;
    private LinearLayout llArchivosPreview;
    private TextView tvNota, tvFeedback;

    private int tareaId;
    private String tareaEstado;
    private List<Uri> archivosSeleccionados = new ArrayList<>();

    // Launchers para cámara y galería
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<String> permisoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea_detalle);

        initLaunchers();
        initViews();
        setupToolbar();
        loadTareaData();
        setupListeners();
    }

    private void initLaunchers() {
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            agregarArchivo(uri);
                        }
                    }
                });

        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();
                    }
                });

        permisoLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvFrecuencia = findViewById(R.id.tv_frecuencia);
        tvFechaVencimiento = findViewById(R.id.tv_fecha_vencimiento);
        tvParaBoletin = findViewById(R.id.tv_para_boletin);
        tvDescripcion = findViewById(R.id.tv_descripcion);
        tvEstadoTarea = findViewById(R.id.tv_estado_tarea);
        cardEvidencia = findViewById(R.id.card_evidencia);
        cardCalificacion = findViewById(R.id.card_calificacion);
        etEvidencia = findViewById(R.id.et_evidencia);
        btnFoto = findViewById(R.id.btn_foto);
        btnGaleria = findViewById(R.id.btn_galeria);
        btnEnviar = findViewById(R.id.btn_enviar);
        llArchivosPreview = findViewById(R.id.ll_archivos_preview);
        tvNota = findViewById(R.id.tv_nota);
        tvFeedback = findViewById(R.id.tv_feedback);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        String titulo = getIntent().getStringExtra("TAREA_TITULO");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo != null ? titulo : "Detalle de Tarea");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadTareaData() {
        Intent intent = getIntent();
        tareaId = intent.getIntExtra("TAREA_ID", 0);
        String titulo = intent.getStringExtra("TAREA_TITULO");
        String descripcion = intent.getStringExtra("TAREA_DESCRIPCION");
        String frecuencia = intent.getStringExtra("TAREA_FRECUENCIA");
        String fecha = intent.getStringExtra("TAREA_FECHA");
        tareaEstado = intent.getStringExtra("TAREA_ESTADO");
        String nota = intent.getStringExtra("TAREA_NOTA");
        String feedback = intent.getStringExtra("TAREA_FEEDBACK");

        // Llenar datos
        tvFrecuencia.setText(frecuencia != null ? frecuencia : "Semanal");
        tvFechaVencimiento.setText(fecha != null ? fecha : "No especificada");
        tvParaBoletin.setText("Sí");
        tvDescripcion.setText(descripcion != null ? descripcion : "Sin descripción");

        // Configurar según estado
        configurarSegunEstado(tareaEstado, nota, feedback);
    }

    private void configurarSegunEstado(String estado, String nota, String feedback) {
        if (estado == null) estado = "pendiente";

        switch (estado) {
            case "vencida":
                tvEstadoTarea.setText("⚠️ TAREA VENCIDA - Entregar lo antes posible");
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.danger));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.VISIBLE);
                cardCalificacion.setVisibility(View.GONE);
                btnEnviar.setText("ENTREGAR AHORA");
                break;

            case "proxima":
                tvEstadoTarea.setText("⏰ Próxima a vencer");
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.warning));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.VISIBLE);
                cardCalificacion.setVisibility(View.GONE);
                break;

            case "pendiente":
                tvEstadoTarea.setVisibility(View.GONE);
                cardEvidencia.setVisibility(View.VISIBLE);
                cardCalificacion.setVisibility(View.GONE);
                break;

            case "completada":
                tvEstadoTarea.setText("✅ Entregada - En revisión por el docente");
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.secondary));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.GONE);
                cardCalificacion.setVisibility(View.GONE);
                break;

            case "calificada":
                tvEstadoTarea.setText("✅ Tarea calificada");
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.success));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.GONE);
                cardCalificacion.setVisibility(View.VISIBLE);

                // Mostrar calificación
                tvNota.setText(nota != null ? nota : "Sin nota");
                tvFeedback.setText(feedback != null ? feedback : "Sin comentarios del docente");
                break;
        }
    }

    private void setupListeners() {
        btnFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                permisoLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galeriaLauncher.launch(intent);
        });

        btnEnviar.setOnClickListener(v -> enviarEvidencia());
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camaraLauncher.launch(intent);
    }

    private void agregarArchivo(Uri uri) {
        if (archivosSeleccionados.size() >= 3) {
            Toast.makeText(this, "Máximo 3 archivos permitidos", Toast.LENGTH_SHORT).show();
            return;
        }

        archivosSeleccionados.add(uri);
        actualizarPreviewArchivos();
        Toast.makeText(this, "Archivo agregado (" + archivosSeleccionados.size() + "/3)", Toast.LENGTH_SHORT).show();
    }

    private void actualizarPreviewArchivos() {
        llArchivosPreview.removeAllViews();

        for (int i = 0; i < archivosSeleccionados.size(); i++) {
            Uri uri = archivosSeleccionados.get(i);
            final int index = i;

            // Crear vista de preview
            View previewView = getLayoutInflater().inflate(R.layout.item_archivo_preview, llArchivosPreview, false);
            ImageView ivPreview = previewView.findViewById(R.id.iv_preview);
            ImageView btnEliminar = previewView.findViewById(R.id.btn_eliminar);

            ivPreview.setImageURI(uri);
            btnEliminar.setOnClickListener(v -> {
                archivosSeleccionados.remove(index);
                actualizarPreviewArchivos();
            });

            llArchivosPreview.addView(previewView);
        }

        llArchivosPreview.setVisibility(archivosSeleccionados.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void enviarEvidencia() {
        String textoEvidencia = etEvidencia.getText().toString().trim();

        if (textoEvidencia.isEmpty() && archivosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Escribe una descripción o adjunta fotos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simular envío
        btnEnviar.setEnabled(false);
        btnEnviar.setText("Enviando...");

        btnEnviar.postDelayed(() -> {
            Toast.makeText(this, "✅ Evidencia enviada correctamente", Toast.LENGTH_LONG).show();
            finish();
        }, 2000);
    }
}

