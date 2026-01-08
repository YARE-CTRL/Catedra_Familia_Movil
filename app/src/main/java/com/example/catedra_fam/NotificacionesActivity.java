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
import com.example.catedra_fam.models.Notificacion;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

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
        // TODO: Llamar a API real
        listaNotificaciones.clear();
        listaNotificaciones.addAll(getMockNotificaciones());
        notificacionesAdapter.notifyDataSetChanged();

        actualizarContadorNoLeidas();
        actualizarEmptyState();
    }

    private List<Notificacion> getMockNotificaciones() {
        List<Notificacion> notificaciones = new ArrayList<>();

        // No leídas
        notificaciones.add(new Notificacion(
            "1",
            "nueva_tarea",
            "Nueva tarea asignada",
            "\"Receta familiar tradicional\" para Juan Pérez (5° A)",
            "Hace 2 horas",
            false,
            "VER_TAREA",
            "tarea_006"
        ));

        notificaciones.add(new Notificacion(
            "2",
            "recordatorio",
            "Recordatorio de tarea",
            "La tarea \"Lectura en familia\" vence mañana",
            "Hace 5 horas",
            false,
            "ENTREGAR",
            "tarea_001"
        ));

        // Leídas
        notificaciones.add(new Notificacion(
            "3",
            "calificada",
            "Tarea calificada",
            "\"Juego de roles en familia\" ha sido calificada: Alto (4.5)",
            "27 Nov, 10:30am",
            true,
            "VER_NOTA",
            "entrega_002"
        ));

        notificaciones.add(new Notificacion(
            "4",
            "anuncio",
            "Reunión de padres",
            "Se convoca a reunión de padres el 30 de noviembre a las 2pm en el salón principal",
            "26 Nov, 8:00am",
            true,
            "VER",
            null
        ));

        notificaciones.add(new Notificacion(
            "5",
            "calificada",
            "Tarea calificada",
            "\"Conversación sobre valores\" ha sido calificada: Superior (5.0)",
            "20 Nov, 3:45pm",
            true,
            "VER_NOTA",
            "entrega_003"
        ));

        notificaciones.add(new Notificacion(
            "6",
            "nueva_tarea",
            "Nueva tarea asignada",
            "\"Caminata ecológica\" para Ana Pérez (3° B)",
            "15 Nov, 9:00am",
            true,
            "VER_TAREA",
            "tarea_005"
        ));

        return notificaciones;
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
        // Marcar como leída
        notificacion.setLeida(true);
        notificacionesAdapter.notifyDataSetChanged();
        actualizarContadorNoLeidas();

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
        // Marcar como leída al tocar
        if (!notificacion.isLeida()) {
            notificacion.setLeida(true);
            notificacionesAdapter.notifyDataSetChanged();
            actualizarContadorNoLeidas();
        }
    }
}

