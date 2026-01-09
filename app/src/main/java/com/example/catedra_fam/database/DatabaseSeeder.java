package com.example.catedra_fam.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.catedra_fam.database.entities.AsignacionEntity;
import com.example.catedra_fam.database.entities.CalificacionEntity;
import com.example.catedra_fam.database.entities.CursoEntity;
import com.example.catedra_fam.database.entities.EstudianteEntity;
import com.example.catedra_fam.database.entities.NotificacionEntity;
import com.example.catedra_fam.database.entities.PeriodoEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Clase helper para insertar datos de prueba en la base de datos local
 */
public class DatabaseSeeder {

    private static final String TAG = "DatabaseSeeder";
    private AppDatabase db;
    private Context context;

    public DatabaseSeeder(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    /**
     * Inserta todos los datos de prueba en la base de datos
     */
    public void seedDatabase(OnSeedCompleteListener listener) {
        new SeedDatabaseTask(listener).execute();
    }

    /**
     * Interfaz para callback cuando termina el seed
     */
    public interface OnSeedCompleteListener {
        void onComplete(boolean success);
    }

    /**
     * AsyncTask para insertar datos en background
     */
    private class SeedDatabaseTask extends AsyncTask<Void, Void, Boolean> {

        private OnSeedCompleteListener listener;

        SeedDatabaseTask(OnSeedCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Log.d(TAG, "Iniciando seed de base de datos...");

                // 1. Insertar Periodo Activo
                insertarPeriodos();

                // 2. Insertar Cursos
                insertarCursos();

                // 3. Insertar Estudiantes (Hijos)
                insertarEstudiantes();

                // 4. Insertar Asignaciones (Tareas)
                insertarAsignaciones();

                // 5. Insertar Calificaciones
                insertarCalificaciones();

                // 6. Insertar Notificaciones
                insertarNotificaciones();

                Log.d(TAG, "Seed completado exitosamente");
                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error en seed de base de datos", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (listener != null) {
                listener.onComplete(success);
            }
        }
    }

    private void insertarPeriodos() {
        PeriodoEntity periodo = new PeriodoEntity();
        periodo.setId(1);
        periodo.setNombre("Periodo 1 - 2026");
        periodo.setFechaInicio("2026-01-20");
        periodo.setFechaFin("2026-05-30");
        periodo.setEstaActivo(true);

        db.periodoDao().insert(periodo);
        Log.d(TAG, "Periodo insertado");
    }

    private void insertarCursos() {
        List<CursoEntity> cursos = new ArrayList<>();

        CursoEntity curso1 = new CursoEntity();
        curso1.setId(1);
        curso1.setNombre("5° A");
        curso1.setGradoId(5);
        curso1.setGradoNombre("Quinto");
        curso1.setJornada("manana");
        cursos.add(curso1);

        CursoEntity curso2 = new CursoEntity();
        curso2.setId(2);
        curso2.setNombre("3° B");
        curso2.setGradoId(3);
        curso2.setGradoNombre("Tercero");
        curso2.setJornada("manana");
        cursos.add(curso2);

        // No tenemos CursoDao, pero las foreign keys lo requerirán
        // Por ahora, los datos están en memoria en las entities
        Log.d(TAG, "Cursos preparados");
    }

    private void insertarEstudiantes() {
        List<EstudianteEntity> estudiantes = new ArrayList<>();

        // Hijo 1: Juan Pérez
        EstudianteEntity juan = new EstudianteEntity();
        juan.setId(1);
        juan.setNombres("Juan");
        juan.setApellidos("Pérez");
        juan.setTipoDocumento("TI");
        juan.setNumeroDocumento("1006789012");
        juan.setFechaNacimiento("2015-03-15");
        juan.setSexo("M");
        juan.setCursoId(1);
        juan.setCursoNombre("5° A");
        juan.setAcudienteId(1);
        estudiantes.add(juan);

        // Hijo 2: Ana Pérez
        EstudianteEntity ana = new EstudianteEntity();
        ana.setId(2);
        ana.setNombres("Ana");
        ana.setApellidos("Pérez");
        ana.setTipoDocumento("TI");
        ana.setNumeroDocumento("1006789013");
        ana.setFechaNacimiento("2017-07-22");
        ana.setSexo("F");
        ana.setCursoId(2);
        ana.setCursoNombre("3° B");
        ana.setAcudienteId(1);
        estudiantes.add(ana);

        db.estudianteDao().insertAll(estudiantes);
        Log.d(TAG, estudiantes.size() + " estudiantes insertados");
    }

