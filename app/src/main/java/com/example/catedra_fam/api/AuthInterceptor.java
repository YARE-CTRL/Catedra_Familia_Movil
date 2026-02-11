package com.example.catedra_fam.api;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * AuthInterceptor - Agrega el token de autorización a las peticiones
 * EXCEPTO al endpoint de login, que NO debe tener Authorization header
 */
public class AuthInterceptor implements Interceptor {
    private static final String PREFS_NAME = "CatedraFamiliaPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // ✅ CRÍTICO: NO agregar Authorization header al endpoint de login
        if (path.contains("/auth/login/movil") || path.contains("/login")) {
            // Enviar la petición sin modificar (sin Authorization header)
            return chain.proceed(originalRequest);
        }

        // Para otros endpoints, agregar el token si existe
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        if (token != null && !token.isEmpty()) {
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authenticatedRequest);
        }

        // Si no hay token, enviar sin Authorization
        return chain.proceed(originalRequest);
    }
}
