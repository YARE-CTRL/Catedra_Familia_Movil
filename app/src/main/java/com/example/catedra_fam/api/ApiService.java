package com.example.catedra_fam.api;

import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.Asignacion;
import com.example.catedra_fam.models.DeviceInfo;
import com.example.catedra_fam.models.Estudiante;
import com.example.catedra_fam.models.EstudianteInfo;
import com.example.catedra_fam.models.TareaLista;
import com.example.catedra_fam.models.TareaDetalle;
import com.example.catedra_fam.models.Entrega;
import com.example.catedra_fam.models.Notificacion;
import com.example.catedra_fam.models.LoginRequest;
import com.example.catedra_fam.models.LoginResponse;
import com.example.catedra_fam.models.CambiarPasswordRequest;
import com.example.catedra_fam.models.RecuperarPasswordRequest;
import com.example.catedra_fam.models.Estadisticas;
import com.example.catedra_fam.models.Preferencias;
import com.example.catedra_fam.models.HistorialResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interfaz ApiService - Endpoints de la API Móvil
 * Base URL: https://escuelaparapadres-backend.onrender.com/api/movil/
 *
 * Carta de Entrega Backend - 22 enero 2026
 */
public interface ApiService {

    // ========== RF-MO-001: AUTENTICACIÓN ==========

    /**
     * RF-MO-001: Inicio de sesión móvil
     * POST /auth/login/movil
     * El backend devuelve LoginResponse directamente (no envuelto en ApiResponse)
     */
    @POST("auth/login/movil")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * RF-MO-002: Cambio de contraseña (obligatorio primer ingreso)
     * POST /auth/cambiar-password
     */
    @POST("auth/cambiar-password")
    Call<ApiResponse<String>> cambiarPassword(@Body CambiarPasswordRequest request);

    /**
     * RF-MO-003: Recuperación de contraseña - Solicitar código
     * POST /auth/recuperar/solicitar
     */
    @POST("auth/recuperar/solicitar")
    Call<ApiResponse<String>> solicitarRecuperacion(@Body Map<String, String> body);

    /**
     * RF-MO-003: Recuperación de contraseña - Verificar código
     * POST /auth/recuperar/verificar
     */
    @POST("auth/recuperar/verificar")
    Call<ApiResponse<String>> verificarCodigo(@Body Map<String, String> body);

    /**
     * RF-MO-003: Recuperación de contraseña - Restablecer
     * POST /auth/recuperar/restablecer
     */
    @POST("auth/recuperar/restablecer")
    Call<ApiResponse<String>> restablecerPassword(@Body RecuperarPasswordRequest request);

    // ========== RF-MO-005 a RF-MO-009: TAREAS Y ENTREGAS ==========

    /**
     * Obtener datos del estudiante autenticado
     * GET /estudiantes
     */
    @GET("estudiantes")
    Call<ApiResponse<EstudianteInfo>> getDatosEstudiante();

    /**
     * RF-MO-005: Listar tareas asignadas a un estudiante
     * GET /estudiantes/:id/tareas
     */
    @GET("estudiantes/{id}/tareas")
    Call<ApiResponse<List<TareaLista>>> getTareas(
        @Path("id") int estudianteId,
        @Query("estado") String estado, // pendiente, completada, vencida
        @Query("periodo") Integer periodoId // Período académico (opcional)
    );

    /**
     * RF-MO-006: Detalle de una tarea específica
     * GET /asignaciones/:id/detalle
     */
    @GET("asignaciones/{id}/detalle")
    Call<ApiResponse<TareaDetalle>> getDetalleTarea(@Path("id") int asignacionId);

    /**
     * RF-MO-007: Enviar evidencia de tarea (multipart con archivos)
     * POST /asignaciones/:id/entregas
     * Soporta archivos hasta 50MB: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT,
     * JPG, JPEG, PNG, WEBP, GIF, MP4, MOV, MKV, AVI, MP3, WAV, M4A
     */
    @Multipart
    @POST("asignaciones/{id}/entregas")
    Call<ApiResponse<Entrega>> enviarEvidencia(
        @Path("id") int asignacionId,
        @Part("estudianteId") RequestBody estudianteId,
        @Part("descripcion") RequestBody descripcion,
        @Part("nombreEnvio") RequestBody nombreEnvio,
        @Part List<MultipartBody.Part> archivos
    );

