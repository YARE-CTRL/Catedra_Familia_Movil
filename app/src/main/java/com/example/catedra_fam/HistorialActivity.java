package com.example.catedra_fam;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.catedra_fam.adapters.HistorialAdapter;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.HistorialResponse;
import com.example.catedra_fam.models.Entrega;
import com.example.catedra_fam.utils.DialogHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private ApiService apiService;
    private int estudianteId;
    private Integer periodoSeleccionadoId;
    private String estudianteNombre;
    private String estudianteCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        apiService = RetrofitClient.getApiService(this);
        obtenerDatosIntent();
        initViews();
        setupToolbar();
        setupSpinnerPeriodo();
        setupRecyclerView();
        setupSwipeRefresh();
        cargarEntregas();
    }

    private void obtenerDatosIntent() {
        estudianteId = getIntent().getIntExtra("ESTUDIANTE_ID", -1);
        estudianteNombre = getIntent().getStringExtra("ESTUDIANTE_NOMBRE");
        estudianteCurso = getIntent().getStringExtra("ESTUDIANTE_CURSO");

        if (estudianteId == -1 || estudianteNombre == null) {
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
        // Por ahora usar periodo activo - en futuro cargar desde API
        String periodoActual = "Periodo Actual";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            new String[]{periodoActual}
        );
        spinnerPeriodo.setAdapter(adapter);
        spinnerPeriodo.setText(periodoActual, false);

        spinnerPeriodo.setOnItemClickListener((parent, view, position, id) -> {
            cargarEntregas();
        });
    }

    private void setupRecyclerView() {
        listaEntregas = new ArrayList<>();
        historialAdapter = new HistorialAdapter(listaEntregas, entrega -> {
            // Ver evidencia
            DialogHelper.showInfoDialog(this, "Evidencia", "Ver evidencia de: " + entrega.getTareaTitulo());
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
        swipeRefresh.setRefreshing(true);

        android.util.Log.d("HistorialActivity", "📡 Cargando historial para estudiante ID: " + estudianteId);
        android.util.Log.d("HistorialActivity", "🔗 URL: /movil/estudiantes/" + estudianteId + "/historial");

        // Llamar API para obtener el historial real
        apiService.getHistorial(estudianteId, null).enqueue(new Callback<ApiResponse<HistorialResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<HistorialResponse>> call, Response<ApiResponse<HistorialResponse>> response) {
                swipeRefresh.setRefreshing(false);

                android.util.Log.d("HistorialActivity", "📥 Response code: " + response.code());
                android.util.Log.d("HistorialActivity", "📥 Response successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<HistorialResponse> apiResponse = response.body();
                    android.util.Log.d("HistorialActivity", "✅ API Success: " + apiResponse.isSuccess());

                    if (apiResponse.isSuccess()) {
                        HistorialResponse historialData = apiResponse.getData();

                        listaEntregas.clear();
                        if (historialData != null && historialData.getEntregas() != null) {
                            listaEntregas.addAll(historialData.getEntregas());
                            android.util.Log.d("HistorialActivity", "📚 Entregas cargadas: " + listaEntregas.size());
                        } else {
                            android.util.Log.w("HistorialActivity", "⚠️ Historial data o entregas es null");
                        }

                        historialAdapter.notifyDataSetChanged();
                        actualizarResumen();
                        actualizarEmptyState();
                    } else {
                        android.util.Log.e("HistorialActivity", "❌ API Error: " + apiResponse.getMessage());
                        DialogHelper.showErrorDialog(HistorialActivity.this, "Error al cargar historial: " + apiResponse.getMessage());
                        mostrarEstadoVacio();
                    }
                } else {
                    android.util.Log.e("HistorialActivity", "❌ Server Error: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            android.util.Log.e("HistorialActivity", "❌ Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            android.util.Log.e("HistorialActivity", "❌ Error leyendo error body", e);
                        }
                    }
                    DialogHelper.showErrorDialog(HistorialActivity.this, "Error del servidor: " + response.code());
                    mostrarEstadoVacio();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<HistorialResponse>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                android.util.Log.e("HistorialActivity", "❌ Network Failure: " + t.getMessage(), t);
                DialogHelper.showErrorDialog(HistorialActivity.this, "Error de conexión: " + t.getMessage());
                mostrarEstadoVacio();
            }
        });
    }

    private void mostrarEstadoVacio() {
        listaEntregas.clear();

        historialAdapter.notifyDataSetChanged();
        actualizarResumen();
        actualizarEmptyState();
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
