package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private ChipGroup chipGroupFiltros;
    private Chip chipTodas, chipPendientes, chipCompletadas, chipCalificadas;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvTareas;
    private LinearLayout llEstadoVacio;

    private TareasAdapter tareasAdapter;
    private List<Tarea> listaTareas;
    private List<Tarea> listaOriginal;
    private ApiService apiService;

    private int estudianteId;
    private String estudianteNombre;
    private String estudianteCurso;

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
        cargarTareas();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        chipGroupFiltros = findViewById(R.id.chip_group_filtros);
        chipTodas = findViewById(R.id.chip_todas);
        chipPendientes = findViewById(R.id.chip_pendientes);
        chipCompletadas = findViewById(R.id.chip_completadas);
        chipCalificadas = findViewById(R.id.chip_calificadas);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        rvTareas = findViewById(R.id.rv_tareas);
        llEstadoVacio = findViewById(R.id.ll_estado_vacio);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tareas de " + estudianteNombre);
            getSupportActionBar().setSubtitle(estudianteCurso);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
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

        // Filtros con chips
        chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipTodas.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            filtrarTareas(checkedId);
        });
    }

    private void filtrarTareas(int chipId) {
        List<Tarea> filtradas = new ArrayList<>();

        if (chipId == R.id.chip_todas) {
            filtradas.addAll(listaOriginal);
        } else if (chipId == R.id.chip_pendientes) {
            for (Tarea t : listaOriginal) {
                if ("pendiente".equals(t.getEstado()) ||
                    "proxima".equals(t.getEstado()) ||
                    "vencida".equals(t.getEstado())) {
                    filtradas.add(t);
                }
            }
        } else if (chipId == R.id.chip_completadas) {
            for (Tarea t : listaOriginal) {
                if ("completada".equals(t.getEstado())) {
                    filtradas.add(t);
                }
            }
        } else if (chipId == R.id.chip_calificadas) {
            for (Tarea t : listaOriginal) {
                if ("calificada".equals(t.getEstado())) {
                    filtradas.add(t);
                }
            }
        }

        listaTareas.clear();
        listaTareas.addAll(filtradas);
        tareasAdapter.notifyDataSetChanged();

        // Mostrar/ocultar estado vacío
        if (listaTareas.isEmpty()) {
            llEstadoVacio.setVisibility(View.VISIBLE);
            rvTareas.setVisibility(View.GONE);
        } else {
            llEstadoVacio.setVisibility(View.GONE);
            rvTareas.setVisibility(View.VISIBLE);
        }
    }

    // Definir tipo para evitar problemas con genéricos anidados
    private interface TareasCallback extends Callback<ApiResponse<List<TareaLista>>> {}

    private void cargarTareas() {
        swipeRefresh.setRefreshing(true);
        
        apiService.getTareas(estudianteId, null).enqueue(new TareasCallback() {
            @Override
            public void onResponse(Call<ApiResponse<List<TareaLista>>> call,
                                   Response<ApiResponse<List<TareaLista>>> response) {
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
                            listaTareas.add(tarea);
                            listaOriginal.add(tarea);
                        }
                        
                        tareasAdapter.notifyDataSetChanged();
                        actualizarEstadoVacio();
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
            public void onFailure(Call<ApiResponse<List<TareaLista>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(TareasActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                mostrarEstadoVacioConError("Sin conexión a internet");
            }
        });
    }

    private void mostrarEstadoVacioConError(String mensaje) {
        listaTareas.clear();
        listaOriginal.clear();
        tareasAdapter.notifyDataSetChanged();
        actualizarContadores();
        actualizarEstadoVacio();
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


    private void actualizarContadores() {
        int pendientes = 0, completadas = 0, calificadas = 0;
        for (Tarea t : listaOriginal) {
            switch (t.getEstado()) {
                case "pendiente":
                case "proxima":
                case "vencida":
                    pendientes++;
                    break;
                case "completada":
                    completadas++;
                    break;
                case "calificada":
                    calificadas++;
                    break;
            }
        }

        chipTodas.setText("Todas (" + listaOriginal.size() + ")");
        chipPendientes.setText("Pendientes (" + pendientes + ")");
        chipCompletadas.setText("Completadas (" + completadas + ")");
        chipCalificadas.setText("Calificadas (" + calificadas + ")");
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
        startActivity(intent);
    }
}
