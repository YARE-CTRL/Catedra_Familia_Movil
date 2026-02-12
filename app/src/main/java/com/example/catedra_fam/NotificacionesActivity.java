package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.catedra_fam.adapters.NotificacionesAdapter;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.Notificacion;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesActivity extends AppCompatActivity implements NotificacionesAdapter.OnNotificacionClickListener {

    // Views
    private Toolbar toolbar;
    private TextView tvNoLeidas;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvNotificaciones;
    private LinearLayout layoutEmpty;
    private MaterialButton btnEliminarLeidas;

    // Data
    private NotificacionesAdapter notificacionesAdapter;
    private List<Notificacion> listaNotificaciones;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        apiService = RetrofitClient.getApiService(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        setupListeners();
        cargarNotificaciones();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvNoLeidas = findViewById(R.id.tv_no_leidas);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        rvNotificaciones = findViewById(R.id.rv_notificaciones);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnEliminarLeidas = findViewById(R.id.btn_eliminar_leidas);
    }

    private void setupToolbar() {
        toolbar.setTitle("Notificaciones");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        listaNotificaciones = new ArrayList<>();
        notificacionesAdapter = new NotificacionesAdapter(listaNotificaciones, this);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));
        rvNotificaciones.setAdapter(notificacionesAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            cargarNotificaciones();
            swipeRefresh.postDelayed(() -> swipeRefresh.setRefreshing(false), 1500);
        });
    }

    private void setupListeners() {
        btnEliminarLeidas.setOnClickListener(v -> {
            eliminarNotificacionesLeidas();
        });
    }

    private void cargarNotificaciones() {
        // Mostrar loading
        swipeRefresh.setRefreshing(true);

        // Llamar API para obtener notificaciones reales
        apiService.getNotificaciones(null, null).enqueue(new Callback<ApiResponse<List<Notificacion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Notificacion>>> call, Response<ApiResponse<List<Notificacion>>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Notificacion>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        listaNotificaciones.clear();
                        if (apiResponse.getData() != null) {
                            listaNotificaciones.addAll(apiResponse.getData());
                        }
                        notificacionesAdapter.notifyDataSetChanged();
                        actualizarContadorNoLeidas();
                        actualizarEmptyState();
                    } else {
                        Toast.makeText(NotificacionesActivity.this,
                            "Error al cargar notificaciones: " + apiResponse.getMessage(),
                            Toast.LENGTH_SHORT).show();
                        mostrarEstadoVacio();
                    }
                } else {
                    Toast.makeText(NotificacionesActivity.this,
                        "Error del servidor: " + response.code(),
                        Toast.LENGTH_SHORT).show();
                    mostrarEstadoVacio();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Notificacion>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(NotificacionesActivity.this,
                    "Error de conexión: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
                mostrarEstadoVacio();
            }
        });
    }

    private void mostrarEstadoVacio() {
        listaNotificaciones.clear();

        notificacionesAdapter.notifyDataSetChanged();
        actualizarContadorNoLeidas();
        actualizarEmptyState();
    }

    private void actualizarContadorNoLeidas() {
        int noLeidas = 0;
        for (Notificacion n : listaNotificaciones) {
            if (!n.isLeida()) {
                noLeidas++;
            }
        }

        if (noLeidas > 0) {
            tvNoLeidas.setVisibility(View.VISIBLE);
            tvNoLeidas.setText("No leídas (" + noLeidas + ")");
        } else {
            tvNoLeidas.setVisibility(View.GONE);
        }
    }

    private void actualizarEmptyState() {
        if (listaNotificaciones.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvNotificaciones.setVisibility(View.GONE);
            btnEliminarLeidas.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvNotificaciones.setVisibility(View.VISIBLE);
            btnEliminarLeidas.setVisibility(View.VISIBLE);
        }
    }

    private void eliminarNotificacionesLeidas() {
        List<Notificacion> noLeidas = new ArrayList<>();
        for (Notificacion n : listaNotificaciones) {
            if (!n.isLeida()) {
                noLeidas.add(n);
            }
        }

        int eliminadas = listaNotificaciones.size() - noLeidas.size();
        listaNotificaciones.clear();
        listaNotificaciones.addAll(noLeidas);
        notificacionesAdapter.notifyDataSetChanged();

        Toast.makeText(this, eliminadas + " notificaciones eliminadas", Toast.LENGTH_SHORT).show();

        actualizarContadorNoLeidas();
        actualizarEmptyState();
    }

    @Override
    public void onAccionClick(Notificacion notificacion) {
        // Marcar como leída usando la API
        if (!notificacion.isLeida()) {
            marcarComoLeida(notificacion);
        }

        // Navegar según acción
        switch (notificacion.getAccion()) {
            case "VER_TAREA":
            case "ENTREGAR":
                Intent intentTarea = new Intent(this, TareaDetalleActivity.class);
                intentTarea.putExtra("TAREA_ID", notificacion.getReferenciaId());
                startActivity(intentTarea);
                break;
            case "VER_NOTA":
                Toast.makeText(this, "Ver calificación de: " + notificacion.getMensaje(), Toast.LENGTH_SHORT).show();
                // TODO: Navegar a detalle de calificación
                break;
            default:
                Toast.makeText(this, notificacion.getMensaje(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNotificacionClick(Notificacion notificacion) {
        // Marcar como leída al tocar usando la API
        if (!notificacion.isLeida()) {
            marcarComoLeida(notificacion);
        }
    }

    private void marcarComoLeida(Notificacion notificacion) {
        apiService.marcarNotificacionLeida(notificacion.getId()).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Actualizar localmente
                        notificacion.setLeida(true);
                        notificacionesAdapter.notifyDataSetChanged();
                        actualizarContadorNoLeidas();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                // Si falla la API, actualizar solo localmente
                notificacion.setLeida(true);
                notificacionesAdapter.notifyDataSetChanged();
                actualizarContadorNoLeidas();
            }
        });
    }
}

