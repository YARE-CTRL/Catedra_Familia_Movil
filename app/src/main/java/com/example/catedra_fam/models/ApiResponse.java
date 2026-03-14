package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * ✅ ACTUALIZADO (13/Feb/2026): Respuesta genérica de la API
 * Estructura del backend:
 * {
 *   "success": true,
 *   "data": [...],
 *   "meta": {
 *     "total": 15,
 *     "pendientes": 5,
 *     "vencidas": 3,
 *     "entregadas": 4,
 *     "calificadas": 3,
 *     "porAtender": 8
 *   },
 *   "message": "...",
 *   "error": "..."
 * }
 */
public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private T data;

    @SerializedName("meta")
    private Object meta; // ✅ NUEVO: Para contadores y metadatos

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