    private void insertarAsignaciones() {
        List<AsignacionEntity> asignaciones = new ArrayList<>();

        // Tareas para Juan (5° A)
        AsignacionEntity tarea1 = new AsignacionEntity();
        tarea1.setId(1);
        tarea1.setTitulo("Lectura en familia: El Principito");
        tarea1.setDescripcion("Lean juntos durante 30 minutos y compartan qué les pareció más interesante.");
        tarea1.setFrecuencia("semanal");
        tarea1.setFechaInicio("2026-01-08");
        tarea1.setFechaVencimiento("2026-01-15");
        tarea1.setIncluirEnBoletin(true);
        tarea1.setCursoId(1);
        tarea1.setPeriodoId(1);
        tarea1.setTema("Comprensión lectora");
        tarea1.setEstado("pendiente");
        asignaciones.add(tarea1);

        AsignacionEntity tarea2 = new AsignacionEntity();
        tarea2.setId(2);
        tarea2.setTitulo("Juego de mesa familiar");
        tarea2.setDescripcion("Jueguen un juego de mesa en familia y tomen fotos del momento.");
        tarea2.setFrecuencia("quincenal");
        tarea2.setFechaInicio("2026-01-08");
        tarea2.setFechaVencimiento("2026-01-22");
        tarea2.setIncluirEnBoletin(true);
        tarea2.setCursoId(1);
        tarea2.setPeriodoId(1);
        tarea2.setTema("Convivencia familiar");
        tarea2.setEstado("pendiente");
        asignaciones.add(tarea2);

        AsignacionEntity tarea3 = new AsignacionEntity();
        tarea3.setId(3);
        tarea3.setTitulo("Conversación sobre valores");
        tarea3.setDescripcion("Conversen en familia sobre la importancia del respeto y la responsabilidad.");
        tarea3.setFrecuencia("semanal");
        tarea3.setFechaInicio("2025-12-20");
        tarea3.setFechaVencimiento("2026-01-05");
        tarea3.setIncluirEnBoletin(true);
        tarea3.setCursoId(1);
        tarea3.setPeriodoId(1);
        tarea3.setTema("Valores");
        tarea3.setEstado("calificada"); // Ya fue calificada
        asignaciones.add(tarea3);

        // Tareas para Ana (3° B)
        AsignacionEntity tarea4 = new AsignacionEntity();
        tarea4.setId(4);
        tarea4.setTitulo("Cuento antes de dormir");
        tarea4.setDescripcion("Lean un cuento juntos antes de dormir durante una semana.");
        tarea4.setFrecuencia("semanal");
        tarea4.setFechaInicio("2026-01-01");
        tarea4.setFechaVencimiento("2026-01-07");
        tarea4.setIncluirEnBoletin(true);
        tarea4.setCursoId(2);
        tarea4.setPeriodoId(1);
        tarea4.setTema("Lectura");
        tarea4.setEstado("calificada");
        asignaciones.add(tarea4);

        AsignacionEntity tarea5 = new AsignacionEntity();
        tarea5.setId(5);
        tarea5.setTitulo("Preparar receta familiar");
        tarea5.setDescripcion("Preparen juntos una receta de cocina simple y compartan fotos.");
        tarea5.setFrecuencia("quincenal");
        tarea5.setFechaInicio("2026-01-05");
        tarea5.setFechaVencimiento("2026-01-19");
        tarea5.setIncluirEnBoletin(true);
        tarea5.setCursoId(2);
        tarea5.setPeriodoId(1);
        tarea5.setTema("Convivencia");
        tarea5.setEstado("calificada");
        asignaciones.add(tarea5);

        db.asignacionDao().insertAll(asignaciones);
        Log.d(TAG, asignaciones.size() + " asignaciones insertadas");
    }

