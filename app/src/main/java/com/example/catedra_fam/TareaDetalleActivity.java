package com.example.catedra_fam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.TareaDetalle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TareaDetalleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvFrecuencia, tvFechaVencimiento, tvDescripcion;
    private TextView tvEstadoTarea;
    private MaterialCardView cardEvidencia, cardCalificacion;
    private EditText etEvidencia;
    private MaterialButton btnFoto, btnGaleria, btnEnviar;
    private LinearLayout llArchivosPreview;
    private TextView tvNota, tvFeedback;

    private int tareaId;
    private final List<Uri> archivosSeleccionados = new ArrayList<>();
    private ApiService apiService;

    // Launchers para cámara y galería
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<String> permisoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea_detalle);

        apiService = RetrofitClient.getApiService(this);

        initLaunchers();
        initViews();
        setupToolbar();
        obtenerDatosIntent();
        cargarDetalleTarea();
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
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        tareaId = intent.getIntExtra("TAREA_ID", 0);
        String titulo = intent.getStringExtra("TAREA_TITULO");

        // Configurar título en toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo != null ? titulo : "Detalle de Tarea");
        }
    }

    private void cargarDetalleTarea() {
        // Mostrar loading
        mostrarLoading(true);

        apiService.getDetalleTarea(tareaId).enqueue(new Callback<ApiResponse<TareaDetalle>>() {
            @Override
            public void onResponse(Call<ApiResponse<TareaDetalle>> call, Response<ApiResponse<TareaDetalle>> response) {
                mostrarLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TareaDetalle> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        TareaDetalle detalle = apiResponse.getData();
                        mostrarDetalleTarea(detalle);
                    } else {
                        Toast.makeText(TareaDetalleActivity.this,
                            "Error: " + apiResponse.getMessage(),
                            Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(TareaDetalleActivity.this,
                        "Error del servidor: " + response.code(),
                        Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TareaDetalle>> call, Throwable t) {
                mostrarLoading(false);
                Toast.makeText(TareaDetalleActivity.this,
                    "Error de conexión: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void mostrarDetalleTarea(TareaDetalle detalle) {
        // Llenar datos reales de la API
        tvFrecuencia.setText(detalle.getFrecuencia() != null ? detalle.getFrecuencia() : "Única");
        tvFechaVencimiento.setText(detalle.getFechaVencimiento() != null ? detalle.getFechaVencimiento() : "Sin fecha límite");

        // Descripción con saltos de línea preservados
        String descripcion = detalle.getTarea().getDescripcion();
        if (descripcion != null) {
            // Preservar saltos de línea
            descripcion = descripcion.replace("\\n", "\n");
            tvDescripcion.setText(descripcion);
        } else {
            tvDescripcion.setText("Sin descripción");
        }

        // Configurar según estado
        configurarSegunEstado(detalle);

        // Mostrar archivos adjuntos del profesor si existen
        mostrarArchivosAdjuntos(detalle.getTarea().getArchivosAdjuntos());

        // Mostrar enlaces si existen
        mostrarEnlaces(detalle.getTarea().getEnlaces());
    }

    /**
     * Muestra archivos adjuntos del profesor
     * ✅ IMPLEMENTACIÓN COMPLETA - LinearLayout dinámico con CardViews clickeables
     */
    private void mostrarArchivosAdjuntos(List<TareaDetalle.ArchivoAdjunto> archivos) {
        if (archivos != null && !archivos.isEmpty()) {
            // Crear contenedor dinámicamente (no existe en layout)
            createArchivosContainer(archivos);
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR - Crear contenedor de archivos dinámicamente
     */
    private void createArchivosContainer(List<TareaDetalle.ArchivoAdjunto> archivos) {
        // Buscar el parent del tvDescripcion para insertar después
        ViewGroup parent = (ViewGroup) tvDescripcion.getParent();
        if (parent == null) return;

        // Crear título
        TextView tituloArchivos = new TextView(this);
        tituloArchivos.setText("📎 Archivos del profesor");
        tituloArchivos.setTextSize(16f);
        tituloArchivos.setTypeface(null, android.graphics.Typeface.BOLD);
        tituloArchivos.setPadding(0, 24, 0, 12);
        parent.addView(tituloArchivos);

        // Crear contenedor
        LinearLayout containerArchivos = new LinearLayout(this);
        containerArchivos.setOrientation(LinearLayout.VERTICAL);
        parent.addView(containerArchivos);

        // Agregar archivos
        for (TareaDetalle.ArchivoAdjunto archivo : archivos) {
            createArchivoAdjuntoView(containerArchivos, archivo);
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR - Crear vista individual para archivo adjunto
     */
    private void createArchivoAdjuntoView(LinearLayout container, TareaDetalle.ArchivoAdjunto archivo) {
        // Crear CardView programáticamente
        androidx.cardview.widget.CardView cardView = new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 12);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(2f);
        cardView.setRadius(8f);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        cardView.setUseCompatPadding(true);

        // LinearLayout interno
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setPadding(16, 12, 16, 12);
        innerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Icono del archivo
        TextView iconView = new TextView(this);
        iconView.setText(getFileIcon(archivo.getTipo()));
        iconView.setTextSize(24f);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        iconParams.setMargins(0, 0, 16, 0);
        iconView.setLayoutParams(iconParams);

        // Información del archivo
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        infoLayout.setLayoutParams(infoParams);

        TextView nameView = new TextView(this);
        nameView.setText(archivo.getNombre());
        nameView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        nameView.setTextSize(14f);
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView typeView = new TextView(this);
        typeView.setText(archivo.getTipo().toUpperCase());
        typeView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        typeView.setTextSize(12f);

        infoLayout.addView(nameView);
        infoLayout.addView(typeView);

        // Botón de descarga/vista
        TextView actionView = new TextView(this);
        actionView.setText("📥");
        actionView.setTextSize(18f);
        actionView.setPadding(12, 8, 8, 8);
        actionView.setBackgroundResource(android.R.drawable.list_selector_background);

        // Ensamblar la vista
        innerLayout.addView(iconView);
        innerLayout.addView(infoLayout);
        innerLayout.addView(actionView);
        cardView.addView(innerLayout);

        // Click listener para abrir/descargar archivo
        cardView.setOnClickListener(v -> {
            abrirArchivoAdjunto(archivo);
        });

        container.addView(cardView);
    }

    /**
     * ✅ MÉTODO AUXILIAR - Abrir archivo adjunto
     */
    private void abrirArchivoAdjunto(TareaDetalle.ArchivoAdjunto archivo) {
        try {
            if (archivo.getUrl() != null && !archivo.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(archivo.getUrl()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "URL no disponible para este archivo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show();
            Log.e("TareaDetalle", "Error abriendo archivo: " + e.getMessage());
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR - Formatear tamaño de archivo
     */
    private String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";

        String[] units = {"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));

        return String.format("%.1f %s",
            bytes / Math.pow(1024, digitGroups),
            units[digitGroups]);
    }

    /**
     * ✅ MÉTODO AUXILIAR - Obtiene el icono según el tipo de archivo
     */
    private String getFileIcon(String tipo) {
        if (tipo == null) return "📄";
        switch (tipo.toLowerCase()) {
            case "pdf": return "📋";
            case "doc":
            case "docx": return "📝";
            case "xls":
            case "xlsx": return "📊";
            case "ppt":
            case "pptx": return "📈";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif": return "🖼️";
            case "mp4":
            case "avi":
            case "mov": return "🎥";
            case "mp3":
            case "wav": return "🎵";
            case "zip":
            case "rar": return "🗜️";
            default: return "📄";
        }
    }

    /**
     * Muestra enlaces proporcionados por el profesor
     * ✅ IMPLEMENTACIÓN COMPLETA - CardViews clickeables con vista previa
     */
    private void mostrarEnlaces(List<TareaDetalle.Enlace> enlaces) {
        if (enlaces != null && !enlaces.isEmpty()) {
            // Crear contenedor dinámicamente (no existe en layout)
            createEnlacesContainer(enlaces);
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR - Crear contenedor de enlaces dinámicamente
     */
    private void createEnlacesContainer(List<TareaDetalle.Enlace> enlaces) {
        // Buscar el parent del tvDescripcion para insertar después
        ViewGroup parent = (ViewGroup) tvDescripcion.getParent();
        if (parent == null) return;

        // Crear título
        TextView tituloEnlaces = new TextView(this);
        tituloEnlaces.setText("🔗 Enlaces de referencia");
        tituloEnlaces.setTextSize(16f);
        tituloEnlaces.setTypeface(null, android.graphics.Typeface.BOLD);
        tituloEnlaces.setPadding(0, 24, 0, 12);
        parent.addView(tituloEnlaces);

        // Crear contenedor
        LinearLayout containerEnlaces = new LinearLayout(this);
        containerEnlaces.setOrientation(LinearLayout.VERTICAL);
        parent.addView(containerEnlaces);

        // Agregar enlaces
        for (TareaDetalle.Enlace enlace : enlaces) {
            createEnlaceView(containerEnlaces, enlace);
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR - Crear vista individual para enlace
     */
    private void createEnlaceView(LinearLayout container, TareaDetalle.Enlace enlace) {
        // Crear CardView programáticamente
        androidx.cardview.widget.CardView cardView = new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 12);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(2f);
        cardView.setRadius(8f);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        cardView.setUseCompatPadding(true);

        // LinearLayout interno
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setPadding(16, 12, 16, 12);
        innerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Icono del enlace
        TextView iconView = new TextView(this);
        iconView.setText(getLinkIcon(enlace.getUrl()));
        iconView.setTextSize(24f);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        iconParams.setMargins(0, 0, 16, 0);
        iconView.setLayoutParams(iconParams);

        // Información del enlace
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        infoLayout.setLayoutParams(infoParams);

        TextView titleView = new TextView(this);
        titleView.setText(enlace.getTitulo());
        titleView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        titleView.setTextSize(14f);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView urlView = new TextView(this);
        urlView.setText(formatUrl(enlace.getUrl()));
        urlView.setTextColor(ContextCompat.getColor(this, R.color.primary));
        urlView.setTextSize(12f);
        urlView.setSingleLine(true);
        urlView.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);

        infoLayout.addView(titleView);
        infoLayout.addView(urlView);

        // Botón de abrir
        TextView actionView = new TextView(this);
        actionView.setText("🌐");
        actionView.setTextSize(18f);
        actionView.setPadding(12, 8, 8, 8);
        actionView.setBackgroundResource(android.R.drawable.list_selector_background);

        // Ensamblar la vista
        innerLayout.addView(iconView);
        innerLayout.addView(infoLayout);
        innerLayout.addView(actionView);
        cardView.addView(innerLayout);

        // Click listener para abrir enlace
        cardView.setOnClickListener(v -> {
            abrirEnlace(enlace);
        });

        container.addView(cardView);
    }

    /**
     * ✅ MÉTODO AUXILIAR - Abrir enlace en navegador
     */
    private void abrirEnlace(TareaDetalle.Enlace enlace) {
        try {
            String url = enlace.getUrl();

            // Agregar protocolo si no existe
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show();
            Log.e("TareaDetalle", "Error abriendo enlace: " + e.getMessage());
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR - Formatear URL para mostrar
     */
    private String formatUrl(String url) {
        if (url == null || url.isEmpty()) return "";

        // Remover protocolo para mostrar URL más limpia
        return url.replaceFirst("^https?://", "")
                  .replaceFirst("^www\\.", "");
    }

    /**
     * ✅ MÉTODO AUXILIAR - Obtener icono según dominio del enlace
     */
    private String getLinkIcon(String url) {
        if (url == null) return "🔗";

        String lowerUrl = url.toLowerCase();

        if (lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be")) {
            return "📹";
        } else if (lowerUrl.contains("docs.google.com")) {
            return "📋";
        } else if (lowerUrl.contains("drive.google.com")) {
            return "💾";
        } else if (lowerUrl.contains("wikipedia.org")) {
            return "📖";
        } else if (lowerUrl.contains("github.com")) {
            return "💻";
        } else if (lowerUrl.contains("pdf") || lowerUrl.endsWith(".pdf")) {
            return "📄";
        } else {
            return "🔗";
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (mostrar) {
            tvDescripcion.setText(getString(R.string.loading_task_info));
            tvFrecuencia.setText("--");
            tvFechaVencimiento.setText("--");

            // Ocultar cards mientras carga
            cardEvidencia.setVisibility(View.GONE);
            cardCalificacion.setVisibility(View.GONE);
            tvEstadoTarea.setVisibility(View.GONE);
        }
        // No hacer nada cuando se oculta, los datos se llenan en mostrarDetalleTarea()
    }

    private void configurarSegunEstado(TareaDetalle detalle) {
        String estado = detalle.getEstado();
        if (estado == null) estado = "pendiente";

        switch (estado) {
            case "vencida":
                tvEstadoTarea.setText(getString(R.string.task_expired));
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.danger));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.VISIBLE);
                cardCalificacion.setVisibility(View.GONE);
                btnEnviar.setText(getString(R.string.deliver_now));
                break;

            case "próximo_vencimiento":
                tvEstadoTarea.setText(getString(R.string.next_due));
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

            case "entregada":
                tvEstadoTarea.setText(getString(R.string.delivered_review));
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.secondary));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.GONE);
                cardCalificacion.setVisibility(View.GONE);

                // Mostrar información de entrega si existe
                if (detalle.getEntrega() != null) {
                    mostrarInformacionEntrega(detalle.getEntrega());
                }
                break;

            case "calificada":
                tvEstadoTarea.setText(getString(R.string.task_graded));
                tvEstadoTarea.setTextColor(ContextCompat.getColor(this, R.color.success));
                tvEstadoTarea.setVisibility(View.VISIBLE);
                cardEvidencia.setVisibility(View.GONE);
                cardCalificacion.setVisibility(View.VISIBLE);

                // Mostrar calificación real
                if (detalle.getCalificacion() != null) {
                    tvNota.setText(getString(R.string.grade_label, detalle.getCalificacion().getNota()));
                    tvFeedback.setText(detalle.getCalificacion().getRetroalimentacion() != null ?
                        detalle.getCalificacion().getRetroalimentacion() : getString(R.string.no_teacher_comments));
                } else {
                    tvNota.setText(getString(R.string.not_graded));
                    tvFeedback.setText(getString(R.string.no_feedback));
                }
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
            Toast.makeText(this, "Escribe una descripción o adjunta archivos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener estudiante ID del intent o SharedPreferences
        Intent intent = getIntent();
        int estudianteId = intent.getIntExtra("ESTUDIANTE_ID", 0);

        if (estudianteId == 0) {
            // Intentar obtener de SharedPreferences como fallback
            android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            estudianteId = prefs.getInt("estudiante_0_id", 0); // Primer estudiante guardado
        }

        if (estudianteId == 0) {
            Toast.makeText(this, "Error: No se pudo identificar al estudiante", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar archivos seleccionados
        for (Uri uri : archivosSeleccionados) {
            if (!validarArchivo(uri)) {
                return; // Error ya mostrado en validarArchivo()
            }
        }

        // Preparar FormData para envío
        btnEnviar.setEnabled(false);
        btnEnviar.setText(getString(R.string.sending));

        try {
            // Crear RequestBody para campos básicos
            okhttp3.RequestBody estudianteIdBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                String.valueOf(estudianteId)
            );

            okhttp3.RequestBody descripcionBody = null;
            if (!textoEvidencia.isEmpty()) {
                descripcionBody = okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("text/plain"),
                    textoEvidencia
                );
            }

            // Crear RequestBody para nombre del envío
            android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String nombreUsuario = prefs.getString("nombre_usuario", "Acudiente");
            okhttp3.RequestBody nombreEnvioBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                nombreUsuario
            );

            // Preparar archivos
            List<okhttp3.MultipartBody.Part> partes = new ArrayList<>();
            for (int i = 0; i < archivosSeleccionados.size(); i++) {
                Uri uri = archivosSeleccionados.get(i);
                try {
                    java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        // Obtener el tipo MIME del archivo
                        String mimeType = getContentResolver().getType(uri);
                        if (mimeType == null) mimeType = "application/octet-stream";

                        // Obtener nombre del archivo
                        String fileName = "archivo_" + i + "." + obtenerExtensionDeMime(mimeType);

                        // Leer bytes del archivo
                        byte[] bytes = leerBytesDeInputStream(inputStream);
                        inputStream.close();

                        // Crear RequestBody del archivo
                        okhttp3.RequestBody archivoBody = okhttp3.RequestBody.create(
                            okhttp3.MediaType.parse(mimeType),
                            bytes
                        );

                        // Crear Part
                        okhttp3.MultipartBody.Part parte = okhttp3.MultipartBody.Part.createFormData(
                            "archivos",
                            fileName,
                            archivoBody
                        );
                        partes.add(parte);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error al procesar archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText(getString(R.string.send_evidence));
                    return;
                }
            }

            // Realizar llamada a la API
            apiService.enviarEvidencia(
                tareaId,
                estudianteIdBody,
                descripcionBody,
                nombreEnvioBody,
                partes
            ).enqueue(new Callback<ApiResponse<com.example.catedra_fam.models.Entrega>>() {
                @Override
                public void onResponse(Call<ApiResponse<com.example.catedra_fam.models.Entrega>> call,
                                     Response<ApiResponse<com.example.catedra_fam.models.Entrega>> response) {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText(getString(R.string.send_evidence));

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<com.example.catedra_fam.models.Entrega> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Toast.makeText(TareaDetalleActivity.this, "✅ Evidencia enviada correctamente", Toast.LENGTH_LONG).show();

                            // Marcar resultado para actualizar la lista
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(TareaDetalleActivity.this,
                                "Error al enviar: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = "Error del servidor: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                if (errorBody.contains("muy grande")) {
                                    errorMsg = "Uno o más archivos son muy grandes (máximo 50MB cada uno)";
                                } else if (errorBody.contains("formato")) {
                                    errorMsg = "Formato de archivo no permitido";
                                } else {
                                    errorMsg = "Error: " + errorBody;
                                }
                            }
                        } catch (Exception e) {
                            // Usar mensaje por defecto
                        }
                        Toast.makeText(TareaDetalleActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<com.example.catedra_fam.models.Entrega>> call, Throwable t) {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText(getString(R.string.send_evidence));

                    Toast.makeText(TareaDetalleActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            btnEnviar.setEnabled(true);
            btnEnviar.setText(getString(R.string.send_evidence));
            Toast.makeText(this, "Error al preparar envío: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Valida formato y tamaño de archivo según especificaciones del backend
     */
    private boolean validarArchivo(Uri uri) {
        try {
            // Obtener información del archivo
            String mimeType = getContentResolver().getType(uri);
            if (mimeType == null) {
                Toast.makeText(this, "No se puede determinar el tipo de archivo", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validar formato
            String[] formatosPermitidos = {
                "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "text/plain", "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif",
                "video/mp4", "video/quicktime", "video/x-msvideo", "audio/mpeg", "audio/wav", "audio/mp4"
            };

            boolean formatoValido = false;
            for (String formato : formatosPermitidos) {
                if (mimeType.equals(formato)) {
                    formatoValido = true;
                    break;
                }
            }

            if (!formatoValido) {
                Toast.makeText(this, "Formato no permitido: " + mimeType, Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validar tamaño (50MB máximo)
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                int tamaño = inputStream.available();
                inputStream.close();

                final int TAMAÑO_MAX = 50 * 1024 * 1024; // 50MB
                if (tamaño > TAMAÑO_MAX) {
                    float tamañoMB = tamaño / (1024f * 1024f);
                    Toast.makeText(this,
                        String.format("Archivo muy grande: %.1f MB (máx 50MB)", tamañoMB),
                        Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            Toast.makeText(this, "Error al validar archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Obtiene extensión de archivo a partir del MIME type
     */
    private String obtenerExtensionDeMime(String mimeType) {
        switch (mimeType) {
            case "application/pdf": return "pdf";
            case "application/msword": return "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return "docx";
            case "image/jpeg": return "jpg";
            case "image/png": return "png";
            case "image/webp": return "webp";
            case "image/gif": return "gif";
            case "video/mp4": return "mp4";
            case "audio/mpeg": return "mp3";
            case "audio/wav": return "wav";
            default: return "bin";
        }
    }

    /**
     * Muestra información de entrega existente
     */
    private void mostrarInformacionEntrega(com.example.catedra_fam.models.Entrega entrega) {
        String infoEntrega = "\n\n📤 Tu entrega:\n";
        String descripcionEntrega = entrega.getDescripcion();
        if (descripcionEntrega != null && !descripcionEntrega.isEmpty()) {
            infoEntrega += getString(R.string.feedback_label) + " " + descripcionEntrega + "\n";
        }
        List<String> archivos = entrega.getArchivos();
        if (archivos != null && !archivos.isEmpty()) {
            infoEntrega += getString(R.string.archivos_adjuntos) + ": " + archivos.size() + " archivo(s)\n";
        }
        String fechaEntrega = entrega.getFechaEntregaPublic();
        infoEntrega += getString(R.string.detalle_vence) + " " + formatearFecha(fechaEntrega) + "\n";
        String estadoActual = tvEstadoTarea.getText().toString();
        tvEstadoTarea.setText(String.format("%s%s", estadoActual, infoEntrega));
    }

    /**
     * Formatea una fecha ISO a formato legible
     */
    private String formatearFecha(String fechaISO) {
        try {
            // Formato de entrada: "2026-02-12T14:30:00.000Z"
            // Formato de salida: "12 Feb 2026, 2:30 PM"
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd MMM yyyy, h:mm a", java.util.Locale.US);
            java.util.Date fecha = inputFormat.parse(fechaISO);
            if (fecha == null) return fechaISO;
            return outputFormat.format(fecha);
        } catch (Exception e) {
            return fechaISO;
        }
    }

    /**
     * Lee todos los bytes de un InputStream
     */
    private byte[] leerBytesDeInputStream(java.io.InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
