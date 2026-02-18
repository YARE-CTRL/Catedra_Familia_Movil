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
        Log.d(TAG, "🔄 Starting notification sync work (attempt " + (getRunAttemptCount() + 1) + ")");

        try {
            // Verificar si hay sesión activa
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String authToken = prefs.getString("AUTH_TOKEN", null);
            
            if (authToken == null) {
                Log.d(TAG, "⚠️ No active session, skipping sync");
                return Result.success();
            }
            
            Log.d(TAG, "📡 Fetching notifications from backend...");
            long startTime = System.currentTimeMillis();

            // Obtener notificaciones del backend
            ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
            
            Call<NotificacionesResponse> call = apiService.getNotificaciones(1, 50, null, false);

            // Síncrono para Worker
            Response<NotificacionesResponse> response = call.execute();

            long duration = System.currentTimeMillis() - startTime;
            Log.d(TAG, "⏱️ Request completed in " + duration + "ms");

            if (response.isSuccessful() && response.body() != null) {
                NotificacionesResponse notifResponse = response.body();

                if (notifResponse.isSuccess()) {
                    List<Notificacion> notifications = notifResponse.getData();
                    Log.d(TAG, "✅ Retrieved " + notifications.size() + " notifications");

                    // Procesar notificaciones no leídas
                    int unreadCount = 0;
                    if (notifResponse.getMeta() != null) {
                        unreadCount = notifResponse.getMeta().getNoLeidas();
                        Log.d(TAG, "📊 Unread count from meta: " + unreadCount);
                    } else {
                        for (Notificacion notification : notifications) {
                            if (!notification.isLeida()) {
                                unreadCount++;
                            }
                        }
                        Log.d(TAG, "📊 Unread count calculated: " + unreadCount);
                    }

                    // Verificar notificaciones urgentes
                    int urgentCount = 0;
                    for (Notificacion notification : notifications) {
                        if (!notification.isLeida() && notification.getMetadatos() != null
                            && "alta".equals(notification.getMetadatos().getPrioridad())) {
                            urgentCount++;
                            Log.d(TAG, "🚨 Urgent notification: " + notification.getAsunto());
                        }
                    }

                    if (urgentCount > 0) {
                        Log.d(TAG, "🚨 Total urgent notifications: " + urgentCount);
                    }

                    // Guardar contador de notificaciones no leídas
                    prefs.edit().putInt("UNREAD_NOTIFICATIONS", unreadCount).apply();
                    
                    Log.d(TAG, "✅ Sync completed successfully. Unread: " + unreadCount);
                    return Result.success();
                } else {
                    Log.e(TAG, "❌ API returned error: success=false");
                    return Result.failure();
                }
            } else {
                Log.e(TAG, "❌ HTTP error: " + response.code());
                if (response.errorBody() != null) {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "❌ Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Could not read error body", e);
                    }
                }
                return Result.failure();
            }

        } catch (java.net.SocketTimeoutException e) {
            Log.e(TAG, "⏱️ Timeout error (server took too long to respond)", e);

            // ✅ Retry en caso de timeout (servidor lento o cold start)
            if (getRunAttemptCount() < 2) {
                Log.d(TAG, "🔄 Will retry due to timeout");
                return Result.retry();
            } else {
                Log.e(TAG, "❌ Max retries reached, giving up");
                return Result.failure();
            }

        } catch (java.io.IOException e) {
            Log.e(TAG, "🌐 Network error (no internet or server down)", e);

            // Retry en caso de error de red
            if (getRunAttemptCount() < 2) {
                Log.d(TAG, "🔄 Will retry due to network error");
                return Result.retry();
            } else {
                Log.e(TAG, "❌ Max retries reached, giving up");
                return Result.failure();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Unexpected error during notification sync", e);
            return Result.failure();
        }
    }
}
