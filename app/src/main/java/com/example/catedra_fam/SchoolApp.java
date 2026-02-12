package com.example.catedra_fam;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.graphics.Color;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.catedra_fam.services.FCMNotificationService;
import com.example.catedra_fam.workers.NotificationSyncWorker;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SchoolApp extends Application {

    private static final String TAG = "SchoolApp";

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "SchoolApp initialized");
        
        // Configurar canales de notificación
        createNotificationChannels();
        
        // Configurar worker periódico para sincronización
        setupPeriodicSync();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal de Tareas
            NotificationChannel tareasChannel = new NotificationChannel(
                FCMNotificationService.TAREAS_CHANNEL,
                "Tareas",
                NotificationManager.IMPORTANCE_HIGH
            );
            tareasChannel.setDescription("Notificaciones sobre tareas asignadas y calificaciones");
            tareasChannel.enableLights(true);
            tareasChannel.setLightColor(Color.BLUE);
            tareasChannel.setShowBadge(true);
            
            // Canal Urgente
            NotificationChannel urgentChannel = new NotificationChannel(
                FCMNotificationService.URGENT_CHANNEL,
                "Urgentes",
                NotificationManager.IMPORTANCE_HIGH
            );
            urgentChannel.setDescription("Notificaciones urgentes de tareas vencidas");
            urgentChannel.enableVibration(true);
            urgentChannel.enableLights(true);
            urgentChannel.setLightColor(Color.RED);
            urgentChannel.setShowBadge(true);
            
            // Canal General
            NotificationChannel generalChannel = new NotificationChannel(
                FCMNotificationService.GENERAL_CHANNEL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("Notificaciones generales de la aplicación");
            generalChannel.enableLights(true);
            generalChannel.setLightColor(Color.GRAY);
            generalChannel.setShowBadge(true);
            
            // Crear canales
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(java.util.Arrays.asList(
                tareasChannel, urgentChannel, generalChannel
            ));
            
            Log.d(TAG, "Notification channels created");
        }
    }

    private void setupPeriodicSync() {
        // Configurar constraints para el worker
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build();
        
        // Crear solicitud de trabajo periódico (cada 15 minutos)
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
            NotificationSyncWorker.class,
            15, // Repetir cada 15 minutos
            TimeUnit.MINUTES
        )
        .setConstraints(constraints)
        .build();
        
        // Enviar worker a WorkManager
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notification_sync",
            ExistingPeriodicWorkPolicy.KEEP, // Mantener existente si ya hay uno
            syncRequest
        );
        
        Log.d(TAG, "Periodic notification sync worker scheduled");
    }
}
