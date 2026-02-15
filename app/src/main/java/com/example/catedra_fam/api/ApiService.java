package com.example.catedra_fam.api;

import com.example.catedra_fam.models.ApiResponse;
import com.example.catedra_fam.models.Asignacion;
import com.example.catedra_fam.models.DeviceInfo;
import com.example.catedra_fam.models.Estudiante;
import com.example.catedra_fam.models.EstudianteInfo;
import com.example.catedra_fam.models.EstadisticasTareas;
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
// ✅ NUEVOS IMPORTS - Modelos OTP
import com.example.catedra_fam.models.OtpSolicitarRequest;
import com.example.catedra_fam.models.OtpSolicitarResponse;
import com.example.catedra_fam.models.OtpVerificarRequest;
import com.example.catedra_fam.models.OtpVerificarResponse;
import com.example.catedra_fam.models.RestablecerPasswordRequest;
// ✅ NUEVOS IMPORTS - Respuestas actualizadas
import com.example.catedra_fam.models.NotificacionesResponse;
import com.example.catedra_fam.models.PreferenciasResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interfaz ApiService - Endpoints de la API Móvil
 * Base URL: https://escuelaparapadres-backend-1.onrender.com/api/
 * CORREGIDO: Removido /movil/ para alinear con backend actual
 *
 * Carta de Entrega Backend - 22 enero 2026
 */
public interface ApiService {

    // ========== RF-MO-001: AUTENTICACIÓN ==========

    /**
     * RF-MO-001: Inicio de sesión móvil para acudientes
     * POST /movil/auth/login/movil - Endpoint específico para móvil
     * El backend devuelve LoginResponse directamente (no envuelto en ApiResponse)
     */
    @POST("movil/auth/login/movil")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * RF-MO-002: Cambio de contraseña (obligatorio primer ingreso)
     * POST /auth/cambiar-password
     */
    @POST("auth/cambiar-password")
    Call<ApiResponse<String>> cambiarPassword(@Body CambiarPasswordRequest request);

    /**
     * RF-MO-003: Recuperación de contraseña - Solicitar código OTP
     * POST /auth/recuperar/solicitar
     * ✅ ACTUALIZADO: Backend ahora valida contra BD y devuelve estructura específica
     */
    @POST("auth/recuperar/solicitar")
    Call<OtpSolicitarResponse> solicitarRecuperacion(@Body OtpSolicitarRequest request);

    /**
     * RF-MO-003: Recuperación de contraseña - Verificar código OTP
     * POST /auth/recuperar/verificar
     * ✅ ACTUALIZADO: Backend valida códigos reales, incluye intentosRestantes
     */
    @POST("auth/recuperar/verificar")
    Call<OtpVerificarResponse> verificarCodigo(@Body OtpVerificarRequest request);

    /**
     * RF-MO-003: Recuperación de contraseña - Restablecer contraseña
     * POST /auth/recuperar/restablecer
     * ✅ ACTUALIZADO: Usa token del paso anterior
     */
    @POST("auth/recuperar/restablecer")
    Call<ApiResponse<String>> restablecerPassword(@Body RestablecerPasswordRequest request);

    // ========== RF-MO-005 a RF-MO-009: TAREAS Y ENTREGAS ==========


    /**
     * RF-MO-005: Listar tareas asignadas a un estudiante
     * GET /movil/estudiantes/:id/tareas
     */
    @GET("movil/estudiantes/{id}/tareas")
    Call<ApiResponse<List<TareaLista>>> getTareas(
        @Path("id") int estudianteId,
        @Query("estado") String estado, // pendiente, completada, vencida
        @Query("periodo") Integer periodoId // Período académico (opcional)
    );

    /**
     * ✅ NUEVO: Estadísticas de tareas para el contador
     * GET /movil/estudiantes/:id/estadisticas
     */
    @GET("movil/estudiantes/{id}/estadisticas")
    Call<ApiResponse<EstadisticasTareas>> getEstadisticasTareas(@Path("id") int estudianteId);

    /**
     * RF-MO-006: Detalle de una tarea específica
     * GET /movil/asignaciones/:id/detalle
     */
    @GET("movil/asignaciones/{id}/detalle")
    Call<ApiResponse<TareaDetalle>> getDetalleTarea(@Path("id") int asignacionId);

