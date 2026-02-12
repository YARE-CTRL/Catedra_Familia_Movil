package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de Calificación
 * Usado en TareaDetalle y otros endpoints
 */
public class Calificacion {
    @SerializedName("id")
    private int id;

    @SerializedName("nota")
    private double nota;

    @SerializedName("escala")
    private String escala;

    @SerializedName("retroalimentacion")
    private String retroalimentacion;

    @SerializedName("calificadoEn")
    private String calificadoEn;

    @SerializedName("docenteId")
    private int docenteId;

    @SerializedName("docenteNombre")
    private String docenteNombre;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getEscala() {
        return escala;
    }

    public void setEscala(String escala) {
        this.escala = escala;
    }

    public String getRetroalimentacion() {
        return retroalimentacion;
    }

    public void setRetroalimentacion(String retroalimentacion) {
        this.retroalimentacion = retroalimentacion;
    }

    public String getCalificadoEn() {
        return calificadoEn;
    }

    public void setCalificadoEn(String calificadoEn) {
        this.calificadoEn = calificadoEn;
    }

    public int getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(int docenteId) {
        this.docenteId = docenteId;
    }

    public String getDocenteNombre() {
        return docenteNombre;
    }

    public void setDocenteNombre(String docenteNombre) {
        this.docenteNombre = docenteNombre;
    }

    // Métodos de conveniencia
    public boolean tieneNota() {
        return nota > 0;
    }

    public String getNotaConEscala() {
        if (escala != null && !escala.isEmpty()) {
            return escala + " (" + nota + ")";
        }
        return String.valueOf(nota);
    }
}
