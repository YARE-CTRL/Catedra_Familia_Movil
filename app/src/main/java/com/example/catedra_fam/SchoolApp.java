package com.example.catedra_fam;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.catedra_fam.services.FCMNotificationService;
import com.example.catedra_fam.workers.NotificationSyncWorker;

import java.util.concurrent.TimeUnit;

/**
 * Application class para configuración global
 * Actualizada según especificaciones del backend FCM
 */
public class SchoolApp extends Application {

    private static final String TAG = "SchoolApp";

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "SchoolApp initialized");
        
        // Configurar canal único de notificación según backend
        createNotificationChannel();

        // Configurar worker periódico para sincronización
        setupPeriodicSync();
    }

    /**
     * Crea el canal único de notificación según especificación backend
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                FCMNotificationService.CATEDRA_FAMILIA_CHANNEL,
                FCMNotificationService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(FCMNotificationService.CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 250, 250, 250});
            channel.setShowBadge(true);
            channel.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Log.d(TAG, "Canal de notificación único creado: " + FCMNotificationService.CATEDRA_FAMILIA_CHANNEL);
        }
    }

    /**
     * Configura worker periódico para sincronización de notificaciones
     */
    private void setupPeriodicSync() {
        // ✅ Configurar constraints para el worker
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build();
        
        // ✅ Crear solicitud de trabajo periódico (cada 15 minutos)
        // Con backoff policy para manejar timeouts del servidor
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
            NotificationSyncWorker.class,
            15, // Repetir cada 15 minutos
            TimeUnit.MINUTES
        )
        .setConstraints(constraints)
        .setBackoffCriteria(
            androidx.work.BackoffPolicy.EXPONENTIAL,  // ✅ Backoff exponencial
            30,                                        // ✅ Inicial: 30 segundos
            TimeUnit.SECONDS
        )
        .build();
        
        // Enviar worker a WorkManager
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notification_sync",
            ExistingPeriodicWorkPolicy.KEEP, // Mantener existente si ya hay uno
            syncRequest
        );
        
        Log.d(TAG, "✅ Periodic notification sync worker scheduled (15min interval, exponential backoff)");
    }
}
