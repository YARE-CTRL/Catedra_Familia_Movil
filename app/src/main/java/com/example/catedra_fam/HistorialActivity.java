package com.example.catedra_fam;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.catedra_fam.adapters.HistorialAdapter;
import com.example.catedra_fam.models.Entrega;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    // Views
    private Toolbar toolbar;
    private AutoCompleteTextView spinnerPeriodo;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvEntregas;
    private LinearLayout layoutEmpty;
    private MaterialCardView cardResumen;
    private TextView tvCompletadas;
    private TextView tvPromedio;

    // Data
    private HistorialAdapter historialAdapter;
    private List<Entrega> listaEntregas;
    private String estudianteId;
    private String estudianteNombre;
    private String estudianteCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        obtenerDatosIntent();
        initViews();
        setupToolbar();
        setupSpinnerPeriodo();
        setupRecyclerView();
        setupSwipeRefresh();
        cargarEntregas();
    }

    private void obtenerDatosIntent() {
        estudianteId = getIntent().getStringExtra("ESTUDIANTE_ID");
        estudianteNombre = getIntent().getStringExtra("ESTUDIANTE_NOMBRE");
        estudianteCurso = getIntent().getStringExtra("ESTUDIANTE_CURSO");

        if (estudianteNombre == null) {
            estudianteNombre = "Estudiante";
        }
        if (estudianteCurso == null) {
            estudianteCurso = "";
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        spinnerPeriodo = findViewById(R.id.spinner_periodo);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        rvEntregas = findViewById(R.id.rv_entregas);
        layoutEmpty = findViewById(R.id.layout_empty);
        cardResumen = findViewById(R.id.card_resumen);
        tvCompletadas = findViewById(R.id.tv_completadas);
        tvPromedio = findViewById(R.id.tv_promedio);
    }

    private void setupToolbar() {
        toolbar.setTitle("Historial de " + estudianteNombre);
        toolbar.setSubtitle(estudianteCurso);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinnerPeriodo() {
        // Periodos mock
        String[] periodos = {
            "Periodo 1 - 2026",
            "Periodo 4 - 2025",
            "Periodo 3 - 2025",
            "Periodo 2 - 2025"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            periodos
        );
        spinnerPeriodo.setAdapter(adapter);
        spinnerPeriodo.setText(periodos[0], false);

        spinnerPeriodo.setOnItemClickListener((parent, view, position, id) -> {
            cargarEntregas();
        });
    }

    private void setupRecyclerView() {
        listaEntregas = new ArrayList<>();
        historialAdapter = new HistorialAdapter(listaEntregas, entrega -> {
            // Ver evidencia
            Toast.makeText(this,
                "Ver evidencia de: " + entrega.getTareaTitulo(),
                Toast.LENGTH_SHORT).show();
            // TODO: Abrir detalle de evidencia
        });
        rvEntregas.setLayoutManager(new LinearLayoutManager(this));
        rvEntregas.setAdapter(historialAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            cargarEntregas();
            swipeRefresh.postDelayed(() -> swipeRefresh.setRefreshing(false), 1500);
        });
    }

    private void cargarEntregas() {
        // TODO: Llamar a API real
        listaEntregas.clear();
        listaEntregas.addAll(getMockEntregas());
        historialAdapter.notifyDataSetChanged();

        actualizarResumen();
        actualizarEmptyState();
    }

    private List<Entrega> getMockEntregas() {
        List<Entrega> entregas = new ArrayList<>();

        entregas.add(new Entrega(
            "1",
            "tarea_001",
            "Lectura en familia: El Principito",
            "2026-01-05",
            "calificada",
            "Leímos juntos el capítulo del zorro...",
            4.5f,
            "Alto",
            "Excelente reflexión familiar. Sigan así.",
            "Profe. Claudia",
            "2026-01-07"
        ));

        entregas.add(new Entrega(
            "2",
            "tarea_002",
            "Juego de roles en familia",
            "2026-01-03",
            "en_revision",
            "Jugamos a representar situaciones...",
            0f,
            null,
            null,
            null,
            null
        ));

        entregas.add(new Entrega(
            "3",
            "tarea_003",
            "Conversación sobre valores",
            "2025-12-28",
            "calificada",
            "Hablamos sobre la honestidad...",
            5.0f,
            "Superior",
            "Muy bien aplicado el concepto de honestidad con ejemplos del día a día.",
            "Profe. Claudia",
            "2025-12-30"
        ));

        entregas.add(new Entrega(
            "4",
            "tarea_004",
            "Receta familiar tradicional",
            "2025-12-20",
            "calificada",
            "Preparamos empanadas con la abuela...",
            4.0f,
            "Alto",
            "Bonita tradición familiar. Las fotos muestran participación de todos.",
            "Profe. Claudia",
            "2025-12-22"
        ));

        entregas.add(new Entrega(
            "5",
            "tarea_005",
            "Caminata ecológica",
            "2025-12-15",
            "calificada",
            "Fuimos al parque y recogimos basura...",
            4.5f,
            "Alto",
            "Gran iniciativa de cuidado ambiental.",
            "Profe. Claudia",
            "2025-12-17"
        ));

        return entregas;
    }

    private void actualizarResumen() {
        int completadas = 0;
        float sumaNotas = 0;
        int cantidadCalificadas = 0;

        for (Entrega entrega : listaEntregas) {
            completadas++;
            if (entrega.isCalificada()) {
                sumaNotas += entrega.getNota();
                cantidadCalificadas++;
            }
        }

        tvCompletadas.setText("Completadas: " + completadas + "/10");

        if (cantidadCalificadas > 0) {
            float promedio = sumaNotas / cantidadCalificadas;
            String cualitativo = obtenerCualitativo(promedio);
            tvPromedio.setText("Promedio: " + String.format("%.1f", promedio) + " (" + cualitativo + ")");
        } else {
            tvPromedio.setText("Promedio: Sin calificar");
        }
    }

    private String obtenerCualitativo(float nota) {
        if (nota >= 4.6f) return "Superior";
        if (nota >= 4.0f) return "Alto";
        if (nota >= 3.0f) return "Básico";
        return "Bajo";
    }

    private void actualizarEmptyState() {
        if (listaEntregas.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvEntregas.setVisibility(View.GONE);
            cardResumen.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvEntregas.setVisibility(View.VISIBLE);
            cardResumen.setVisibility(View.VISIBLE);
        }
    }
}

