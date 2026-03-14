package com.example.catedra_fam.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AuthInterceptor - Agrega automáticamente el token de autenticación a las peticiones HTTP
 *
 * IMPORTANTE:
 * - NO agrega token al endpoint de login (/auth/login/movil)
 * - SÍ agrega token a todos los demás endpoints
 * - El token se obtiene de SharedPreferences después del login exitoso
 */
public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 🚨 EXCLUIR LOGIN - No agregar token al endpoint de login
        String url = originalRequest.url().toString();
        if (url.contains("/acudientes/login") || url.contains("/auth/login")) {
            // Para login, proceder sin Authorization header
            return chain.proceed(originalRequest);
        }

        // Obtener token de SharedPreferences para otros endpoints
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("AUTH_TOKEN", "");

        // Si hay token, agregarlo al header (solo para endpoints que no sean login)
        if (!token.isEmpty()) {
            Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}

