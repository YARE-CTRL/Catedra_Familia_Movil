package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.catedra_fam.adapters.TareasAdapter;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.TareaLista;
import com.example.catedra_fam.models.Tarea;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TareasActivity extends AppCompatActivity implements TareasAdapter.OnTareaClickListener {

    private Toolbar toolbar;
    private ChipGroup chipGroupFiltros, chipGroupFecha;
    private Chip chipTodas, chipPendientes, chipCompletadas, chipCalificadas;
    private Chip chipTodosTiempos, chipHoy, chip24h, chipSemana;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvTareas;
    private LinearLayout llEstadoVacio;
    private com.google.android.material.button.MaterialButton btnOrdenar;

    private TareasAdapter tareasAdapter;
    private List<Tarea> listaTareas;
    private List<Tarea> listaOriginal;
    private ApiService apiService;

    private int estudianteId;
    private String estudianteNombre;
    private String estudianteCurso;

    // Variables para filtros y ordenamiento
    private String filtroEstadoActual = "todas";
    private String filtroFechaActual = "todos";
    private boolean ordenDescendente = true; // true = más nuevas primero

    private ActivityResultLauncher<Intent> detalleTareaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        // Obtener datos del intent
        estudianteId = getIntent().getIntExtra("ESTUDIANTE_ID", 0);
        estudianteNombre = getIntent().getStringExtra("ESTUDIANTE_NOMBRE");
        estudianteCurso = getIntent().getStringExtra("ESTUDIANTE_CURSO");

        if (estudianteNombre == null) estudianteNombre = "Estudiante";
        if (estudianteCurso == null) estudianteCurso = "";

        apiService = RetrofitClient.getApiService(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        // Inicializar estado de ordenamiento
        actualizarTextoOrdenamiento();

        cargarTareas();

        detalleTareaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarTareas();
                }
            }
        );
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        chipGroupFiltros = findViewById(R.id.chip_group_filtros);
        chipGroupFecha = findViewById(R.id.chip_group_fecha);
        chipTodas = findViewById(R.id.chip_todas);
        chipPendientes = findViewById(R.id.chip_pendientes);
        chipCompletadas = findViewById(R.id.chip_completadas);
        chipCalificadas = findViewById(R.id.chip_calificadas);
        chipTodosTiempos = findViewById(R.id.chip_todos_tiempos);
        chipHoy = findViewById(R.id.chip_hoy);
        chip24h = findViewById(R.id.chip_24h);
        chipSemana = findViewById(R.id.chip_semana);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        rvTareas = findViewById(R.id.rv_tareas);
        llEstadoVacio = findViewById(R.id.ll_estado_vacio);
        btnOrdenar = findViewById(R.id.btn_ordenar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.tareas_titulo, estudianteNombre));
            getSupportActionBar().setSubtitle(estudianteCurso);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        listaTareas = new ArrayList<>();
        listaOriginal = new ArrayList<>();
        tareasAdapter = new TareasAdapter(listaTareas, this);

        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        rvTareas.setAdapter(tareasAdapter);
    }

    private void setupListeners() {
        // SwipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary, R.color.accent);
        swipeRefresh.setOnRefreshListener(this::cargarTareas);

        // Filtros por estado (chips principales)
        chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipTodas.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            actualizarFiltroEstado(checkedId);
            aplicarFiltros();
        });

        // Filtros por fecha (chips secundarios)
        chipGroupFecha.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipTodosTiempos.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            actualizarFiltroFecha(checkedId);
            aplicarFiltros();
        });

        // Botón de ordenamiento
        btnOrdenar.setOnClickListener(v -> {
            ordenDescendente = !ordenDescendente;
            actualizarTextoOrdenamiento();
            aplicarFiltros();
        });
    }

    private void actualizarFiltroEstado(int chipId) {
        if (chipId == R.id.chip_todas) {
            filtroEstadoActual = "todas";
        } else if (chipId == R.id.chip_pendientes) {
            filtroEstadoActual = "pendientes";
        } else if (chipId == R.id.chip_vencidas) {
            filtroEstadoActual = "vencidas";
        } else if (chipId == R.id.chip_completadas) {
            filtroEstadoActual = "completadas";
        } else if (chipId == R.id.chip_calificadas) {
            filtroEstadoActual = "calificadas";
        }
    }

    private void actualizarFiltroFecha(int chipId) {
        if (chipId == R.id.chip_todos_tiempos) {
            filtroFechaActual = "todos";
        } else if (chipId == R.id.chip_hoy) {
            filtroFechaActual = "hoy";
        } else if (chipId == R.id.chip_24h) {
            filtroFechaActual = "24h";
        } else if (chipId == R.id.chip_semana) {
            filtroFechaActual = "semana";
        }
    }

    private void actualizarTextoOrdenamiento() {
        if (ordenDescendente) {
            btnOrdenar.setText("Fecha ↓");
        } else {
            btnOrdenar.setText("Fecha ↑");
        }
    }

    private void aplicarFiltros() {
        android.util.Log.d("TareasActivity", "🔍 Aplicando filtros - Estado: " + filtroEstadoActual + ", Fecha: " + filtroFechaActual);

        List<Tarea> filtradas = new ArrayList<>();

        // Aplicar filtro por estado
        for (Tarea t : listaOriginal) {
            boolean cumpleEstado = false;

            switch (filtroEstadoActual) {
                case "todas":
                    cumpleEstado = true;
                    break;
                case "pendientes":
                    cumpleEstado = "pendiente".equals(t.getEstado()) ||
                                 "próximo_vencimiento".equals(t.getEstado());
                    break;
                case "vencidas":
                    cumpleEstado = "vencida".equals(t.getEstado());
                    break;
                case "completadas":
                    cumpleEstado = "entregada".equals(t.getEstado()) ||
                                 "completada".equals(t.getEstado());
                    break;
                case "calificadas":
                    cumpleEstado = "calificada".equals(t.getEstado());
                    break;
            }

            if (cumpleEstado) {
                filtradas.add(t);
            }
        }

        android.util.Log.d("TareasActivity", "   Después filtro estado: " + filtradas.size() + " de " + listaOriginal.size());

        // Aplicar filtro por fecha
        List<Tarea> filtradasPorFecha = new ArrayList<>();
        java.util.Calendar ahora = java.util.Calendar.getInstance();

        for (Tarea t : filtradas) {
            boolean cumpleFecha = false;

            switch (filtroFechaActual) {
                case "todos":
                    cumpleFecha = true;
                    break;
                case "hoy":
                    // Mostrar tareas que vencen hoy o fueron publicadas hoy
                    cumpleFecha = esFechaHoy(t.getFechaVencimiento(), ahora) ||
                                 esFechaHoy(t.getFechaPublicacion(), ahora);
                    break;
                case "24h":
                    // Mostrar tareas publicadas en las últimas 24 horas (incluye hoy automáticamente)
                    cumpleFecha = esUltimas24Horas(t.getFechaPublicacion(), ahora);
                    break;
                case "semana":
                    // Mostrar tareas de esta semana (publicación o vencimiento)
                    cumpleFecha = esEstaSemana(t.getFechaPublicacion(), ahora) ||
                                 esEstaSemana(t.getFechaVencimiento(), ahora);
                    break;
            }

            if (cumpleFecha) {
                filtradasPorFecha.add(t);
            }
        }

        android.util.Log.d("TareasActivity", "   Después filtro fecha: " + filtradasPorFecha.size() + " tareas");

        // Ordenar por fecha
        filtradasPorFecha.sort((t1, t2) -> {
            try {
                java.util.Date fecha1 = parseFechaFlexible(t1.getFechaPublicacion());
                java.util.Date fecha2 = parseFechaFlexible(t2.getFechaPublicacion());

                if (fecha1 == null && fecha2 == null) return 0;
                if (fecha1 == null) return 1;
                if (fecha2 == null) return -1;

                if (ordenDescendente) {
                    return fecha2.compareTo(fecha1); // Más nuevas primero
                } else {
                    return fecha1.compareTo(fecha2); // Más antiguas primero
                }
            } catch (Exception e) {
                android.util.Log.w("TareasActivity", "Error ordenando tareas", e);
                return 0;
            }
        });

        // Actualizar lista y UI
        listaTareas.clear();
        listaTareas.addAll(filtradasPorFecha);
        tareasAdapter.notifyDataSetChanged();

        // Mostrar/ocultar estado vacío
        actualizarEstadoVacio();
    }

    private boolean esFechaHoy(String fechaISO, java.util.Calendar ahora) {
        try {
            java.util.Date fecha = parseFechaFlexible(fechaISO);
            if (fecha == null) {
                return false;
            }

            java.util.Calendar fechaCalendar = java.util.Calendar.getInstance();
            fechaCalendar.setTime(fecha);

            return fechaCalendar.get(java.util.Calendar.YEAR) == ahora.get(java.util.Calendar.YEAR) &&
                   fechaCalendar.get(java.util.Calendar.DAY_OF_YEAR) == ahora.get(java.util.Calendar.DAY_OF_YEAR);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean esUltimas24Horas(String fechaISO, java.util.Calendar ahora) {
        try {
            java.util.Date fecha = parseFechaFlexible(fechaISO);
            if (fecha == null) {
                return false;
            }

            long ahoraMillis = ahora.getTimeInMillis();
            long hace24h = ahoraMillis - (24 * 60 * 60 * 1000);
            long fechaMillis = fecha.getTime();

            // Incluir tareas desde hace 24 horas hasta ahora
            boolean es24h = fechaMillis >= hace24h && fechaMillis <= ahoraMillis;

            return es24h;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean esEstaSemana(String fechaISO, java.util.Calendar ahora) {
        try {
            java.util.Date fecha = parseFechaFlexible(fechaISO);
            if (fecha == null) return false;

            java.util.Calendar fechaCalendar = java.util.Calendar.getInstance();
            fechaCalendar.setTime(fecha);

            return fechaCalendar.get(java.util.Calendar.YEAR) == ahora.get(java.util.Calendar.YEAR) &&
                   fechaCalendar.get(java.util.Calendar.WEEK_OF_YEAR) == ahora.get(java.util.Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse flexible de fechas que acepta múltiples formatos del backend
     */
    private java.util.Date parseFechaFlexible(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) return null;

        // Formato yyyy-MM-dd (fechaPublicacion/fechaVencimiento)
        try {
            java.text.SimpleDateFormat formatSimple = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            return formatSimple.parse(fechaStr);
        } catch (Exception e) {
            // Intentar otros formatos
        }

        // Formato ISO completo
        try {
            java.text.SimpleDateFormat formatISO = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            formatISO.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return formatISO.parse(fechaStr);
        } catch (Exception e) {
            // Intentar sin milisegundos
        }

        // Formato ISO sin milisegundos
        try {
            java.text.SimpleDateFormat formatISO2 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
            formatISO2.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return formatISO2.parse(fechaStr);
        } catch (Exception e) {
            return null;
        }
    }

    // Método legacy mantenido para compatibilidad
    private void filtrarTareas(int chipId) {
        actualizarFiltroEstado(chipId);
        aplicarFiltros();
    }

    // Definir tipo para evitar problemas con genéricos anidados
    private interface TareasCallback extends Callback<ApiResponse<List<TareaLista>>> {}

    private void cargarTareas() {
        swipeRefresh.setRefreshing(true);
        // Si hay filtro de fecha específico, usar endpoint de notificaciones con periodo
        String periodoFiltro = null;
        if (!"todos".equals(filtroFechaActual)) {
            periodoFiltro = filtroFechaActual; // "hoy", "24h", "semana"
        }

        apiService.getTareas(estudianteId, null, null).enqueue(new TareasCallback() {
            @Override
            public void onResponse(@androidx.annotation.NonNull Call<ApiResponse<List<TareaLista>>> call,
                                   @androidx.annotation.NonNull Response<ApiResponse<List<TareaLista>>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TareaLista>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        List<TareaLista> tareas = apiResponse.getData();

                        // Convertir TareaLista -> Tarea
                        listaTareas.clear();
                        listaOriginal.clear();
                        for (TareaLista tareaLista : tareas) {
                            Tarea tarea = convertirTareaListaATarea(tareaLista);
                            listaOriginal.add(tarea);
                        }

                        // ✅ NUEVO: Aplicar filtros después de cargar
                        aplicarFiltros();

                        // ✅ NUEVO: Usar contadores del backend si están disponibles
                        actualizarContadoresConMeta(apiResponse);
                    } else {
                        Toast.makeText(TareasActivity.this, "Error al cargar tareas: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        mostrarEstadoVacioConError("No se pudieron cargar las tareas");
                    }
                } else {
                    Toast.makeText(TareasActivity.this, "Error en el servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    mostrarEstadoVacioConError("Error del servidor");
                }
            }

            @Override
            public void onFailure(@androidx.annotation.NonNull Call<ApiResponse<List<TareaLista>>> call, @androidx.annotation.NonNull Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(TareasActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                mostrarEstadoVacioConError("Sin conexión a internet");
            }
        });
    }

    private void mostrarEstadoVacioConError(String mensaje) {
        listaTareas.clear();
        listaOriginal.clear();
        aplicarFiltros(); // Esto actualizará el adapter y el estado vacío
        actualizarContadores();
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private Tarea convertirTareaListaATarea(TareaLista tareaLista) {
        Tarea tarea = new Tarea();
        tarea.setId(tareaLista.getId());
        tarea.setTitulo(tareaLista.getTitulo());
        tarea.setCategoria(tareaLista.getCategoria());
        tarea.setFrecuencia(tareaLista.getFrecuencia());
        tarea.setFechaVencimiento(tareaLista.getFechaVencimiento());
        tarea.setEstado(tareaLista.getEstado());

        // Campos específicos del nuevo modelo
        tarea.setFechaPublicacion(tareaLista.getFechaPublicacion());
        tarea.setDiasRestantes(tareaLista.getDiasRestantes());

        // Determinar si incluye en boletín basado en el estado/categoria
        tarea.setIncluyeEnBoletin(!"unica".equals(tareaLista.getFrecuencia()));

        return tarea;
    }


    /**
     * ✅ NUEVO: Actualiza contadores usando el objeto meta del backend
     */
    private void actualizarContadoresConMeta(ApiResponse<List<TareaLista>> apiResponse) {
        try {
            // Intentar leer el objeto meta del backend
            Object meta = apiResponse.getMeta();
            if (meta instanceof com.google.gson.internal.LinkedTreeMap) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> metaMap = (java.util.Map<String, Object>) meta;

                // Leer contadores desde el backend
                Double totalDouble = (Double) metaMap.get("total");
                Double pendientesDouble = (Double) metaMap.get("pendientes");
                Double entregadasDouble = (Double) metaMap.get("entregadas");
                Double calificadasDouble = (Double) metaMap.get("calificadas");
                Double vencidasDouble = (Double) metaMap.get("vencidas");
                Double porAtenderDouble = (Double) metaMap.get("porAtender");

                int total = totalDouble != null ? totalDouble.intValue() : listaOriginal.size();
                int pendientes = pendientesDouble != null ? pendientesDouble.intValue() : 0;
                int entregadas = entregadasDouble != null ? entregadasDouble.intValue() : 0;
                int calificadas = calificadasDouble != null ? calificadasDouble.intValue() : 0;
                int vencidas = vencidasDouble != null ? vencidasDouble.intValue() : 0;
                int porAtender = porAtenderDouble != null ? porAtenderDouble.intValue() : pendientes + vencidas;

                android.util.Log.d("TareasActivity", "📊 Contadores desde backend:");
                android.util.Log.d("TareasActivity", "   Total: " + total);
                android.util.Log.d("TareasActivity", "   Pendientes: " + pendientes);
                android.util.Log.d("TareasActivity", "   Vencidas: " + vencidas);
                android.util.Log.d("TareasActivity", "   Entregadas: " + entregadas);
                android.util.Log.d("TareasActivity", "   Calificadas: " + calificadas);
                android.util.Log.d("TareasActivity", "   Por atender: " + porAtender);

                // ✅ CORREGIDO: Actualizar chips con contadores correctos
                chipTodas.setText(getString(R.string.tareas_filtro_todas, total));
                chipPendientes.setText("Pendientes (" + pendientes + ")");  // ✅ Solo pendientes, NO por atender
                chipCompletadas.setText(getString(R.string.tareas_filtro_completadas, entregadas));
                chipCalificadas.setText(getString(R.string.tareas_filtro_calificadas, calificadas));

                android.util.Log.d("TareasActivity", "✅ Chips actualizados:");
                android.util.Log.d("TareasActivity", "   Todas: " + total);
                android.util.Log.d("TareasActivity", "   Pendientes: " + pendientes + " (NO " + porAtender + ")");
                android.util.Log.d("TareasActivity", "   Completadas: " + entregadas);
                android.util.Log.d("TareasActivity", "   Calificadas: " + calificadas);

                return; // Salir early si se procesó correctamente
            }
        } catch (Exception e) {
            android.util.Log.w("TareasActivity", "Error procesando meta del backend, usando cálculo local", e);
        }

        // Fallback: usar método original si falla leer meta
        actualizarContadores();
    }

    private void actualizarContadores() {
        int pendientes = 0, completadas = 0, calificadas = 0;
        for (Tarea t : listaOriginal) {
            switch (t.getEstado()) {
                case "pendiente":
                case "próximo_vencimiento":
                case "vencida":
                    pendientes++;
                    break;
                case "entregada":
                    completadas++;
                    break;
                case "calificada":
                    calificadas++;
                    break;
            }
        }
        chipTodas.setText(getString(R.string.tareas_filtro_todas, listaOriginal.size()));
        chipPendientes.setText(getString(R.string.tareas_filtro_pendientes, pendientes));
        chipCompletadas.setText(getString(R.string.tareas_filtro_completadas, completadas));
        chipCalificadas.setText(getString(R.string.tareas_filtro_calificadas, calificadas));
    }

    private void actualizarEstadoVacio() {
        if (listaTareas.isEmpty()) {
            llEstadoVacio.setVisibility(View.VISIBLE);
            rvTareas.setVisibility(View.GONE);
        } else {
            llEstadoVacio.setVisibility(View.GONE);
            rvTareas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTareaClick(Tarea tarea) {
        abrirDetalleTarea(tarea);
    }

    @Override
    public void onVerDetalleClick(Tarea tarea) {
        abrirDetalleTarea(tarea);
    }

    private void abrirDetalleTarea(Tarea tarea) {
        Intent intent = new Intent(this, TareaDetalleActivity.class);
        intent.putExtra("TAREA_ID", tarea.getId());
        intent.putExtra("TAREA_TITULO", tarea.getTitulo());
        intent.putExtra("TAREA_DESCRIPCION", tarea.getDescripcion());
        intent.putExtra("TAREA_FRECUENCIA", tarea.getFrecuencia());
        intent.putExtra("TAREA_FECHA", tarea.getFechaVencimiento());
        intent.putExtra("TAREA_ESTADO", tarea.getEstado());
        intent.putExtra("TAREA_NOTA", tarea.getNota());
        intent.putExtra("TAREA_FEEDBACK", tarea.getFeedback());
        intent.putExtra("ESTUDIANTE_ID", estudianteId);
        intent.putExtra("ESTUDIANTE_NOMBRE", estudianteNombre);
        detalleTareaLauncher.launch(intent);
    }
}
