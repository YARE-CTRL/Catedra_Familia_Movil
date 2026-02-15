package com.example.catedra_fam.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.catedra_fam.MainActivity;
import com.example.catedra_fam.R;
import com.example.catedra_fam.TareaDetalleActivity;
import com.example.catedra_fam.services.FCMNotificationService;

/**
 * Utilidad para probar notificaciones FCM localmente sin depender del backend
 * Actualizada para la nueva implementación FCM según especificaciones del backend
 */
public class NotificationTestHelper {
    private static final String TAG = "NotificationTestHelper";

    /**
     * Simula una notificación de nueva tarea según especificación backend
     */
    public static void testNuevaTareaNotification(Context context) {
        Log.d(TAG, "🧪 Probando notificación de nueva tarea");

        showTestNotification(
            context,
            "📚 Nueva Tarea Asignada",
            "El orientador asignó: Matemáticas - Ejercicio 5",
            createTareaIntent(context, "123"),
            1001
        );

        // Incrementar contador local para testing
        incrementTestCounter(context);
    }

    /**
     * Simula una notificación de tarea entregada
     */
    public static void testTareaEntregadaNotification(Context context) {
        Log.d(TAG, "🧪 Probando notificación de tarea entregada");

        showTestNotification(
            context,
            "✅ Tarea Entregada",
            "Tu tarea de Ciencias fue entregada correctamente",
            createMainActivityIntent(context, "OPEN_TAREAS"),
            1002
        );

        incrementTestCounter(context);
    }

    /**
     * Simula una notificación de tarea vencida
     */
    public static void testTareaVencidaNotification(Context context) {
        Log.d(TAG, "🧪 Probando notificación de tarea vencida");

        showTestNotification(
            context,
            "⏰ Tarea Próxima a Vencer",
            "La tarea de Historia vence en 1 día",
            createMainActivityIntent(context, "OPEN_TAREAS"),
            1003
        );

        incrementTestCounter(context);
    }

    /**
     * Simula una notificación de calificación
     */
    public static void testCalificacionNotification(Context context) {
        Log.d(TAG, "🧪 Probando notificación de calificación");

        showTestNotification(
            context,
            "🏆 Nueva Calificación",
            "Has recibido calificación en Matemáticas: 4.5/5.0",
            createMainActivityIntent(context, "OPEN_HISTORIAL"),
            1004
        );

        incrementTestCounter(context);
    }

    /**
     * Simula una notificación genérica
     */
    public static void testGeneralNotification(Context context) {
        Log.d(TAG, "🧪 Probando notificación general");

        showTestNotification(
            context,
            "📱 Recordatorio Escolar",
            "No olvides la reunión de padres el viernes",
            createMainActivityIntent(context, null),
            1005
        );

        incrementTestCounter(context);
    }

    /**
     * Muestra todas las notificaciones de prueba disponibles
     */
    public static void showAllTestNotifications(Context context) {
        Log.d(TAG, "🧪 INICIANDO TODAS LAS PRUEBAS DE NOTIFICACIÓN");

        android.os.Handler handler = new android.os.Handler(context.getMainLooper());

        handler.postDelayed(() -> testNuevaTareaNotification(context), 1000);
        handler.postDelayed(() -> testTareaEntregadaNotification(context), 3000);
        handler.postDelayed(() -> testTareaVencidaNotification(context), 5000);
        handler.postDelayed(() -> testCalificacionNotification(context), 7000);
        handler.postDelayed(() -> testGeneralNotification(context), 9000);

        Log.d(TAG, "🧪 Todas las pruebas programadas - revisa las notificaciones");
    }

    /**
     * Método base para mostrar notificaciones de testing
     */
    private static void showTestNotification(Context context, String title, String body,
                                           Intent intent, int notificationId) {

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
            context,
            FCMNotificationService.CATEDRA_FAMILIA_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 250, 250, 250})
            .setShowWhen(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        try {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "✅ Notificación de prueba enviada: " + title);
        } catch (SecurityException e) {
            Log.w(TAG, "⚠️ Sin permisos de notificación: " + e.getMessage());
        }
    }

    /**
     * Crea intent para navegación a detalle de tarea
     */
    private static Intent createTareaIntent(Context context, String tareaId) {
        Intent intent = new Intent(context, TareaDetalleActivity.class);
        intent.putExtra("TAREA_ID", tareaId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * Crea intent para navegación a MainActivity con extras específicos
     */
    private static Intent createMainActivityIntent(Context context, String extra) {
        Intent intent = new Intent(context, MainActivity.class);
        if (extra != null) {
            intent.putExtra(extra, true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * Incrementa contador de notificaciones para testing
     */
    private static void incrementTestCounter(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences(
            "notification_counter",
            Context.MODE_PRIVATE
        );
        int current = prefs.getInt("unread_count", 0);
        prefs.edit()
            .putInt("unread_count", current + 1)
            .putLong("last_sync", System.currentTimeMillis())
            .apply();

        Log.d(TAG, "📊 Contador testing incrementado: " + (current + 1));
    }

    /**
     * Resetea contador de notificaciones para testing
     */
    public static void resetTestCounter(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences(
            "notification_counter",
            Context.MODE_PRIVATE
        );
        prefs.edit()
            .putInt("unread_count", 0)
            .putLong("last_sync", System.currentTimeMillis())
            .apply();

        Log.d(TAG, "🔄 Contador testing reseteado");
    }
}




