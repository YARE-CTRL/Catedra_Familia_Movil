package com.example.catedra_fam.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.catedra_fam.api.ApiService;
import com.example.catedra_fam.api.RetrofitClient;
import com.example.catedra_fam.models.Notificacion;
import com.example.catedra_fam.models.NotificacionesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NotificationSyncWorker extends Worker {

    private static final String TAG = "NotificationSyncWorker";

    public NotificationSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting notification sync work");
        
        try {
            // Verificar si hay sesión activa
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String authToken = prefs.getString("AUTH_TOKEN", null);
            
            if (authToken == null) {
                Log.d(TAG, "No active session, skipping sync");
                return Result.success();
            }
            
            // Obtener notificaciones del backend
            ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
            
            Call<NotificacionesResponse> call = apiService.getNotificaciones(1, 50, null, false);

            // Síncrono para Worker
            Response<NotificacionesResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                NotificacionesResponse notifResponse = response.body();

                if (notifResponse.isSuccess()) {
                    List<Notificacion> notifications = notifResponse.getData();

                    // Procesar notificaciones no leídas
                    int unreadCount = 0;
                    if (notifResponse.getMeta() != null) {
                        unreadCount = notifResponse.getMeta().getNoLeidas();
                    } else {
                        for (Notificacion notification : notifications) {
                            if (!notification.isLeida()) {
                                unreadCount++;
                            }
                        }
                    }

                    // Verificar notificaciones urgentes
                    for (Notificacion notification : notifications) {
                        if (!notification.isLeida() && notification.getMetadatos() != null
                            && "alta".equals(notification.getMetadatos().getPrioridad())) {
                            // Aquí se podría mostrar una notificación local si es necesario
                            Log.d(TAG, "Urgent notification found: " + notification.getAsunto());
                        }
                    }
                    
                    // Guardar contador de notificaciones no leídas
                    prefs.edit().putInt("UNREAD_NOTIFICATIONS", unreadCount).apply();
                    
                    Log.d(TAG, "Sync completed. Unread notifications: " + unreadCount);
                    return Result.success();
                } else {
                    Log.e(TAG, "API returned error");
                    return Result.failure();
                }
            } else {
                Log.e(TAG, "HTTP error: " + response.code());
                return Result.failure();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error during notification sync", e);
            return Result.failure();
        }
    }
}
