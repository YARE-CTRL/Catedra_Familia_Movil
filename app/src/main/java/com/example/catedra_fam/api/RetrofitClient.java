package com.example.catedra_fam.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * RetrofitClient - Cliente HTTP configurado con interceptores
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://escuelaparapadres-backend-1.onrender.com/api/";
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    /**
     * Obtiene la instancia de ApiService configurada
     * @param context Contexto de la aplicación
     * @return ApiService configurado
     */
    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            retrofit = getRetrofitInstance(context);
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    /**
     * Crea la instancia de Retrofit con todas las configuraciones
     */
    private static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Logging interceptor para debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // AuthInterceptor que EXCLUYE el endpoint de login
            AuthInterceptor authInterceptor = new AuthInterceptor(context.getApplicationContext());

            // Configurar OkHttpClient con interceptores
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)          // Agregar token (excepto login)
                    .addInterceptor(loggingInterceptor)       // Logging
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Crear Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Reinicia la instancia (útil para testing o logout)
     */
    public static void resetInstance() {
        retrofit = null;
        apiService = null;
    }
}
