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
import com.google.android.material.button.MaterialButton;
// ✅ IMPORT - Para confirmación de eliminación
import androidx.appcompat.app.AlertDialog;
// ✅ IMPORT - Para Snackbar con undo
import com.google.android.material.snackbar.Snackbar;
// ✅ IMPORT - Para chips de filtros
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

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
    // ✅ NUEVO - Chips de filtros
    private ChipGroup chipGroupFiltros;
    private Chip chipTodas, chipHoy, chip24h, chipSemana, chipNoLeidas;

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

    // ✅ NUEVO - Filtros activos
    private String periodoActual = null; // null, "hoy", "24h", "semana"
    private Boolean filtroLeida = null; // null (todas), true (solo leídas), false (solo no leídas)

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

        // ✅ NUEVO - Inicializar chips de filtros
        chipGroupFiltros = findViewById(R.id.chip_group_filtros);
        chipTodas = findViewById(R.id.chip_todas);
        chipHoy = findViewById(R.id.chip_hoy);
        chip24h = findViewById(R.id.chip_24h);
        chipSemana = findViewById(R.id.chip_semana);
        chipNoLeidas = findViewById(R.id.chip_no_leidas);
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

        // ✅ NUEVO - Listener para chips de filtros
        chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipTodas.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            aplicarFiltro(checkedId);
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
     * ✅ NUEVO MÉTODO - Aplicar filtro según el chip seleccionado
     */
    private void aplicarFiltro(int chipId) {
        if (chipId == R.id.chip_todas) {
            periodoActual = null;
            filtroLeida = null;
        } else if (chipId == R.id.chip_hoy) {
            periodoActual = "hoy";
            filtroLeida = null;
        } else if (chipId == R.id.chip_24h) {
            periodoActual = "24h";
            filtroLeida = null;
        } else if (chipId == R.id.chip_semana) {
            periodoActual = "semana";
            filtroLeida = null;
        } else if (chipId == R.id.chip_no_leidas) {
            periodoActual = null;
            filtroLeida = false; // false = no leídas
        }

        // Resetear a primera página y recargar
        paginaActual = 1;
        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        if (cargandoDatos) return; // Evitar llamadas duplicadas

        cargandoDatos = true;
        // Mostrar loading
        swipeRefresh.setRefreshing(true);

        // ✅ LLAMADA ACTUALIZADA - Con filtros de periodo y leída
        apiService.getNotificaciones(
            paginaActual,
            LIMITE_POR_PAGINA,
            null,           // tipo (puede ser "tarea", "evento", etc)
            filtroLeida,    // filtro de leída/no leída
            periodoActual,  // periodo: "hoy", "24h", "semana"
            null,           // prioridad
            null            // orden
        )
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
                            // Error en la respuesta - solo mostrar toast
                            android.widget.Toast.makeText(NotificacionesActivity.this,
                                "Error al cargar notificaciones",
                                android.widget.Toast.LENGTH_SHORT).show();
                            mostrarEstadoVacio();
                        }
                    } else {
                        // Error del servidor - solo mostrar toast
                        android.widget.Toast.makeText(NotificacionesActivity.this,
                            "Error del servidor: " + response.code(),
                            android.widget.Toast.LENGTH_SHORT).show();
                        mostrarEstadoVacio();
                    }
                }

                @Override
                public void onFailure(Call<NotificacionesResponse> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    cargandoDatos = false;
                    // Error de conexión - solo mostrar toast
                    android.widget.Toast.makeText(NotificacionesActivity.this,
                        "Error de conexión",
                        android.widget.Toast.LENGTH_SHORT).show();
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
            android.widget.Toast.makeText(this,
                "No hay notificaciones leídas para eliminar",
                android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar opción para eliminar todas
        new AlertDialog.Builder(this)
            .setTitle("Eliminar notificaciones")
            .setMessage("Hay " + leidasCount + " notificaciones leídas de " + totalNotificaciones + " total.\n\n" +
                       "¿Deseas eliminar TODAS las notificaciones?")
            .setPositiveButton("Eliminar todas", (dialog, which) -> {
                eliminarTodasNotificaciones();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onAccionClick(Notificacion notificacion) {
        // Marcar como leída de forma asíncrona (sin esperar)
        marcarComoLeidaSilenciosamente(notificacion);

        // Obtener el ID del primer estudiante desde la sesión
        int estudianteId = obtenerEstudianteIdActual();

        // Redirigir inmediatamente al panel de tareas con el estudiante_id correcto
        Intent intentTareas = new Intent(this, TareasActivity.class);
        intentTareas.putExtra("ESTUDIANTE_ID", estudianteId);
        intentTareas.putExtra("ESTUDIANTE_NOMBRE", "Estudiante");
        startActivity(intentTareas);
    }

    @Override
    public void onNotificacionClick(Notificacion notificacion) {
        // Marcar como leída de forma asíncrona (sin esperar)
        marcarComoLeidaSilenciosamente(notificacion);

        // Obtener el ID del primer estudiante desde la sesión
        int estudianteId = obtenerEstudianteIdActual();

        // Redirigir inmediatamente al panel de tareas con el estudiante_id correcto
        Intent intentTareas = new Intent(this, TareasActivity.class);
        intentTareas.putExtra("ESTUDIANTE_ID", estudianteId);
        intentTareas.putExtra("ESTUDIANTE_NOMBRE", "Estudiante");
        startActivity(intentTareas);
    }

    /**
     * Obtiene el ID del estudiante actual desde SharedPreferences
     * @return ID del estudiante o 1 por defecto
     */
    private int obtenerEstudianteIdActual() {
        // Intentar obtener desde SharedPreferences (si se guardó en login/MainActivity)
        android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int estudianteId = prefs.getInt("estudianteId", 0);

        // Si no hay ID guardado, obtener el primer estudiante del acudiente
        if (estudianteId == 0) {
            // Como último recurso, usar ID 1 (primer estudiante de prueba)
            estudianteId = 1;
        }

        android.util.Log.d("NotificacionesActivity", "📍 ID Estudiante obtenido: " + estudianteId);
        return estudianteId;
    }

    @Override
    public void onDeleteClick(Notificacion notificacion, int position) {
        // Usar el mismo sistema que swipe-to-delete
        eliminarConSnackbar(notificacion, position);
    }

    /**
     * Marca una notificación como leída de forma silenciosa (sin mensajes al usuario)
     * La llamada es asíncrona y no bloquea la navegación
     */
    private void marcarComoLeidaSilenciosamente(Notificacion notificacion) {
        // Si ya está leída, no hacer nada
        if (notificacion.isLeida()) {
            return;
        }

        // Marcar localmente de inmediato para mejorar UX
        notificacion.setLeida(true);

        // Llamada asíncrona al backend
        apiService.marcarNotificacionLeida(notificacion.getId()).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Actualizar contador sin mostrar mensaje
                        actualizarContadorNoLeidas();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                // Fallo silencioso - la notificación ya se marcó localmente
                // El usuario no necesita saber de este error técnico
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
                        // Error - mostrar con Toast
                        android.widget.Toast.makeText(NotificacionesActivity.this,
                            "Error al eliminar las notificaciones",
                            android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    // Error de conexión - mostrar con Toast
                    android.widget.Toast.makeText(NotificacionesActivity.this,
                        "Error de conexión",
                        android.widget.Toast.LENGTH_SHORT).show();
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
