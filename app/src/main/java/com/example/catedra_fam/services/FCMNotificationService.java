package com.example.catedra_fam.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.catedra_fam.MainActivity;
import com.example.catedra_fam.NotificacionesActivity;
import com.example.catedra_fam.R;
import com.example.catedra_fam.TareaDetalleActivity;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.DeviceInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Servicio FCM para recibir notificaciones push
 * Implementación 100% alineada con especificaciones del backend
 */
public class FCMNotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCMNotificationService";

    // Canales de notificación según especificación backend
    public static final String CATEDRA_FAMILIA_CHANNEL = "catedra_familia_channel";
    public static final String CHANNEL_NAME = "Cátedra Familia";
    public static final String CHANNEL_DESCRIPTION = "Notificaciones de tareas y eventos";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo FCM token: " + token);

        // 📱 MOSTRAR NUEVO TOKEN PARA DESARROLLO
        Log.d("TOKEN_FCM_COPIAR", "====================================");
        Log.d("TOKEN_FCM_COPIAR", "NUEVO TOKEN FCM PARA COPIAR:");
        Log.d("TOKEN_FCM_COPIAR", token);
        Log.d("TOKEN_FCM_COPIAR", "====================================");

        // Guardar token localmente
        saveTokenLocally(token);

        // Registrar token en backend si hay sesión activa
        registerTokenWithBackend(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "📩 Mensaje FCM recibido de: " + remoteMessage.getFrom());

        // Incrementar contador de notificaciones no leídas
        incrementNotificationCounter();

        // Extraer título y cuerpo de la notificación
        String title = "Nueva notificación";
        String body = "";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // ✅ ACTUALIZADO: Extraer datos según especificación backend (16-Feb-2026)
        // Backend envía: data: { tipo, titulo, cuerpo, datos: "{\"tipo\":\"nueva_tarea\",\"id\":42}" }
        String tipo = remoteMessage.getData().get("tipo");
        String datosJson = remoteMessage.getData().get("datos");

        // Parsear JSON de datos para obtener id
        String targetId = null;
        String estudianteId = null;

        if (datosJson != null) {
            try {
                org.json.JSONObject datosObj = new org.json.JSONObject(datosJson);
                if (datosObj.has("id")) {
                    targetId = datosObj.getString("id");
                }
                if (datosObj.has("asignacionId")) {
                    targetId = datosObj.getString("asignacionId");
                }
                if (datosObj.has("estudianteId")) {
                    estudianteId = datosObj.getString("estudianteId");
                }
                Log.d(TAG, "📊 Datos parseados - tipo: " + datosObj.optString("tipo") + ", id: " + targetId);
            } catch (org.json.JSONException e) {
                Log.e(TAG, "❌ Error parseando datos JSON: " + e.getMessage());
            }
        }

        // Fallback: intentar obtener de campos directos (compatibilidad)
        if (targetId == null) {
            targetId = remoteMessage.getData().get("target_id");
        }
        if (estudianteId == null) {
            estudianteId = remoteMessage.getData().get("estudiante_id");
        }

        Log.d(TAG, "📋 Procesando - Tipo: " + tipo + ", Target ID: " + targetId + ", Estudiante ID: " + estudianteId);

        // Mostrar notificación según tipo
        showNotificationByType(title, body, tipo, targetId, estudianteId);
    }

    /**
     * Maneja diferentes tipos de notificación según especificación backend
     */
    private void showNotificationByType(String title, String body, String tipo, String targetId, String estudianteId) {
        Intent intent = createIntentForNotificationType(tipo, targetId, estudianteId);
        int notificationId = (int) System.currentTimeMillis();

        // Personalizar según tipo
        switch (tipo != null ? tipo : "general") {
            case "tarea":
                showNotification(
                    title != null ? title : "📚 Nueva tarea asignada",
                    body != null ? body : "Tienes una nueva tarea pendiente",
                    notificationId,
                    intent
                );
                break;

            case "tarea_entregada":
                showNotification(
                    title != null ? title : "✅ Tarea entregada",
                    body != null ? body : "Tu tarea ha sido recibida correctamente",
                    notificationId,
                    intent
                );
                break;

            case "tarea_vencida":
                showNotification(
                    title != null ? title : "⏰ Tarea próxima a vencer",
                    body != null ? body : "Tienes una tarea que vence pronto",
                    notificationId,
                    intent
                );
                break;

            case "calificacion":
                showNotification(
                    title != null ? title : "🏆 Nueva calificación",
                    body != null ? body : "Has recibido una nueva calificación",
                    notificationId,
                    intent
                );
                break;

            case "evento":
                showNotification(
                    title != null ? title : "📅 Nuevo evento",
                    body != null ? body : "Hay un nuevo evento escolar",
                    notificationId,
                    intent
                );
                break;

            case "recordatorio":
                showNotification(
                    title != null ? title : "🔔 Recordatorio",
                    body != null ? body : "Tienes un recordatorio importante",
                    notificationId,
                    intent
                );
                break;

            default:
                showNotification(
                    title != null ? title : "📱 Nueva notificación",
                    body != null ? body : "Tienes una nueva actualización",
                    notificationId,
                    intent
                );
                break;
        }
    }

    /**
     * Crea intent de navegación según el tipo de notificación
     */
    private Intent createIntentForNotificationType(String tipo, String targetId, String estudianteId) {
        Intent intent;

        switch (tipo != null ? tipo : "general") {
            case "tarea":
                if (targetId != null) {
                    intent = new Intent(this, TareaDetalleActivity.class);
                    intent.putExtra("TAREA_ID", targetId);
                    if (estudianteId != null) {
                        intent.putExtra("ESTUDIANTE_ID", estudianteId);
                    }
                } else {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("OPEN_TAREAS", true);
                }
                break;

            case "tarea_entregada":
            case "tarea_vencida":
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("OPEN_TAREAS", true);
                if (estudianteId != null) {
                    intent.putExtra("ESTUDIANTE_ID", estudianteId);
                }
                break;

            case "calificacion":
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("OPEN_HISTORIAL", true);
                if (estudianteId != null) {
                    intent.putExtra("ESTUDIANTE_ID", estudianteId);
                }
                break;

            case "evento":
            case "recordatorio":
                intent = new Intent(this, NotificacionesActivity.class);
                break;

            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * Muestra notificación usando el canal único
     */
    private void showNotification(String title, String body, int notificationId, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CATEDRA_FAMILIA_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 250, 250, 250})
            .setShowWhen(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Verificar permisos para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (notificationManager.areNotificationsEnabled()) {
                try {
                    notificationManager.notify(notificationId, builder.build());
                    Log.d(TAG, "✅ Notificación mostrada: " + title);
                } catch (SecurityException e) {
                    Log.e(TAG, "❌ Error de permisos al mostrar notificación: " + e.getMessage());
                }
            } else {
                Log.w(TAG, "⚠️ Notificaciones no habilitadas por el usuario");
            }
        } else {
            // Android < 13: notificaciones habilitadas por defecto
            try {
                notificationManager.notify(notificationId, builder.build());
                Log.d(TAG, "✅ Notificación mostrada: " + title);
            } catch (SecurityException e) {
                Log.e(TAG, "❌ Error al mostrar notificación: " + e.getMessage());
            }
        }
    }

    /**
     * Crea canal de notificación único según especificación backend
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CATEDRA_FAMILIA_CHANNEL,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 250, 250, 250});
            channel.setShowBadge(true);
            channel.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Log.d(TAG, "Canal de notificación creado: " + CATEDRA_FAMILIA_CHANNEL);
        }
    }

    /**
     * Incrementa el contador de notificaciones no leídas
     */
    private void incrementNotificationCounter() {
        SharedPreferences prefs = getSharedPreferences("notification_counter", MODE_PRIVATE);
        int current = prefs.getInt("unread_count", 0);
        prefs.edit()
            .putInt("unread_count", current + 1)
            .putLong("last_sync", System.currentTimeMillis())
            .apply();

        Log.d(TAG, "Contador notificaciones incrementado: " + (current + 1));
    }

    /**
     * Guarda token FCM localmente
     */
    private void saveTokenLocally(String token) {
        SharedPreferences prefs = getSharedPreferences("fcm_prefs", MODE_PRIVATE);
        prefs.edit().putString("fcm_token", token).apply();
        Log.d(TAG, "Token FCM guardado localmente");
    }

    /**
     * Registra token en backend si hay sesión activa
     * ✅ ACTUALIZADO: Campo plataforma agregado (16 Feb 2026)
     * ✅ CORREGIDO: SharedPreferences ahora coincide con LoginActivity (16 Feb 2026)
     */
    private void registerTokenWithBackend(String token) {
        // ✅ CORREGIDO: Usar mismo nombre que LoginActivity ("AppPrefs" y "AUTH_TOKEN")
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String authToken = prefs.getString("AUTH_TOKEN", null);

        Log.d(TAG, "🔍 Verificando sesión activa...");
        Log.d(TAG, "Auth token presente: " + (authToken != null ? "SÍ" : "NO"));

        if (authToken != null) {
            // ✅ Crear DeviceInfo con campo plataforma requerido por backend
            DeviceInfo deviceInfo = new DeviceInfo(
                token,
                android.os.Build.MODEL,       // dispositivo: "moto e40"
                "Android",                    // ✅ plataforma: "Android" (capitalizado)
                "android",                    // sistemaOperativo: "android" (legacy)
                "1.0.0"                      // versionApp
            );

            Log.d(TAG, "📡 Registrando token FCM en backend...");
            Log.d(TAG, "📦 Payload: " + deviceInfo.toString());

            RetrofitClient.getApiService(this).registerFCMToken("Bearer " + authToken, deviceInfo)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "✅ Token FCM registrado exitosamente en backend");
                            Log.d(TAG, "🎉 Las notificaciones push ahora funcionarán correctamente");
                        } else {
                            Log.e(TAG, "❌ Error registrando token FCM - Código: " + response.code());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "📄 Error body: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error leyendo error body: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "❌ Fallo en registro FCM: " + t.getMessage());
                        Log.e(TAG, "🔄 El token se intentará registrar en el próximo login");
                    }
                });
        } else {
            Log.w(TAG, "⚠️ No hay sesión activa (AUTH_TOKEN null)");
            Log.w(TAG, "💡 Token se registrará automáticamente después del próximo login");
        }
    }
}
