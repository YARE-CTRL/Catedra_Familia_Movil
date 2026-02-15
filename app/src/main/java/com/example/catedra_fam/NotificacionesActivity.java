package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.catedra_fam.adapters.NotificacionesAdapter;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.Notificacion;
// ✅ NUEVO IMPORT - Para paginación
import com.example.catedra_fam.models.NotificacionesResponse;
import com.example.catedra_fam.utils.DialogHelper;
import com.google.android.material.button.MaterialButton;
// ✅ IMPORT - Para confirmación de eliminación
import androidx.appcompat.app.AlertDialog;
// ✅ IMPORT - Para Snackbar con undo
import com.google.android.material.snackbar.Snackbar;

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
    // ✅ NUEVO - Botón para eliminar todas
    private MaterialButton btnEliminarTodas;

    // Data
    private NotificacionesAdapter notificacionesAdapter;
    private List<Notificacion> listaNotificaciones;
    private ApiService apiService;

    // ✅ NUEVO - Variables para paginación
    private int paginaActual = 1;
    private final int LIMITE_POR_PAGINA = 20;
    private int totalNotificaciones = 0;
    private int noLeidas = 0;
    private boolean cargandoDatos = false;

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
        // btnEliminarTodas no existe en el layout - funcionalidad opcional deshabilitada
        btnEliminarTodas = null;
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

        // ✅ CONFIGURAR SWIPE-TO-DELETE
        setupSwipeToDelete();
    }

    /**
     * ✅ NUEVO MÉTODO - Configurar swipe-to-delete con ItemTouchHelper
     */
    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No drag & drop, solo swipe
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Notificacion notificacion = notificacionesAdapter.getItem(position);

                if (notificacion != null) {
                    // Eliminar con confirmación via Snackbar
                    eliminarConSnackbar(notificacion, position);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(rvNotificaciones);
    }

    /**
     * ✅ NUEVO MÉTODO - Eliminar con Snackbar y opción de deshacer
     */
    private void eliminarConSnackbar(Notificacion notificacion, int position) {
        // Remover temporalmente de la vista
        notificacionesAdapter.removeItem(position);

        // Mostrar Snackbar con opción de deshacer
        Snackbar.make(rvNotificaciones, "Notificación eliminada", Snackbar.LENGTH_LONG)
            .setAction("Deshacer", v -> {
                // Restaurar notificación
                listaNotificaciones.add(position, notificacion);
                notificacionesAdapter.notifyItemInserted(position);
            })
            .addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event != DISMISS_EVENT_ACTION) {
                        // Usuario no deshizo - eliminar definitivamente
                        eliminarNotificacionDefinitivamente(notificacion);
                    }
                }
            })
            .show();
    }

    /**
     * ✅ NUEVO MÉTODO - Eliminar definitivamente del backend
     */
    private void eliminarNotificacionDefinitivamente(Notificacion notificacion) {
        apiService.eliminarNotificacion(notificacion.getId())
            .enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Actualizar contadores
                        totalNotificaciones--;
                        if (!notificacion.isLeida()) {
                            noLeidas--;
                        }
                        actualizarContadorNoLeidas();
                        actualizarEmptyState();
                    } else {
                        // Error - volver a agregar a la lista
                        listaNotificaciones.add(notificacion);
                        notificacionesAdapter.notifyDataSetChanged();
                        Snackbar.make(rvNotificaciones, "Error al eliminar", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    // Error de conexión - volver a agregar
                    listaNotificaciones.add(notificacion);
                    notificacionesAdapter.notifyDataSetChanged();
                    Snackbar.make(rvNotificaciones, "Error de conexión", Snackbar.LENGTH_SHORT).show();
                }
            });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            paginaActual = 1; // Reset a primera página
            cargarNotificaciones();
        });
    }

    private void setupListeners() {
        btnEliminarLeidas.setOnClickListener(v -> {
            eliminarNotificacionesLeidas();
        });

        // ✅ NUEVO - Listener para eliminar todas las notificaciones
        if (btnEliminarTodas != null) {
            btnEliminarTodas.setOnClickListener(v -> {
                confirmarEliminarTodas();
            });
        }

        // ✅ NUEVO - Agregar scroll listener para paginación infinita (opcional)
        rvNotificaciones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !cargandoDatos) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Cargar más cuando estemos cerca del final
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && listaNotificaciones.size() < totalNotificaciones) {
                        paginaActual++;
                        cargarNotificaciones();
                    }
                }
            }
        });
    }

    /**
     * Marca todas las notificaciones como leídas usando el endpoint del backend
     */
    private void marcarTodasComoLeidas() {
        apiService.marcarTodasNotificacionesLeidas().enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DialogHelper.showSuccessDialog(NotificacionesActivity.this,
                        "Todas las notificaciones han sido marcadas como leídas");

                    // Recargar lista de notificaciones
                    cargarNotificaciones();
                } else {
                    DialogHelper.showErrorDialog(NotificacionesActivity.this,
                        "Error al marcar las notificaciones como leídas");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                DialogHelper.showErrorDialog(NotificacionesActivity.this,
                    "Error de conexión: " + t.getMessage());
            }
        });
    }

    /**
     * Marca una notificación específica como leída
     */
    private void marcarNotificacionComoLeida(int notificacionId) {
        apiService.marcarNotificacionLeida(notificacionId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    // Actualizar la lista local
                    cargarNotificaciones();
                } else {
                    DialogHelper.showErrorDialog(NotificacionesActivity.this,
                        "Error al marcar la notificación como leída");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                DialogHelper.showErrorDialog(NotificacionesActivity.this,
                    "Error de conexión: " + t.getMessage());
            }
        });
    }

    private void cargarNotificaciones() {
        if (cargandoDatos) return; // Evitar llamadas duplicadas

        cargandoDatos = true;
        // Mostrar loading
        swipeRefresh.setRefreshing(true);

        // ✅ LLAMADA ACTUALIZADA - Con paginación y nuevo response type
        apiService.getNotificaciones(paginaActual, LIMITE_POR_PAGINA, null, null)
            .enqueue(new Callback<NotificacionesResponse>() {
                @Override
                public void onResponse(Call<NotificacionesResponse> call, Response<NotificacionesResponse> response) {
                    swipeRefresh.setRefreshing(false);
                    cargandoDatos = false;

                    if (response.isSuccessful() && response.body() != null) {
                        NotificacionesResponse notifResponse = response.body();

                        if (notifResponse.isSuccess()) {
                            // ✅ MANEJAR PAGINACIÓN
                            if (paginaActual == 1) {
                                // Primera página - reemplazar todo
                                listaNotificaciones.clear();
                            }

                            if (notifResponse.getData() != null) {
                                listaNotificaciones.addAll(notifResponse.getData());
                            }

                            // ✅ ACTUALIZAR METADATA
                            if (notifResponse.getMeta() != null) {
                                totalNotificaciones = notifResponse.getMeta().getTotal();
                                noLeidas = notifResponse.getMeta().getNoLeidas();
                            }

                            notificacionesAdapter.notifyDataSetChanged();
                            actualizarContadorNoLeidas();
                            actualizarEmptyState();
                        } else {
                            DialogHelper.showErrorDialog(NotificacionesActivity.this,
                                "Error al cargar notificaciones");
                            mostrarEstadoVacio();
                        }
                    } else {
                        DialogHelper.showErrorDialog(NotificacionesActivity.this,
                            "Error del servidor: " + response.code());
                        mostrarEstadoVacio();
                    }
                }

                @Override
                public void onFailure(Call<NotificacionesResponse> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    cargandoDatos = false;
                    DialogHelper.showErrorDialog(NotificacionesActivity.this,
                        "Error de conexión: " + t.getMessage());
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
        // Contar cuántas están leídas
        int leidasCount = 0;
        for (Notificacion n : listaNotificaciones) {
            if (n.isLeida()) {
                leidasCount++;
            }
        }

        if (leidasCount == 0) {
            DialogHelper.showInfoDialog(this, "Sin notificaciones leídas",
                "No hay notificaciones leídas para eliminar.");
            return;
        }

        // Por ahora, sugerir eliminar todas ya que no hay endpoint específico para eliminar solo leídas
        new AlertDialog.Builder(this)
            .setTitle("Eliminar notificaciones leídas")
            .setMessage("Hay " + leidasCount + " notificaciones leídas. " +
                       "¿Quieres eliminar TODAS las notificaciones? " +
                       "(No hay opción para eliminar solo las leídas)")
            .setPositiveButton("Eliminar todas", (dialog, which) -> {
                eliminarTodasNotificaciones();
            })
            .setNegativeButton("Cancelar", null)
            .show();
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
                DialogHelper.showInfoDialog(this, "Calificación", "Ver calificación de: " + notificacion.getMensaje());
                // TODO: Navegar a detalle de calificación
                break;
            default:
                DialogHelper.showInfoDialog(this, "Notificación", notificacion.getMensaje());
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

    @Override
    public void onDeleteClick(Notificacion notificacion, int position) {
        // Usar el mismo sistema que swipe-to-delete
        eliminarConSnackbar(notificacion, position);
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

    /**
     * ✅ NUEVO MÉTODO - Confirmar eliminar todas las notificaciones
     */
    private void confirmarEliminarTodas() {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar todas las notificaciones")
            .setMessage("¿Estás seguro de que quieres eliminar todas las notificaciones? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar", (dialog, which) -> {
                eliminarTodasNotificaciones();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    /**
     * ✅ NUEVO MÉTODO - Eliminar todas las notificaciones
     */
    private void eliminarTodasNotificaciones() {
        swipeRefresh.setRefreshing(true);

        apiService.eliminarTodasNotificaciones()
            .enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    swipeRefresh.setRefreshing(false);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // ✅ LIMPIAR LISTA LOCAL
                        listaNotificaciones.clear();
                        notificacionesAdapter.notifyDataSetChanged();

                        // Resetear contadores
                        totalNotificaciones = 0;
                        noLeidas = 0;
                        paginaActual = 1;

                        actualizarContadorNoLeidas();
                        actualizarEmptyState();

                        Snackbar.make(rvNotificaciones, "Todas las notificaciones eliminadas", Snackbar.LENGTH_SHORT).show();
                    } else {
                        DialogHelper.showErrorDialog(NotificacionesActivity.this,
                            "Error al eliminar las notificaciones");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    DialogHelper.showErrorDialog(NotificacionesActivity.this,
                        "Error de conexión: " + t.getMessage());
                }
            });
    }

    /**
     * ✅ MÉTODO MEJORADO - Actualizar contador usando datos del backend
     */
    private void actualizarContadorNoLeidas() {
        if (noLeidas > 0) {
            tvNoLeidas.setText(noLeidas + " no leídas");
            tvNoLeidas.setVisibility(View.VISIBLE);
        } else {
            tvNoLeidas.setVisibility(View.GONE);
        }

        // Actualizar visibilidad de botones según el estado
        btnEliminarLeidas.setVisibility(listaNotificaciones.isEmpty() ? View.GONE : View.VISIBLE);
        if (btnEliminarTodas != null) {
            btnEliminarTodas.setVisibility(listaNotificaciones.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }
}