    private void insertarCalificaciones() {
        List<CalificacionEntity> calificaciones = new ArrayList<>();

        // Calificación para tarea 3 de Juan
        CalificacionEntity cal1 = new CalificacionEntity();
        cal1.setId(1);
        cal1.setEntregaId(1); // Asumimos que hay una entrega con ID 1
        cal1.setEstudianteId(1);
        cal1.setAsignacionId(3);
        cal1.setNota(4.5);
        cal1.setEscala("alto");
        cal1.setNotaCualitativa("Alto");
        cal1.setRetroalimentacion("Excelente reflexión familiar sobre valores. Se evidencia participación activa.");
        cal1.setCalificadoEn("2026-01-06 10:30:00");
        cal1.setPeriodoId(1);
        calificaciones.add(cal1);

        // Calificaciones para Ana (tareas 4 y 5)
        CalificacionEntity cal2 = new CalificacionEntity();
        cal2.setId(2);
        cal2.setEntregaId(2);
        cal2.setEstudianteId(2);
        cal2.setAsignacionId(4);
        cal2.setNota(4.8);
        cal2.setEscala("superior");
        cal2.setNotaCualitativa("Superior");
        cal2.setRetroalimentacion("Hermosa evidencia de lectura en familia. Felicitaciones!");
        cal2.setCalificadoEn("2026-01-08 14:20:00");
        cal2.setPeriodoId(1);
        calificaciones.add(cal2);

        CalificacionEntity cal3 = new CalificacionEntity();
        cal3.setId(3);
        cal3.setEntregaId(3);
        cal3.setEstudianteId(2);
        cal3.setAsignacionId(5);
        cal3.setNota(4.7);
        cal3.setEscala("alto");
        cal3.setNotaCualitativa("Alto");
        cal3.setRetroalimentacion("Qué deliciosa receta prepararon juntos. Se nota el trabajo en equipo.");
        cal3.setCalificadoEn("2026-01-07 16:15:00");
        cal3.setPeriodoId(1);
        calificaciones.add(cal3);

        db.calificacionDao().insertAll(calificaciones);
        Log.d(TAG, calificaciones.size() + " calificaciones insertadas");
    }

    private void insertarNotificaciones() {
        List<NotificacionEntity> notificaciones = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String ahora = sdf.format(new Date());

        // Notificación 1: Nueva tarea asignada
        NotificacionEntity notif1 = new NotificacionEntity();
        notif1.setId(1);
        notif1.setTipo("tarea_asignada");
        notif1.setAsunto("Nueva tarea asignada");
        notif1.setMensaje("Se ha asignado una nueva tarea: 'Lectura en familia: El Principito'");
        notif1.setEstado("enviada");
        notif1.setLeido(false);
        notif1.setAsignacionId(1);
        notif1.setCreadoEn(ahora);
        notificaciones.add(notif1);

        // Notificación 2: Recordatorio de vencimiento
        NotificacionEntity notif2 = new NotificacionEntity();
        notif2.setId(2);
        notif2.setTipo("recordatorio");
        notif2.setAsunto("Recordatorio: Tarea próxima a vencer");
        notif2.setMensaje("La tarea 'Lectura en familia' vence el 15 de enero. ¡No olvides entregarla!");
        notif2.setEstado("enviada");
        notif2.setLeido(false);
        notif2.setAsignacionId(1);
        notif2.setCreadoEn(ahora);
        notificaciones.add(notif2);

        // Notificación 3: Tarea calificada
        NotificacionEntity notif3 = new NotificacionEntity();
        notif3.setId(3);
        notif3.setTipo("tarea_calificada");
        notif3.setAsunto("Tarea calificada");
        notif3.setMensaje("Tu hijo Juan ha recibido calificación en 'Conversación sobre valores': Alto (4.5)");
        notif3.setEstado("enviada");
        notif3.setLeido(true);
        notif3.setLeidoEn("2026-01-07 08:00:00");
        notif3.setAsignacionId(3);
        notif3.setCreadoEn("2026-01-06 10:35:00");
        notificaciones.add(notif3);

        db.notificacionDao().insertAll(notificaciones);
        Log.d(TAG, notificaciones.size() + " notificaciones insertadas");
    }
}

