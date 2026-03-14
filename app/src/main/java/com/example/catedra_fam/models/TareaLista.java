package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para tarea en lista según especificaciones del backend
 * GET /estudiantes/{id}/tareas
 */
public class TareaLista {
    @SerializedName("id")
    private int id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("fechaPublicacion")
    private String fechaPublicacion;

    @SerializedName("fechaVencimiento")
    private String fechaVencimiento;

    @SerializedName("diasRestantes")
    private int diasRestantes;

    @SerializedName("estado")
    private String estado; // pendiente, próximo_vencimiento, vencida, entregada, calificada

    @SerializedName("frecuencia")
    private String frecuencia; // unica, semanal, quincenal, mensual

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(int diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    // Métodos de conveniencia para UI
    public boolean isVencida() {
        return "vencida".equals(estado);
    }

    public boolean isPendiente() {
        return "pendiente".equals(estado);
    }

    public boolean isProximoVencimiento() {
        return "próximo_vencimiento".equals(estado);
    }

    public boolean isEntregada() {
        return "entregada".equals(estado);
    }

    public boolean isCalificada() {
        return "calificada".equals(estado);
    }

    /**
     * Obtiene prioridad visual para ordenamiento UI
     * Mayor número = más urgente/importante
     */
    public int getPrioridadVisual() {
        switch (estado.toLowerCase()) {
            case "próximo_vencimiento":
                return 10;
            case "pendiente":
                if (diasRestantes <= 1) return 9;
                if (diasRestantes <= 3) return 7;
                return 5;
            case "vencida":
                return Math.abs(diasRestantes) + 6;
            case "entregada":
                return 3;
            case "calificada":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Verifica si la tarea es urgente (para destacar en UI)
     */
    public boolean isUrgente() {
        return isProximoVencimiento() ||
               (isPendiente() && diasRestantes <= 1) ||
               isVencida();
    }

    /**
     * Obtiene descripción amigable del tiempo restante
     */
    public String getDescripcionTiempo() {
        if (diasRestantes > 1) {
            return "Vence en " + diasRestantes + " días";
        } else if (diasRestantes == 1) {
            return "Vence mañana";
        } else if (diasRestantes == 0) {
            return "Vence hoy";
        } else if (diasRestantes == -1) {
            return "Venció ayer";
        } else {
            return "Venció hace " + Math.abs(diasRestantes) + " días";
        }
    }
}

