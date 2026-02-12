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
import com.example.catedra_fam.models.Hijo;
import com.example.catedra_fam.models.DeviceInfo;
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
    private ImageView ivEstadoConexion;

    private HijosAdapter hijosAdapter;
    private List<Hijo> listaHijos;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService(this);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupRecyclerView();
        setupListeners();
        
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
    private interface EstudiantesCallback extends Callback<ApiResponse<List<Estudiante>>> {}

private void cargarMisEstudiantes() {
        mostrarLoading(true);
        
        apiService.getMisEstudiantes().enqueue(new EstudiantesCallback() {
            @Override
            public void onResponse(Call<ApiResponse<List<Estudiante>>> call,
                                   Response<ApiResponse<List<Estudiante>>> response) {
                mostrarLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Estudiante>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        List<Estudiante> estudiantes = apiResponse.getData();
                        
                        // DEBUG: Verificar datos recibidos
                        if (estudiantes == null || estudiantes.isEmpty()) {
                            Toast.makeText(MainActivity.this, "No se encontraron estudiantes", Toast.LENGTH_SHORT).show();
                            cargarEstudiantesGuardados();
                            return;
                        }
                        
                        // Convertir Estudiante -> Hijo
                        listaHijos.clear();
                        for (Estudiante estudiante : estudiantes) {
                            Hijo hijo = convertirEstudianteAHijo(estudiante);
                            listaHijos.add(hijo);
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
            public void onFailure(Call<ApiResponse<List<Estudiante>>> call, Throwable t) {
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
        // TODO: Implementar indicador de carga si es necesario
        // Por ahora no hay ProgressBar en el layout principal
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
        for (Hijo h : listaHijos) {
            totalPendientes += h.getTareasPendientes();
        }

        tvBadgeTareas.setText(totalPendientes + " pendientes");

        // TODO: Obtener notificaciones no leídas de la API
        tvBadgeNotificaciones.setText("Nuevas");

        if (totalPendientes > 0) {
            cardAlerta.setVisibility(View.VISIBLE);
            tvAlerta.setText(totalPendientes + " tareas pendientes");
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
        DeviceInfo deviceInfo = new DeviceInfo(
            fcmToken,
            "android",
            "1.0", // Versión hardcoded temporalmente
            android.os.Build.MODEL,
            android.os.Build.VERSION.RELEASE
        );
        
        apiService.registerFCMToken("Bearer " + authToken, deviceInfo)
            .enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("MainActivity", "FCM Token registrado exitosamente");
                    } else {
                        Log.e("MainActivity", "Error registrando FCM token: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    Log.e("MainActivity", "Error de red registrando FCM token", t);
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
}

