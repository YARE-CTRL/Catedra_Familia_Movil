package com.example.catedra_fam;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catedra_fam.adapters.HijosAdapter;
import com.example.catedra_fam.models.Hijo;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupRecyclerView();
        setupListeners();
        loadMockData();
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
            getSupportActionBar().setTitle("Bienvenido, María");
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

        // Configurar header del navigation drawer
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombreHeader = headerView.findViewById(R.id.tv_nombre_header);
        TextView tvCorreoHeader = headerView.findViewById(R.id.tv_correo_header);

        if (tvNombreHeader != null) {
            tvNombreHeader.setText("María Pérez");
        }
        if (tvCorreoHeader != null) {
            tvCorreoHeader.setText("maria.perez@gmail.com");
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

    private void loadMockData() {
        // Datos de prueba para los hijos
        listaHijos.clear();

        Hijo hijo1 = new Hijo();
        hijo1.setId(1);
        hijo1.setNombres("Juan");
        hijo1.setApellidos("Pérez");
        hijo1.setCursoNombre("5° A");
        hijo1.setTareasPendientes(2);
        hijo1.setTareasCompletadas(3);
        hijo1.setTareasCalificadas(1);
        hijo1.setProximaTarea("Lectura familiar");
        hijo1.setFechaProximaTarea("15 Ene");
        hijo1.setEstado("con_pendientes");

        Hijo hijo2 = new Hijo();
        hijo2.setId(2);
        hijo2.setNombres("Ana");
        hijo2.setApellidos("Pérez");
        hijo2.setCursoNombre("3° B");
        hijo2.setTareasPendientes(0);
        hijo2.setTareasCompletadas(5);
        hijo2.setTareasCalificadas(5);
        hijo2.setProximaTarea(null);
        hijo2.setFechaProximaTarea(null);
        hijo2.setEstado("al_dia");

        listaHijos.add(hijo1);
        listaHijos.add(hijo2);

        hijosAdapter.notifyDataSetChanged();

        // Actualizar badges
        int totalPendientes = 0;
        for (Hijo h : listaHijos) {
            totalPendientes += h.getTareasPendientes();
        }

        tvBadgeTareas.setText(totalPendientes + " pendientes");
        tvBadgeNotificaciones.setText("2 nuevas");

        // Mostrar/ocultar alerta
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
}

