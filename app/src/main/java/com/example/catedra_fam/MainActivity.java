package com.example.catedra_fam;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catedra_fam.adapters.HijosAdapter;
import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.Estudiante;
import com.example.catedra_fam.models.EstudianteInfo;
import com.example.catedra_fam.models.EstadisticasTareas; // ✅ NUEVO
import com.example.catedra_fam.models.Hijo;
import com.example.catedra_fam.models.DeviceInfo;
import com.example.catedra_fam.models.Notificacion;
import com.example.catedra_fam.models.NotificacionesResponse;
import com.example.catedra_fam.models.TareaLista;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Constante para solicitud de permiso de notificaciones
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView rvHijos;
    private MaterialCardView cardAlerta;
    private MaterialCardView btnTareas, btnNotificaciones, btnHistorial, btnAyuda;
    private TextView tvAlerta, tvBadgeTareas, tvBadgeNotificaciones;
    private TextView tvEstadoConexion;
    private TextView badgeNotificaciones; // 🔥 Nuevo badge circular
    private ImageView ivEstadoConexion;

    private HijosAdapter hijosAdapter;
    private List<Hijo> listaHijos;
    private ApiService apiService;

    private int notificacionesNoLeidasCount = 0;
    private boolean notificacionesPermitidas = false;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService(this);

        // Inicializar token de autenticación
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("AUTH_TOKEN", null);

        // Debug: Verificar si tenemos token
        if (authToken != null) {
            Log.d("MainActivity", "✅ Token encontrado: " + authToken.substring(0, Math.min(20, authToken.length())) + "...");
        } else {
            Log.w("MainActivity", "⚠️ No hay token de autenticación disponible");
        }

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupRecyclerView();
        setupListeners();
        
        // 🔥 Mostrar mensaje de bienvenida si viene del login
        String mensajeBienvenida = getIntent().getStringExtra("MENSAJE_BIENVENIDA");
        if (mensajeBienvenida != null) {
            // Usar Snackbar en lugar de Dialog para evitar problemas
            com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                mensajeBienvenida,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
            ).show();
        }

        // 🔥 Solicitar permiso de notificaciones para Android 13+
        solicitarPermisoNotificaciones();
        
        inicializarFCM();
        cargarMisEstudiantes();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        rvHijos = findViewById(R.id.rv_hijos);
        cardAlerta = findViewById(R.id.card_alerta);
        tvAlerta = findViewById(R.id.tv_alerta);

        btnTareas = findViewById(R.id.btn_tareas);
        btnNotificaciones = findViewById(R.id.btn_notificaciones);
        btnHistorial = findViewById(R.id.btn_historial);
        btnAyuda = findViewById(R.id.btn_ayuda);

        tvBadgeTareas = findViewById(R.id.tv_badge_tareas);
        tvBadgeNotificaciones = findViewById(R.id.tv_badge_notificaciones);
        badgeNotificaciones = findViewById(R.id.badge_notificaciones); // 🔥 Nuevo badge circular
        tvEstadoConexion = findViewById(R.id.tv_estado_conexion);
        ivEstadoConexion = findViewById(R.id.iv_estado_conexion);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Obtener nombre del usuario desde SharedPreferences
            String nombreUsuario = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .getString("nombre_usuario", "Usuario");
            getSupportActionBar().setTitle("Bienvenido, " + nombreUsuario);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Configurar header del navigation drawer con datos de SharedPreferences
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombreHeader = headerView.findViewById(R.id.tv_nombre_header);
        TextView tvCorreoHeader = headerView.findViewById(R.id.tv_correo_header);

        String nombreUsuario = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getString("nombre_usuario", "Usuario");
        String correoUsuario = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getString("correo_usuario", "");

        if (tvNombreHeader != null) {
            tvNombreHeader.setText(nombreUsuario);
        }
        if (tvCorreoHeader != null) {
            tvCorreoHeader.setText(correoUsuario);
        }
    }

    private void setupRecyclerView() {
        listaHijos = new ArrayList<>();
        hijosAdapter = new HijosAdapter(listaHijos, this::onHijoClick);

        rvHijos.setLayoutManager(new LinearLayoutManager(this));
        rvHijos.setAdapter(hijosAdapter);
        rvHijos.setNestedScrollingEnabled(false);
    }

    private void setupListeners() {
        // Banner de alerta
        cardAlerta.setOnClickListener(v -> {
            if (!listaHijos.isEmpty()) {
                Hijo primerHijo = listaHijos.get(0);
                abrirTareasDeHijo(primerHijo);
            }
        });

        // Botón Tareas
        btnTareas.setOnClickListener(v -> {
            if (!listaHijos.isEmpty()) {
                Hijo primerHijo = listaHijos.get(0);
                abrirTareasDeHijo(primerHijo);
            }
        });

        // 🧪 DEBUG: Long press en tareas para recargar estadísticas
        btnTareas.setOnLongClickListener(v -> {
            Log.d("MainActivity", "🧪 RECARGANDO ESTADÍSTICAS MANUALMENTE");
            if (!listaHijos.isEmpty()) {
                for (Hijo hijo : listaHijos) {
                    Log.d("MainActivity", "🔄 Recargando estadísticas para: " + hijo.getNombreCompleto());
                    cargarEstadisticasTareas(hijo);
                }

                // Mostrar dialog informativo
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("🧪 Debug - Recarga de Estadísticas")
                    .setMessage("Se están recargando las estadísticas de tareas.\n\nRevisa los logs con filtro 'MainActivity' para ver el progreso.\n\nEsto es temporal para debugging.")
                    .setPositiveButton("OK", null)
                    .show();
            }
            return true;
        });

        // Botón Notificaciones
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificacionesActivity.class);
            startActivity(intent);
        });

        // Botón Historial
        btnHistorial.setOnClickListener(v -> {
            if (!listaHijos.isEmpty()) {
                Hijo primerHijo = listaHijos.get(0);
                Intent intent = new Intent(this, HistorialActivity.class);
                intent.putExtra("ESTUDIANTE_ID", primerHijo.getId());
                intent.putExtra("ESTUDIANTE_NOMBRE", primerHijo.getNombres());
                intent.putExtra("ESTUDIANTE_CURSO", primerHijo.getCurso());
                startActivity(intent);
            }
        });

        // Botón Ayuda
        btnAyuda.setOnClickListener(v -> {
            Intent intent = new Intent(this, SoporteActivity.class);
            startActivity(intent);
        });

        // 🧪 BOTÓN DE PRUEBA TEMPORAL PARA CONTADOR TAREAS (remover en producción)
        btnAyuda.setOnLongClickListener(v -> {
            Log.d("MainActivity", "🧪 Ejecutando prueba de contador de tareas");

            // Mostrar dialog explicativo
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("🧪 Debug - Test Contador Tareas")
                .setMessage("Probando contador de tareas pendientes.\n\nEsto va a consultar directamente las estadísticas y actualizar el contador.\n\nRevisa los logs con filtro 'MainActivity' para ver los valores.")
                .setPositiveButton("Probar Contador", (dialog, which) -> {
                    if (!listaHijos.isEmpty()) {
                        Hijo primerHijo = listaHijos.get(0);
                        Log.d("MainActivity", "🧪 TESTING CONTADOR - Consultando estadísticas para: " + primerHijo.getNombreCompleto());
                        cargarEstadisticasTareas(primerHijo);
                    } else {
                        Log.w("MainActivity", "🧪 TESTING CONTADOR - No hay hijos para probar");
                        Toast.makeText(this, "No hay estudiantes para probar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

            return true; // Consumir el evento
        });
    }

    private void abrirTareasDeHijo(Hijo hijo) {
        Intent intent = new Intent(this, TareasActivity.class);
        intent.putExtra("ESTUDIANTE_ID", hijo.getId());
        intent.putExtra("ESTUDIANTE_NOMBRE", hijo.getNombres());
        intent.putExtra("ESTUDIANTE_CURSO", hijo.getCurso());
        startActivity(intent);
    }

    private void onHijoClick(Hijo hijo) {
        abrirTareasDeHijo(hijo);
    }

    // Definir tipo para evitar problemas con genéricos anidados
    private interface EstudiantesCallback extends Callback<ApiResponse<EstudianteInfo>> {}

    private void cargarMisEstudiantes() {
        mostrarLoading(true);
        
        apiService.getMisEstudiantes().enqueue(new Callback<ApiResponse<EstudianteInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<EstudianteInfo>> call,
                                   Response<ApiResponse<EstudianteInfo>> response) {
                mostrarLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<EstudianteInfo> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        EstudianteInfo estudianteInfo = apiResponse.getData();

                        // ✅ CAMBIO BREAKING: Ahora siempre es array
                        List<EstudianteInfo.EstudianteDetalle> estudiantes = estudianteInfo.getEstudiantes();

                        // DEBUG: Verificar datos recibidos
                        if (estudiantes.isEmpty()) {
                            Toast.makeText(MainActivity.this, "No se encontraron estudiantes", Toast.LENGTH_SHORT).show();
                            cargarEstudiantesGuardados();
                            return;
                        }
                        
                        // Convertir EstudianteDetalle -> Hijo
                        listaHijos.clear();
                        for (EstudianteInfo.EstudianteDetalle estudiante : estudiantes) {
                            Hijo hijo = convertirEstudianteDetalleAHijo(estudiante);
                            listaHijos.add(hijo);
                        }
                        
                        // ✅ GUARDAR ID del primer estudiante en SharedPreferences
                        if (!listaHijos.isEmpty()) {
                            int primerEstudianteId = listaHijos.get(0).getId();
                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            prefs.edit().putInt("estudianteId", primerEstudianteId).apply();
                            Log.d("MainActivity", "✅ Guardado estudianteId: " + primerEstudianteId);
                        }

                        hijosAdapter.notifyDataSetChanged();
                        actualizarBadgesYAlertas();
                    } else {
                        String errorMsg = "Error al cargar estudiantes";
                        if (apiResponse.getMessage() != null) {
                            errorMsg += ": " + apiResponse.getMessage();
                        }
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        // Cargar datos guardados como fallback
                        cargarEstudiantesGuardados();
                    }
                } else {
                    String errorMsg = "Error en el servidor";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMsg = "Error " + response.code() + ": " + errorBody;
                        }
                    } catch (Exception e) {
                        errorMsg = "Error " + response.code();
                    }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    // Cargar datos guardados como fallback
                    cargarEstudiantesGuardados();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<EstudianteInfo>> call, Throwable t) {
                mostrarLoading(false);
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Cargar datos guardados como fallback
                cargarEstudiantesGuardados();
            }
        });
    }

    private void cargarEstudiantesGuardados() {
        // Cargar estudiantes guardados en SharedPreferences (fallback offline)
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int estudiantesCount = prefs.getInt("estudiantes_count", 0);
        
        if (estudiantesCount > 0) {
            listaHijos.clear();
            
            for (int i = 0; i < estudiantesCount; i++) {
                String prefix = "estudiante_" + i + "_";
                
                Hijo hijo = new Hijo();
                hijo.setId(prefs.getInt(prefix + "id", 0));
                hijo.setNombres(prefs.getString(prefix + "nombre", ""));
                hijo.setApellidos(prefs.getString(prefix + "apellido", ""));
                hijo.setCursoNombre(prefs.getString(prefix + "curso_nombre", ""));
                
                // Valores temporales hasta cargar tareas
                hijo.setTareasPendientes(0);
                hijo.setTareasCompletadas(0);
                hijo.setTareasCalificadas(0);
                hijo.setEstado("al_dia");
                
                listaHijos.add(hijo);
            }
            
            hijosAdapter.notifyDataSetChanged();
            actualizarBadgesYAlertas();
        } else {
            // Si no hay datos guardados, cargar datos mock como último recurso
            cargarDatosMock();
        }
    }

    private void cargarDatosMock() {
        listaHijos.clear();

        // Hijo 1 - Con tareas pendientes urgentes
        Hijo hijo1 = new Hijo();
        hijo1.setId(1);
        hijo1.setNombres("Santiago");
        hijo1.setApellidos("García López");
        hijo1.setCursoNombre("5° Primaria - Sección A");
        hijo1.setTareasPendientes(4);
        hijo1.setTareasCompletadas(8);
        hijo1.setTareasCalificadas(6);
        hijo1.setProximaTarea("Lectura en familia: Capítulo 3");
        hijo1.setFechaProximaTarea("2026-01-30");
        hijo1.setEstado("con_pendientes");
        listaHijos.add(hijo1);

        // Hijo 2 - Con tareas vencidas
        Hijo hijo2 = new Hijo();
        hijo2.setId(2);
        hijo2.setNombres("Valentina");
        hijo2.setApellidos("García López");
        hijo2.setCursoNombre("3° Primaria - Sección B");
        hijo2.setTareasPendientes(2);
        hijo2.setTareasCompletadas(5);
        hijo2.setTareasCalificadas(4);
        hijo2.setProximaTarea("Dibujo familiar");
        hijo2.setFechaProximaTarea("2026-02-01");
        hijo2.setEstado("con_vencidas");
        listaHijos.add(hijo2);

        // Hijo 3 - Al día
        Hijo hijo3 = new Hijo();
        hijo3.setId(3);
        hijo3.setNombres("Mateo");
        hijo3.setApellidos("García López");
        hijo3.setCursoNombre("1° Primaria - Sección A");
        hijo3.setTareasPendientes(1);
        hijo3.setTareasCompletadas(10);
        hijo3.setTareasCalificadas(9);
        hijo3.setProximaTarea("Canción de los números");
        hijo3.setFechaProximaTarea("2026-02-05");
        hijo3.setEstado("al_dia");
        listaHijos.add(hijo3);

        hijosAdapter.notifyDataSetChanged();
        actualizarBadgesYAlertas();
    }

    private void mostrarLoading(boolean mostrar) {
        // ✅ IMPLEMENTACIÓN - Indicador de carga simple
        // Mostrar/ocultar RecyclerView
        if (rvHijos != null) {
            rvHijos.setVisibility(mostrar ? View.GONE : View.VISIBLE);
        }

        // Mostrar estado de carga en el indicador de conexión si existe
        if (tvEstadoConexion != null && ivEstadoConexion != null) {
            if (mostrar) {
                tvEstadoConexion.setText("Cargando...");
                tvEstadoConexion.setVisibility(View.VISIBLE);
            } else {
                tvEstadoConexion.setVisibility(View.GONE);
            }
        }

        // Log para debugging
        Log.d("MainActivity", "Loading indicator: " + (mostrar ? "SHOWN" : "HIDDEN"));
    }

    /**
     * Convierte EstudianteDetalle (nueva estructura del backend) a Hijo para UI
     */
    private Hijo convertirEstudianteDetalleAHijo(EstudianteInfo.EstudianteDetalle estudiante) {
        Hijo hijo = new Hijo();

        try {
            hijo.setId(estudiante.getId());
            hijo.setNombres(estudiante.getNombres());
            hijo.setApellidos(estudiante.getApellidos());
            hijo.setCursoNombre(estudiante.getGrado()); // Usar grado como curso

            // Valores por defecto (se actualizarán cuando se carguen las tareas)
            hijo.setTareasPendientes(0);
            hijo.setTareasVencidas(0);
            hijo.setTareasCompletadas(0);
            hijo.setTareasCalificadas(0);

            // Estado por defecto
            hijo.setEstado("al_dia");
            hijo.setProximaTarea("Sin tareas pendientes");
            hijo.setFechaProximaTarea("--");

        } catch (Exception e) {
            Log.e("MainActivity", "Error convirtiendo EstudianteDetalle a Hijo", e);
        }

        // Cargar estadísticas reales de tareas desde la API
        cargarEstadisticasTareas(hijo);

        return hijo;
    }

    private Hijo convertirEstudianteAHijo(Estudiante estudiante) {
        Hijo hijo = new Hijo();
        
        try {
            hijo.setId(estudiante.getId());
            hijo.setNombres(estudiante.getNombres());
            hijo.setApellidos(estudiante.getApellidos());

            if (estudiante.getCurso() != null) {
                hijo.setCursoNombre(estudiante.getCurso().getNombre());
            }

            hijo.setTareasPendientes(estudiante.getTareasPendientes());
            hijo.setTareasCompletadas(estudiante.getTareasCompletadas());
            hijo.setTareasCalificadas(estudiante.getTareasVencidas()); // Usar tareasVencidas como calificadas

            if (estudiante.getProximaTarea() != null) {
                hijo.setProximaTarea(estudiante.getProximaTarea().getTitulo());
                hijo.setFechaProximaTarea(estudiante.getProximaTarea().getFechaVencimiento());
            }

            // Determinar estado
            if (estudiante.getTareasVencidas() > 0) {
                hijo.setEstado("con_vencidas");
            } else if (estudiante.getTareasPendientes() > 0) {
                hijo.setEstado("con_pendientes");
            } else {
                hijo.setEstado("al_dia");
            }
        } catch (Exception e) {
            // Si hay error en conversión, crear hijo básico
            Toast.makeText(this, "Error convirtiendo estudiante: " + e.getMessage(), Toast.LENGTH_LONG).show();
            hijo.setId(estudiante != null ? estudiante.getId() : 0);
            hijo.setNombres("Error");
            hijo.setApellidos("Conversión");
            hijo.setCursoNombre("N/A");
            hijo.setTareasPendientes(0);
            hijo.setTareasCompletadas(0);
            hijo.setTareasCalificadas(0);
            hijo.setEstado("error");
        }

        return hijo;
    }

    private void actualizarBadgesYAlertas() {
        int totalPendientes = 0;
        int totalVencidas = 0;
        int totalPorAtender = 0;

        Log.d("MainActivity", "🔄 Actualizando badges - Revisando " + listaHijos.size() + " hijos:");

        for (Hijo h : listaHijos) {
            int pendientes = h.getTareasPendientes();
            int vencidas = h.getTareasVencidas();
            int porAtender = h.getTareasPorAtender(); // Usar el método que consulta porAtender del backend

            totalPendientes += pendientes;
            totalVencidas += vencidas;
            totalPorAtender += porAtender;

            Log.d("MainActivity", "   📊 " + h.getNombreCompleto() + " - P:" + pendientes + " V:" + vencidas + " PA:" + porAtender);
        }

        Log.d("MainActivity", "🎯 Totales - Pendientes: " + totalPendientes + ", Vencidas: " + totalVencidas + ", Total: " + totalPorAtender);

        // Actualizar badge con total (pendientes + vencidas)
        if (totalVencidas > 0) {
            String badgeText = totalPorAtender + " por atender (" + totalVencidas + " vencidas)";
            tvBadgeTareas.setText(badgeText);
            Log.d("MainActivity", "📋 Badge actualizado: " + badgeText);
        } else {
            String badgeText = totalPorAtender + " pendientes";
            tvBadgeTareas.setText(badgeText);
            Log.d("MainActivity", "📋 Badge actualizado: " + badgeText);
        }

        // Actualizar badge de notificaciones con contador real
        actualizarBadgeNotificaciones();

        if (totalPorAtender > 0) {
            cardAlerta.setVisibility(View.VISIBLE);
            if (totalVencidas > 0) {
                tvAlerta.setText("⚠️ " + totalVencidas + " vencidas, " + totalPendientes + " pendientes");
            } else {
                tvAlerta.setText(totalPorAtender + " tareas pendientes");
            }
        } else {
            cardAlerta.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            // Ya estamos en inicio, solo cerramos el drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        } else if (id == R.id.nav_mis_hijos) {
            // Mostrar lista de hijos (scroll al RecyclerView)
            drawerLayout.closeDrawer(GravityCompat.START);
            rvHijos.smoothScrollToPosition(0);
            Toast.makeText(this, "Aquí están tus hijos", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_tareas) {
            // Abrir tareas del primer hijo o mostrar selector si hay varios
            drawerLayout.closeDrawer(GravityCompat.START);
            if (!listaHijos.isEmpty()) {
                if (listaHijos.size() == 1) {
                    abrirTareasDeHijo(listaHijos.get(0));
                } else {
                    mostrarSelectorHijo();
                }
            } else {
                Toast.makeText(this, "No hay hijos registrados", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_notificaciones) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, NotificacionesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_historial) {
            drawerLayout.closeDrawer(GravityCompat.START);
            if (!listaHijos.isEmpty()) {
                if (listaHijos.size() == 1) {
                    abrirHistorialDeHijo(listaHijos.get(0));
                } else {
                    mostrarSelectorHijoParaHistorial();
                }
            } else {
                Toast.makeText(this, "No hay hijos registrados", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_ayuda) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, SoporteActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_cerrar_sesion) {
            drawerLayout.closeDrawer(GravityCompat.START);
            confirmarCerrarSesion();
            return true;
        }

        return true;
    }

    private void mostrarSelectorHijo() {
        String[] nombresHijos = new String[listaHijos.size()];
        for (int i = 0; i < listaHijos.size(); i++) {
            nombresHijos[i] = listaHijos.get(i).getNombres() + " " + listaHijos.get(i).getApellidos();
        }

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Selecciona un hijo")
                .setItems(nombresHijos, (dialog, which) -> {
                    abrirTareasDeHijo(listaHijos.get(which));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarSelectorHijoParaHistorial() {
        String[] nombresHijos = new String[listaHijos.size()];
        for (int i = 0; i < listaHijos.size(); i++) {
            nombresHijos[i] = listaHijos.get(i).getNombres() + " " + listaHijos.get(i).getApellidos();
        }

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Selecciona un hijo")
                .setItems(nombresHijos, (dialog, which) -> {
                    abrirHistorialDeHijo(listaHijos.get(which));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirHistorialDeHijo(Hijo hijo) {
        Intent intent = new Intent(this, HistorialActivity.class);
        intent.putExtra("ESTUDIANTE_ID", hijo.getId());
        intent.putExtra("ESTUDIANTE_NOMBRE", hijo.getNombres());
        intent.putExtra("ESTUDIANTE_CURSO", hijo.getCursoNombre());
        startActivity(intent);
    }

    private void confirmarCerrarSesion() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, salir", (dialog, which) -> {
                    cerrarSesion();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        // Limpiar preferencias
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Volver al login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void inicializarFCM() {
        // Obtener token FCM y guardarlo si no existe
        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();
                Log.d("MainActivity", "FCM Token: " + token);
                
                // 📱 MOSTRAR TOKEN PARA DESARROLLO - FÁCIL COPIAR
                Log.d("TOKEN_FCM_COPIAR", "====================================");
                Log.d("TOKEN_FCM_COPIAR", "TOKEN FCM PARA COPIAR:");
                Log.d("TOKEN_FCM_COPIAR", token);
                Log.d("TOKEN_FCM_COPIAR", "====================================");
                
                // Mostrar en Toast para desarrollo
Toast.makeText(this, "Token FCM copiado en LogCat", Toast.LENGTH_LONG).show();
                
                // Guardar token en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                prefs.edit().putString("FCM_TOKEN", token).apply();
                
                // Si hay sesión activa, registrar token en backend
                String authToken = prefs.getString("AUTH_TOKEN", null);
                if (authToken != null) {
                    registrarFCMToken(token, authToken);
                }
            });
    }

    private void registrarFCMToken(String fcmToken, String authToken) {
        // ✅ Crear DeviceInfo con campo plataforma actualizado (Fix Backend 16-Feb-2026)
        DeviceInfo deviceInfo = new DeviceInfo(
            fcmToken,
            android.os.Build.MODEL,           // dispositivo: "moto e40"
            "Android",                        // ✅ plataforma: "Android" (capitalizado)
            "android",                        // sistemaOperativo: "android" (legacy)
            "1.0.0"                          // versionApp actualizada
        );

        Log.d("MainActivity", "📡 Registrando token FCM: " + deviceInfo.toString());

        apiService.registerFCMToken("Bearer " + authToken, deviceInfo)
            .enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("MainActivity", "✅ FCM Token registrado exitosamente en backend");
                    } else if (response.code() == 404) {
                        Log.d("MainActivity", "⚠️ Endpoint FCM no implementado aún - token guardado localmente");
                        // Token se guarda en SharedPreferences para intentar registrar más tarde
                    } else {
                        Log.w("MainActivity", "⚠️ Error registrando FCM token: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    Log.d("MainActivity", "⚠️ Endpoint FCM no disponible (normal si no está implementado): " + t.getMessage());
                    // Token se mantiene localmente para intentar más tarde
                }
            });
    }

    /**
     * 🔥 Solicita permiso de notificaciones para Android 13+ (API 33+)
     * Este método es CRÍTICO para que las notificaciones funcionen en Android 13+
     */
    private void solicitarPermisoNotificaciones() {
        // Solo para Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verificar si el permiso ya está concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                
                Log.d("MainActivity", "Solicitando permiso de notificaciones para Android 13+");
                
                // Mostrar explicación (opcional pero recomendado)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, 
                    Manifest.permission.POST_NOTIFICATIONS)) {
                    
                    // Mostrar diálogo explicativo
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Permiso de Notificaciones Requerido")
                        .setMessage("Para recibir alertas importantes sobre tareas y eventos de tus hijos, necesitamos tu permiso para enviar notificaciones.")
                        .setPositiveButton("Conceder", (dialog, which) -> {
                            // Solicitar permiso
                            ActivityCompat.requestPermissions(
                                this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            Log.w("MainActivity", "Usuario denegó permiso de notificaciones");
                            Toast.makeText(this, "No podrás recibir notificaciones importantes", Toast.LENGTH_LONG).show();
                        })
                        .show();
                } else {
                    // Solicitar permiso directamente (sin explicación)
                    ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    );
                }
            } else {
                Log.d("MainActivity", "Permiso de notificaciones ya concedido");
            }
        } else {
            // Para Android < 13, el permiso no se solicita en runtime
            Log.d("MainActivity", "Android < 13, no se requiere permiso runtime para notificaciones");
        }
    }

    /**
     * 🔥 Maneja la respuesta del usuario a la solicitud de permiso
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "✅ Permiso de notificaciones concedido");
                Toast.makeText(this, "¡Gracias! Ahora recibirás notificaciones importantes", Toast.LENGTH_SHORT).show();
                
                // Re-inicializar FCM después de conceder el permiso
                inicializarFCM();
                
            } else {
                Log.w("MainActivity", "❌ Permiso de notificaciones denegado");
                Toast.makeText(this, "No podrás recibir notificaciones importantes. Puedes activarlas en Configuración.", Toast.LENGTH_LONG).show();
                
                // Opcional: Redirigir a configuración de la app
                // Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                // intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                // startActivity(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar contador de notificaciones cada vez que regresa a la activity
        cargarContadorNotificaciones();
        verificarPermisoNotificaciones();
    }

    private void verificarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificacionesPermitidas = ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            notificacionesPermitidas = true; // En versiones anteriores están habilitadas por defecto
        }

        if (!notificacionesPermitidas) {
            // Mostrar indicador visual de que las notificaciones están deshabilitadas
            tvBadgeNotificaciones.setText("⚠️ Deshabilitadas");
            tvBadgeNotificaciones.setTextColor(getColor(android.R.color.holo_orange_dark));
        }
    }

    /**
     * Carga el contador de notificaciones no leídas desde la API
     * Según especificación backend: GET /api/movil/notificaciones?leida=false
     */
    private void cargarContadorNotificaciones() {
        // ✅ CORREGIDO: Usar "AppPrefs" y "AUTH_TOKEN" (16 Feb 2026)
        String authToken = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            .getString("AUTH_TOKEN", null);

        if (authToken == null) return;

        apiService.getNotificaciones(1, 50, null, false, null, null, null).enqueue(new Callback<NotificacionesResponse>() {
            @Override
            public void onResponse(Call<NotificacionesResponse> call, Response<NotificacionesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NotificacionesResponse notifResponse = response.body();

                    if (notifResponse.isSuccess() && notifResponse.getData() != null) {
                        // Usar meta del backend si está disponible
                        int noLeidas = 0;
                        if (notifResponse.getMeta() != null) {
                            noLeidas = notifResponse.getMeta().getNoLeidas();
                        } else {
                            // Fallback: contar manualmente
                            for (Notificacion notif : notifResponse.getData()) {
                                if (!notif.isLeida()) {
                                    noLeidas++;
                                }
                            }
                        }

                        // Actualizar contador local
                        notificacionesNoLeidasCount = noLeidas;
                        SharedPreferences prefs = getSharedPreferences("notification_counter", MODE_PRIVATE);
                        prefs.edit().putInt("unread_count", noLeidas).apply();

                        // Actualizar badge
                        actualizarBadgeNotificaciones();

                        Log.d("MainActivity", "Contador notificaciones cargado: " + noLeidas);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificacionesResponse> call, Throwable t) {
                // En caso de error, usar contador local
                SharedPreferences prefs = getSharedPreferences("notification_counter", MODE_PRIVATE);
                notificacionesNoLeidasCount = prefs.getInt("unread_count", 0);
                actualizarBadgeNotificaciones();

                Log.d("MainActivity", "Error cargando contador, usando local: " + notificacionesNoLeidasCount);
            }
        });
    }

    private void actualizarBadgeNotificaciones() {
        if (notificacionesNoLeidasCount > 0) {
            // 🔥 Mostrar badge circular con contador y animación
            if (badgeNotificaciones.getVisibility() != View.VISIBLE) {
                badgeNotificaciones.setAlpha(0f);
                badgeNotificaciones.setVisibility(View.VISIBLE);
                badgeNotificaciones.animate()
                    .alpha(1f)
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        badgeNotificaciones.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100);
                    });
            }

            if (notificacionesNoLeidasCount > 99) {
                badgeNotificaciones.setText("99+");
            } else {
                badgeNotificaciones.setText(String.valueOf(notificacionesNoLeidasCount));
            }

            // Mantener el texto badge original oculto
            tvBadgeNotificaciones.setVisibility(View.GONE);
        } else {
            // 🔥 Ocultar badge circular con animación suave
            if (badgeNotificaciones.getVisibility() == View.VISIBLE) {
                badgeNotificaciones.animate()
                    .alpha(0f)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        badgeNotificaciones.setVisibility(View.GONE);
                    });
            }

            if (notificacionesPermitidas) {
                tvBadgeNotificaciones.setVisibility(View.GONE);
            } else {
                tvBadgeNotificaciones.setVisibility(View.VISIBLE);
                tvBadgeNotificaciones.setText("⚠️ Deshabilitadas");
                tvBadgeNotificaciones.setTextColor(getColor(android.R.color.holo_orange_dark));
            }
        }
    }

    /**
     * ✅ NUEVO: Carga estadísticas usando el endpoint optimizado del backend
     * GET /movil/estudiantes/:id/estadisticas
     */
    private void cargarEstadisticasTareas(Hijo hijo) {
        // Verificar token - intentar obtenerlo dinámicamente si es null
        if (authToken == null) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            authToken = prefs.getString("AUTH_TOKEN", null);
        }

        if (authToken == null) {
            Log.w("MainActivity", "⚠️ No hay token de autorización para cargar estadísticas de " + hijo.getNombreCompleto());
            // Establecer valores por defecto
            hijo.setTareasPendientes(0);
            hijo.setTareasVencidas(0);
            hijo.setTareasCompletadas(0);
            actualizarEstadoHijo(hijo);
            return;
        }

        Log.d("MainActivity", "🔄 Cargando estadísticas para: " + hijo.getNombreCompleto());
        Log.d("MainActivity", "📡 Llamando API: /movil/estudiantes/" + hijo.getId() + "/tareas (mismo que TareasActivity)");

        // ✅ NUEVO: Usar el endpoint de tareas que incluye meta con contadores correctos
        apiService.getTareas(hijo.getId(), null, null).enqueue(new Callback<ApiResponse<List<TareaLista>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TareaLista>>> call, Response<ApiResponse<List<TareaLista>>> response) {
                Log.d("MainActivity", "📡 Response tareas - Código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TareaLista>> apiResponse = response.body();
                    Log.d("MainActivity", "📡 Response success: " + apiResponse.isSuccess());

                    if (apiResponse.isSuccess()) {
                        // ✅ Leer contadores desde el meta del backend
                        try {
                            Object meta = apiResponse.getMeta();
                            if (meta instanceof java.util.Map) {
                                @SuppressWarnings("unchecked")
                                java.util.Map<String, Object> metaMap = (java.util.Map<String, Object>) meta;

                                // Leer contadores directamente del backend
                                int pendientes = getIntFromMap(metaMap, "pendientes");
                                int vencidas = getIntFromMap(metaMap, "vencidas");
                                int entregadas = getIntFromMap(metaMap, "entregadas");
                                int calificadas = getIntFromMap(metaMap, "calificadas");
                                int total = getIntFromMap(metaMap, "total");

                                // Calcular por atender (pendientes + vencidas)
                                int porAtender = pendientes + vencidas;

                                // Actualizar estadísticas del hijo
                                hijo.setTareasPendientes(pendientes);
                                hijo.setTareasVencidas(vencidas);
                                hijo.setTareasCompletadas(entregadas + calificadas);
                                hijo.setPorAtender(porAtender);

                                // Logs detallados
                                Log.d("MainActivity", "📊 " + hijo.getNombreCompleto() + " - Contadores desde meta:");
                                Log.d("MainActivity", "   📋 Pendientes: " + pendientes);
                                Log.d("MainActivity", "   ⚠️ Vencidas: " + vencidas);
                                Log.d("MainActivity", "   📤 Entregadas: " + entregadas);
                                Log.d("MainActivity", "   ⭐ Calificadas: " + calificadas);
                                Log.d("MainActivity", "   🎯 Por atender: " + porAtender);
                                Log.d("MainActivity", "   📊 Total: " + total);

                            } else {
                                Log.w("MainActivity", "⚠️ Meta no es un Map para " + hijo.getNombreCompleto());
                                // Fallback: contar manualmente
                                contarTareasManualmente(hijo, apiResponse.getData());
                            }
                        } catch (Exception e) {
                            Log.e("MainActivity", "❌ Error procesando meta para " + hijo.getNombreCompleto(), e);
                            // Fallback: contar manualmente
                            contarTareasManualmente(hijo, apiResponse.getData());
                        }

                    } else {
                        Log.w("MainActivity", "⚠️ Response no exitosa para " + hijo.getNombreCompleto());
                        hijo.setTareasPendientes(0);
                        hijo.setTareasVencidas(0);
                        hijo.setTareasCompletadas(0);
                        hijo.setPorAtender(0);
                    }
                } else {
                    Log.w("MainActivity", "⚠️ Error al cargar tareas para " + hijo.getNombreCompleto() + " - Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.w("MainActivity", "⚠️ Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.w("MainActivity", "⚠️ No se pudo leer error body", e);
                        }
                    }
                    // Establecer valores por defecto en caso de error
                    hijo.setTareasPendientes(0);
                    hijo.setTareasVencidas(0);
                    hijo.setTareasCompletadas(0);
                    hijo.setPorAtender(0);
                }

                // Actualizar estado y UI
                actualizarEstadoHijo(hijo);
                runOnUiThread(() -> {
                    hijosAdapter.notifyDataSetChanged();
                    actualizarBadgesYAlertas();
                    Log.d("MainActivity", "🔄 UI actualizada para " + hijo.getNombreCompleto());
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TareaLista>>> call, Throwable t) {
                Log.e("MainActivity", "❌ Fallo al cargar tareas para " + hijo.getNombreCompleto(), t);
                // Establecer valores por defecto en caso de fallo
                hijo.setTareasPendientes(0);
                hijo.setTareasVencidas(0);
                hijo.setTareasCompletadas(0);
                hijo.setPorAtender(0);
                actualizarEstadoHijo(hijo);
                runOnUiThread(() -> {
                    hijosAdapter.notifyDataSetChanged();
                    actualizarBadgesYAlertas();
                });
            }
        });
    }

    /**
     * Carga las tareas vencidas para un estudiante específico
     */
    private void cargarTareasVencidas(Hijo hijo) {
        apiService.getTareas(hijo.getId(), "vencida", null).enqueue(new Callback<ApiResponse<List<TareaLista>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TareaLista>>> call, Response<ApiResponse<List<TareaLista>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int vencidas = response.body().getData().size();
                    hijo.setTareasVencidas(vencidas);
                    Log.d("MainActivity", "⚠️ " + hijo.getNombreCompleto() + " - Vencidas: " + vencidas);
                } else {
                    Log.w("MainActivity", "⚠️ Error al cargar tareas vencidas para " + hijo.getNombreCompleto() + " - Code: " + response.code());
                    hijo.setTareasVencidas(0);
                }

                // Después de cargar vencidas, cargar completadas para tener estadísticas completas
                cargarTareasCompletadas(hijo);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TareaLista>>> call, Throwable t) {
                Log.e("MainActivity", "❌ Fallo al cargar tareas vencidas para " + hijo.getNombreCompleto(), t);
                hijo.setTareasVencidas(0);
                cargarTareasCompletadas(hijo);
            }
        });
    }

    /**
     * Carga las tareas completadas para un estudiante específico
     */
    private void cargarTareasCompletadas(Hijo hijo) {
        apiService.getTareas(hijo.getId(), "completada", null).enqueue(new Callback<ApiResponse<List<TareaLista>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TareaLista>>> call, Response<ApiResponse<List<TareaLista>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int completadas = response.body().getData().size();
                    hijo.setTareasCompletadas(completadas);
                    Log.d("MainActivity", "✅ " + hijo.getNombreCompleto() + " - Completadas: " + completadas);
                } else {
                    Log.w("MainActivity", "⚠️ Error al cargar tareas completadas para " + hijo.getNombreCompleto() + " - Code: " + response.code());
                    hijo.setTareasCompletadas(0);
                }

                // Actualizar estado del hijo basado en las estadísticas
                actualizarEstadoHijo(hijo);

                // Actualizar UI después de cargar todas las estadísticas
                runOnUiThread(() -> {
                    hijosAdapter.notifyDataSetChanged();
                    actualizarBadgesYAlertas();
                    Log.d("MainActivity", "🔄 UI actualizada para " + hijo.getNombreCompleto());
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TareaLista>>> call, Throwable t) {
                Log.e("MainActivity", "❌ Fallo al cargar tareas completadas para " + hijo.getNombreCompleto(), t);
                hijo.setTareasCompletadas(0);
                actualizarEstadoHijo(hijo);
                runOnUiThread(() -> {
                    hijosAdapter.notifyDataSetChanged();
                    actualizarBadgesYAlertas();
                });
            }
        });
    }

    /**
     * Actualiza el estado del hijo basado en sus estadísticas de tareas
     */
    private void actualizarEstadoHijo(Hijo hijo) {
        String estadoAnterior = hijo.getEstado();

        if (hijo.getTareasVencidas() > 0) {
            hijo.setEstado("con_vencidas");
        } else if (hijo.getTareasPendientes() > 5) {
            hijo.setEstado("con_pendientes_urgentes");
        } else if (hijo.getTareasPendientes() > 0) {
            hijo.setEstado("con_pendientes");
        } else {
            hijo.setEstado("al_dia");
        }

        // Log del resumen de estadísticas
        Log.d("MainActivity", "📊 " + hijo.getNombreCompleto() + " - Resumen:");
        Log.d("MainActivity", "   📋 Pendientes: " + hijo.getTareasPendientes());
        Log.d("MainActivity", "   ⚠️ Vencidas: " + hijo.getTareasVencidas());
        Log.d("MainActivity", "   ✅ Completadas: " + hijo.getTareasCompletadas());
        Log.d("MainActivity", "   🎯 Estado: " + estadoAnterior + " → " + hijo.getEstado());
    }

    /**
     * ✅ NUEVO: Helper para leer enteros del meta del backend
     */
    private int getIntFromMap(java.util.Map<String, Object> map, String key) {
        try {
            Object value = map.get(key);
            if (value == null) return 0;
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            Log.w("MainActivity", "Error leyendo " + key + " del meta", e);
            return 0;
        }
    }

    /**
     * ✅ NUEVO: Fallback para contar tareas manualmente si falla el meta
     */
    private void contarTareasManualmente(Hijo hijo, List<TareaLista> tareas) {
        if (tareas == null) {
            Log.w("MainActivity", "⚠️ Lista de tareas null para " + hijo.getNombreCompleto());
            hijo.setTareasPendientes(0);
            hijo.setTareasVencidas(0);
            hijo.setTareasCompletadas(0);
            hijo.setPorAtender(0);
            return;
        }

        int pendientes = 0;
        int vencidas = 0;
        int completadas = 0;

        for (TareaLista tarea : tareas) {
            String estado = tarea.getEstado();
            if ("pendiente".equals(estado)) {
                pendientes++;
            } else if ("vencida".equals(estado)) {
                vencidas++;
            } else if ("entregada".equals(estado) || "calificada".equals(estado)) {
                completadas++;
            }
        }

        hijo.setTareasPendientes(pendientes);
        hijo.setTareasVencidas(vencidas);
        hijo.setTareasCompletadas(completadas);
        hijo.setPorAtender(pendientes + vencidas);

        Log.d("MainActivity", "📊 " + hijo.getNombreCompleto() + " - Conteo manual:");
        Log.d("MainActivity", "   📋 Pendientes: " + pendientes);
        Log.d("MainActivity", "   ⚠️ Vencidas: " + vencidas);
        Log.d("MainActivity", "   ✅ Completadas: " + completadas);
        Log.d("MainActivity", "   🎯 Por atender: " + (pendientes + vencidas));
    }
}
