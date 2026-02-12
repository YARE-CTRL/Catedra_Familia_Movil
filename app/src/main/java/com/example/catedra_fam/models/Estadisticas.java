package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de estadísticas del estudiante
 * RF-MO-011
 */
public class Estadisticas {
    @SerializedName("totalTareas")
    private int totalTareas;

    @SerializedName("tareasPendientes")
    private int tareasPendientes;

    @SerializedName("tareasCompletadas")
    private int tareasCompletadas;

    @SerializedName("tareasVencidas")
    private int tareasVencidas;

    @SerializedName("promedioGeneral")
    private double promedioGeneral;

    @SerializedName("tasaEntrega")
    private double tasaEntrega; // Porcentaje 0-100

    @SerializedName("ultimaEntrega")
    private String ultimaEntrega; // Fecha ISO 8601

    // Getters y Setters
    public int getTotalTareas() {
        return totalTareas;
    }

    public void setTotalTareas(int totalTareas) {
        this.totalTareas = totalTareas;
    }

    public int getTareasPendientes() {
        return tareasPendientes;
    }

    public void setTareasPendientes(int tareasPendientes) {
        this.tareasPendientes = tareasPendientes;
    }

    public int getTareasCompletadas() {
        return tareasCompletadas;
    }

    public void setTareasCompletadas(int tareasCompletadas) {
        this.tareasCompletadas = tareasCompletadas;
    }

    public int getTareasVencidas() {
        return tareasVencidas;
    }

    public void setTareasVencidas(int tareasVencidas) {
        this.tareasVencidas = tareasVencidas;
    }

    public double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public double getTasaEntrega() {
        return tasaEntrega;
    }

    public void setTasaEntrega(double tasaEntrega) {
        this.tasaEntrega = tasaEntrega;
    }

    public String getUltimaEntrega() {
        return ultimaEntrega;
    }

    public void setUltimaEntrega(String ultimaEntrega) {
        this.ultimaEntrega = ultimaEntrega;
    }
}