    /**
     * RF-MO-007: Enviar evidencia de tarea (multipart con archivos)
     * POST /asignaciones/:id/entregas
     * Soporta archivos hasta 50MB: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT,
     * JPG, JPEG, PNG, WEBP, GIF, MP4, MOV, MKV, AVI, MP3, WAV, M4A
     */
    @Multipart
    @POST("movil/asignaciones/{id}/entregas")
    Call<ApiResponse<Entrega>> enviarEvidencia(
        @Path("id") int asignacionId,
        @Part("estudianteId") RequestBody estudianteId,
        @Part("descripcion") RequestBody descripcion,
        @Part("nombreEnvio") RequestBody nombreEnvio,
        @Part List<MultipartBody.Part> archivos
    );

    /**
     * RF-MO-008: Editar entrega ya enviada
     * PUT /movil/entregas/:id
     * Solo permitido si: fecha límite no pasó y tarea no está calificada
     */
    @Multipart
    @PUT("movil/entregas/{id}")
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
     * RF-MO-012: Mis estudiantes (hijos del acudiente) - ENDPOINT CORREGIDO
     * GET /acudientes/mis-estudiantes - ALIAS CREADO POR BACKEND
     * Estructura: { success: true, data: { estudiante: {...} } } para 1 estudiante
     *            { success: true, data: { estudiantes: [...] } } para múltiples
     */
    @GET("acudientes/mis-estudiantes")
    Call<ApiResponse<EstudianteInfo>> getMisEstudiantes();

    /**
     * RF-MO-013: Perfil completo de un estudiante
     * GET /estudiantes/:id/perfil
     */
    @GET("estudiantes/{id}/perfil")
    Call<ApiResponse<Estudiante>> getPerfilEstudiante(@Path("id") int estudianteId);

    // ========== RF-MO-014 a RF-MO-016: NOTIFICACIONES ==========

    /**
     * RF-MO-014: Registrar token FCM con información del dispositivo
     * POST /movil/notificaciones/token
     */
    @POST("movil/notificaciones/token")
    Call<Void> registerFCMToken(@Header("Authorization") String authToken, @Body DeviceInfo deviceInfo);

    /**
     * RF-MO-015: Lista de notificaciones del usuario
     * GET /movil/notificaciones
     * ✅ ACTUALIZADO: Soporte paginación y filtro "leida" (no "leidas")
     */
    @GET("movil/notificaciones")
    Call<NotificacionesResponse> getNotificaciones(
        @Query("page") Integer page,
        @Query("limit") Integer limit,
        @Query("tipo") String tipo,
        @Query("leida") Boolean leida  // Cambió de "leidas" a "leida"
    );

    /**
     * RF-MO-016: Marcar notificación como leída
     * PUT /movil/notificaciones/:id/leer
     */
    @PUT("movil/notificaciones/{id}/leer")
    Call<ApiResponse<String>> marcarNotificacionLeida(@Path("id") int notificacionId);

    /**
     * RF-MO-016: Marcar TODAS las notificaciones como leídas
     * PUT /movil/notificaciones/leer-todas
     */
    @PUT("movil/notificaciones/leer-todas")
    Call<ApiResponse<String>> marcarTodasNotificacionesLeidas();

    /**
     * 🆕 RF-MO-019: Eliminar una notificación específica
     * DELETE /movil/notificaciones/:id
     */
    @DELETE("movil/notificaciones/{id}")
    Call<ApiResponse<String>> eliminarNotificacion(@Path("id") int notificacionId);

    /**
     * 🆕 RF-MO-020: Eliminar todas las notificaciones del usuario
     * DELETE /movil/notificaciones
     */
    @DELETE("movil/notificaciones")
    Call<ApiResponse<String>> eliminarTodasNotificaciones();

    // ========== RF-MO-017: PREFERENCIAS ==========

    /**
     * RF-MO-017: Obtener preferencias del usuario
     * GET /usuarios/preferencias
     * ✅ ACTUALIZADO: Estructura anidada data.notificaciones.{tipos}
     */
    @GET("usuarios/preferencias")
    Call<PreferenciasResponse> getPreferencias();

    /**
     * RF-MO-017: Actualizar preferencias del usuario
     * PUT /usuarios/preferencias
     * ✅ ACTUALIZADO: Permite updates parciales, estructura anidada
     */
    @PUT("usuarios/preferencias")
    Call<ApiResponse<PreferenciasResponse.PreferenciasData>> actualizarPreferencias(@Body Map<String, Object> preferencias);

    // ========== RF-MO-018: SOPORTE ==========

    /**
     * RF-MO-018: Información de soporte (FAQs, contacto)
     * GET /soporte/info
     */
    @GET("soporte/info")
    Call<ApiResponse<Map<String, Object>>> getInfoSoporte();

    // Fin de la interfaz ApiService
}
