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

import com.example.catedra_fam.LoginActivity;
import com.example.catedra_fam.MainActivity;
import com.example.catedra_fam.R;
import com.example.catedra_fam.TareaDetalleActivity;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.DeviceInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMNotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCMNotificationService";
    
    // Canales de notificación
    public static final String TAREAS_CHANNEL = "tareas_channel";
    public static final String URGENT_CHANNEL = "urgent_channel";
    public static final String GENERAL_CHANNEL = "general_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM Token refreshed: " + token);
        
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
        
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());
        
        // Manejar notificación con datos
        if (remoteMessage.getData().size() > 0) {
            // Convertir Map<String,String> a Bundle
            android.os.Bundle data = new android.os.Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                data.putString(entry.getKey(), entry.getValue());
            }
            handleDataMessage(data);
        }
        
        // Manejar notificación con payload
        if (remoteMessage.getNotification() != null) {
            handleNotificationMessage(remoteMessage.getNotification());
        }
    }

    private void handleDataMessage(android.os.Bundle data) {
        String tipo = data.getString("tipo", "general");
        String titulo = data.getString("title", data.getString("titulo", "Notificación"));
        String cuerpo = data.getString("body", data.getString("cuerpo", "Tienes una nueva notificación"));
        
        Log.d(TAG, "Data message - Tipo: " + tipo + ", Título: " + titulo);
        
        switch (tipo) {
            case "tarea":
                handleTareaNotification(data);
                break;
            case "evento":
                handleEventoNotification(data);
                break;
            default:
                handleGeneralNotification(titulo, cuerpo);
                break;
        }
    }

    private void handleTareaNotification(android.os.Bundle data) {
        String subtipo = data.getString("subtipo", "general");
        String asignacionIdStr = data.getString("asignacion_id");
        Integer asignacionId = asignacionIdStr != null ? Integer.parseInt(asignacionIdStr) : null;
        
        switch (subtipo) {
            case "nueva_tarea":
                showTareaNotification(
                    "📚 Nueva Tarea",
                    data.getString("cuerpo", "Se asignó una nueva tarea"),
                    asignacionId
                );
                break;
            case "proxima_vencer":
                showUrgentNotification(
                    "⏰ Tarea por Vencer",
                    data.getString("cuerpo", "Una tarea está por vencer"),
                    asignacionId
                );
                break;
            case "calificada":
                showCalificacionNotification(data);
                break;
            case "vencida":
                showUrgentNotification(
                    "❌ Tarea Vencida",
                    data.getString("cuerpo", "Una tarea ha vencido"),
                    asignacionId
                );
                break;
            default:
                handleGeneralNotification("📋 Tarea", data.getString("cuerpo", "Actualización de tarea"));
                break;
        }
    }

    private void handleEventoNotification(android.os.Bundle data) {
        String titulo = data.getString("titulo", "📅 Evento");
        String cuerpo = data.getString("cuerpo", "Tienes un nuevo evento");
        
        showGeneralNotification(titulo, cuerpo, GENERAL_CHANNEL);
    }

    private void handleGeneralNotification(String titulo, String cuerpo) {
        showGeneralNotification(titulo, cuerpo, GENERAL_CHANNEL);
    }

    private void handleNotificationMessage(RemoteMessage.Notification notification) {
        String titulo = notification.getTitle();
        String cuerpo = notification.getBody();
        
        showGeneralNotification(titulo, cuerpo, GENERAL_CHANNEL);
    }

    private void showTareaNotification(String title, String body, Integer asignacionId) {
        Intent intent = new Intent(this, TareaDetalleActivity.class);
        if (asignacionId != null) {
            intent.putExtra("TAREA_ID", asignacionId);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        showNotification(title, body, TAREAS_CHANNEL, asignacionId != null ? asignacionId : 0, intent);
    }

    private void showUrgentNotification(String title, String body, Integer asignacionId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        showNotification(title, body, URGENT_CHANNEL, asignacionId != null ? asignacionId : 1000, intent);
    }

    private void showCalificacionNotification(android.os.Bundle data) {
        String titulo = "🌟 Calificación Disponible";
        String cuerpo = data.getString("cuerpo", "Una tarea ha sido calificada");
        Integer asignacionId = data.getString("asignacion_id") != null ? 
            Integer.parseInt(data.getString("asignacion_id")) : null;
        
        Intent intent = new Intent(this, TareaDetalleActivity.class);
        if (asignacionId != null) {
            intent.putExtra("TAREA_ID", asignacionId);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        showNotification(titulo, cuerpo, TAREAS_CHANNEL, asignacionId != null ? asignacionId : 0, intent);
    }

    private void showGeneralNotification(String title, String body, String channel) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        showNotification(title, body, channel, 9999, intent);
    }

    private void showNotification(String title, String body, String channel, int notificationId, Intent intent) {
        createNotificationChannels();
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);
        
        // Configuración específica para canal urgente
        if (URGENT_CHANNEL.equals(channel)) {
            builder.setVibrate(new long[]{0, 500, 200, 500});
        }
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        
        // Verificar permisos para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(notificationId, builder.build());
            } else {
                Log.w(TAG, "Notifications not enabled");
            }
        } else {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal de Tareas
            NotificationChannel tareasChannel = new NotificationChannel(
                TAREAS_CHANNEL,
                "Tareas",
                NotificationManager.IMPORTANCE_HIGH
            );
            tareasChannel.setDescription("Notificaciones sobre tareas asignadas y calificaciones");
            tareasChannel.enableLights(true);
            tareasChannel.setLightColor(android.graphics.Color.BLUE);
            
            // Canal Urgente
            NotificationChannel urgentChannel = new NotificationChannel(
                URGENT_CHANNEL,
                "Urgentes",
                NotificationManager.IMPORTANCE_HIGH
            );
            urgentChannel.setDescription("Notificaciones urgentes de tareas vencidas");
            urgentChannel.enableVibration(true);
            urgentChannel.enableLights(true);
            urgentChannel.setLightColor(android.graphics.Color.RED);
            
            // Canal General
            NotificationChannel generalChannel = new NotificationChannel(
                GENERAL_CHANNEL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("Notificaciones generales de la aplicación");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(Arrays.asList(
                tareasChannel, urgentChannel, generalChannel
            ));
        }
    }

    private void saveTokenLocally(String token) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("FCM_TOKEN", token).apply();
    }

    private void registerTokenWithBackend(String token) {
        // Verificar si hay sesión activa
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String authToken = prefs.getString("AUTH_TOKEN", null);
        
        if (authToken != null) {
            // Crear información del dispositivo
            DeviceInfo deviceInfo = new DeviceInfo(
                token,
                "android",
                "1.0", // Versión hardcoded temporalmente
                android.os.Build.MODEL,
                android.os.Build.VERSION.RELEASE
            );
            
            // Registrar token en backend
            RetrofitClient.getApiService(this).registerFCMToken(
                "Bearer " + authToken,
                deviceInfo
            ).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "FCM Token registered successfully");
                    } else {
                        Log.e(TAG, "Failed to register FCM token: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error registering FCM token", t);
                }
            });
        } else {
            Log.d(TAG, "No active session, token will be registered on login");
        }
    }
}
