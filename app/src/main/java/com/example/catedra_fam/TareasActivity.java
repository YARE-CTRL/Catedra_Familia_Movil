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
import com.example.catedra_fam.models.Tarea;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

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

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadMockData();
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
            getSupportActionBar().setSubtitle(estudianteCurso + " - Periodo 1");
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
        swipeRefresh.setOnRefreshListener(() -> {
            // Simular recarga
            swipeRefresh.postDelayed(() -> {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(this, "Tareas actualizadas", Toast.LENGTH_SHORT).show();
            }, 1500);
        });

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

    private void loadMockData() {
        listaOriginal.clear();

        // Tarea 1 - Vencida
        Tarea t1 = new Tarea();
        t1.setId(1);
        t1.setTitulo("Juego de mesa familiar");
        t1.setDescripcion("Jueguen en familia durante 30 minutos. Puede ser dominó, cartas, parqués, etc.");
        t1.setFrecuencia("Quincenal");
        t1.setFechaVencimiento("5 Enero");
        t1.setEstado("vencida");
        t1.setIncluyeEnBoletin(true);

        // Tarea 2 - Próxima a vencer
        Tarea t2 = new Tarea();
        t2.setId(2);
        t2.setTitulo("Lectura en familia");
        t2.setDescripcion("Lean juntos un cuento o libro durante 20 minutos. Conversen sobre qué aprendieron.");
        t2.setFrecuencia("Semanal");
        t2.setFechaVencimiento("10 Enero");
        t2.setEstado("proxima");
        t2.setIncluyeEnBoletin(true);

        // Tarea 3 - Pendiente normal
        Tarea t3 = new Tarea();
        t3.setId(3);
        t3.setTitulo("Receta familiar");
        t3.setDescripcion("Preparen juntos una receta tradicional de la familia.");
        t3.setFrecuencia("Mensual");
        t3.setFechaVencimiento("20 Enero");
        t3.setEstado("pendiente");
        t3.setIncluyeEnBoletin(true);

        // Tarea 4 - Completada (en revisión)
        Tarea t4 = new Tarea();
        t4.setId(4);
        t4.setTitulo("Conversación sobre valores");
        t4.setDescripcion("Dialoguen sobre un valor importante: respeto, honestidad, responsabilidad.");
        t4.setFrecuencia("Semanal");
        t4.setFechaVencimiento("3 Enero");
        t4.setEstado("completada");
        t4.setIncluyeEnBoletin(true);

        // Tarea 5 - Calificada
        Tarea t5 = new Tarea();
        t5.setId(5);
        t5.setTitulo("Árbol genealógico");
        t5.setDescripcion("Construyan juntos el árbol genealógico de la familia.");
        t5.setFrecuencia("Única");
        t5.setFechaVencimiento("28 Dic");
        t5.setEstado("calificada");
        t5.setNota("Alto (4.5)");
        t5.setFeedback("Excelente trabajo. El árbol está muy completo y creativo.");
        t5.setIncluyeEnBoletin(true);

        // Tarea 6 - Calificada
        Tarea t6 = new Tarea();
        t6.setId(6);
        t6.setTitulo("Paseo en familia");
        t6.setDescripcion("Realicen un paseo familiar al parque o lugar cercano.");
        t6.setFrecuencia("Quincenal");
        t6.setFechaVencimiento("20 Dic");
        t6.setEstado("calificada");
        t6.setNota("Superior (4.8)");
        t6.setFeedback("Las fotos muestran un momento muy bonito en familia.");
        t6.setIncluyeEnBoletin(true);

        listaOriginal.add(t1);
        listaOriginal.add(t2);
        listaOriginal.add(t3);
        listaOriginal.add(t4);
        listaOriginal.add(t5);
        listaOriginal.add(t6);

        // Mostrar todas inicialmente
        listaTareas.addAll(listaOriginal);
        tareasAdapter.notifyDataSetChanged();

        // Actualizar contador en chips
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