    /**
     * RF-MO-008: Editar entrega ya enviada
     * PUT /entregas/:id
     * Solo permitido si: fecha límite no pasó y tarea no está calificada
     */
    @Multipart
    @PUT("entregas/{id}")
    Call<ApiResponse<Entrega>> editarEntrega(
        @Path("id") int entregaId,
        @Part("descripcion") RequestBody descripcion,
        @Part List<MultipartBody.Part> archivos
    );

    /**
     * RF-MO-009: Sincronización offline - Enviar entregas pendientes
     * POST /asignaciones/:id/entregas/sync
     */
    @Multipart
    @POST("asignaciones/{id}/entregas/sync")
    Call<ApiResponse<Entrega>> sincronizarEntrega(
        @Path("id") int asignacionId,
        @Part("estudianteId") RequestBody estudianteId,
        @Part("descripcion") RequestBody descripcion,
        @Part("fechaEntregaLocal") RequestBody fechaEntregaLocal,
        @Part List<MultipartBody.Part> archivos
    );

    /**
     * RF-MO-009: Sincronización offline - Obtener tareas actualizadas
     * GET /estudiantes/:id/tareas/sync
     */
    @GET("estudiantes/{id}/tareas/sync")
    Call<ApiResponse<List<TareaLista>>> sincronizarTareas(
        @Path("id") int estudianteId,
        @Query("ultimaSync") String ultimaSincronizacion // ISO 8601 format
    );

    // ========== RF-MO-010 a RF-MO-013: HISTORIAL Y PERFIL ==========

    /**
     * RF-MO-010: Historial de entregas y calificaciones
     * GET /estudiantes/:id/historial
     */
    @GET("estudiantes/{id}/historial")
    Call<ApiResponse<HistorialResponse>> getHistorial(
        @Path("id") int estudianteId,
        @Query("periodoId") Integer periodoId
    );

    /**
     * RF-MO-011: Estadísticas del estudiante
     * GET /estudiantes/:id/estadisticas
     */
    @GET("estudiantes/{id}/estadisticas")
    Call<ApiResponse<Estadisticas>> getEstadisticas(@Path("id") int estudianteId);

    /**
     * RF-MO-012: Mis estudiantes (hijos del acudiente)
     * GET /acudientes/mis-estudiantes
     */
    @GET("acudientes/mis-estudiantes")
    Call<ApiResponse<List<Estudiante>>> getMisEstudiantes();

    /**
     * RF-MO-013: Perfil completo de un estudiante
     * GET /estudiantes/:id/perfil
     */
    @GET("estudiantes/{id}/perfil")
    Call<ApiResponse<Estudiante>> getPerfilEstudiante(@Path("id") int estudianteId);

    // ========== RF-MO-014 a RF-MO-016: NOTIFICACIONES ==========

    /**
     * RF-MO-014: Registrar token FCM con información del dispositivo
     * POST /notificaciones/token
     */
    @POST("notificaciones/token")
    Call<Void> registerFCMToken(@Header("Authorization") String authToken, @Body DeviceInfo deviceInfo);

    /**
     * RF-MO-015: Lista de notificaciones del usuario
     * GET /notificaciones
     */
    @GET("notificaciones")
    Call<ApiResponse<List<Notificacion>>> getNotificaciones(
        @Query("leidas") Boolean leidas,
        @Query("limit") Integer limit
    );

    /**
     * RF-MO-016: Marcar notificación como leída
     * PUT /notificaciones/:id/leer
     */
    @PUT("notificaciones/{id}/leer")
    Call<ApiResponse<String>> marcarNotificacionLeida(@Path("id") int notificacionId);

    /**
     * RF-MO-016: Marcar todas las notificaciones como leídas
     * PUT /notificaciones/leer-todas
     */
    @PUT("notificaciones/leer-todas")
    Call<ApiResponse<String>> marcarTodasLeidas();

    // ========== RF-MO-017: PREFERENCIAS ==========

    /**
     * RF-MO-017: Obtener preferencias del usuario
     * GET /usuarios/preferencias
     */
    @GET("usuarios/preferencias")
    Call<ApiResponse<Preferencias>> getPreferencias();

    /**
     * RF-MO-017: Actualizar preferencias del usuario
     * PUT /usuarios/preferencias
     */
    @PUT("usuarios/preferencias")
    Call<ApiResponse<Preferencias>> actualizarPreferencias(@Body Preferencias preferencias);

    // ========== RF-MO-018: SOPORTE ==========

    /**
     * RF-MO-018: Información de soporte (FAQs, contacto)
     * GET /soporte/info
     */
    @GET("soporte/info")
    Call<ApiResponse<Map<String, Object>>> getInfoSoporte();
}

