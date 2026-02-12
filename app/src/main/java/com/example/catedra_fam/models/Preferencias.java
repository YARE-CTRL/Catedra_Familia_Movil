package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de preferencias del usuario
 * RF-MO-017
 */
public class Preferencias {
    @SerializedName("notificacionesPush")
    private boolean notificacionesPush;

    @SerializedName("notificacionesEmail")
    private boolean notificacionesEmail;

    @SerializedName("recordatoriosTareas")
    private boolean recordatoriosTareas;

    @SerializedName("modoOscuro")
    private boolean modoOscuro;

    @SerializedName("idioma")
    private String idioma; // "es", "en"

    @SerializedName("horaRecordatorio")
    private String horaRecordatorio; // "HH:mm" format

    // Constructor por defecto
    public Preferencias() {
        // Valores por defecto
        this.notificacionesPush = true;
        this.notificacionesEmail = false;
        this.recordatoriosTareas = true;
        this.modoOscuro = false;
        this.idioma = "es";
        this.horaRecordatorio = "18:00";
    }

    // Getters y Setters
    public boolean isNotificacionesPush() {
        return notificacionesPush;
    }

    public void setNotificacionesPush(boolean notificacionesPush) {
        this.notificacionesPush = notificacionesPush;
    }

    public boolean isNotificacionesEmail() {
        return notificacionesEmail;
    }

    public void setNotificacionesEmail(boolean notificacionesEmail) {
        this.notificacionesEmail = notificacionesEmail;
    }

    public boolean isRecordatoriosTareas() {
        return recordatoriosTareas;
    }

    public void setRecordatoriosTareas(boolean recordatoriosTareas) {
        this.recordatoriosTareas = recordatoriosTareas;
    }

    public boolean isModoOscuro() {
        return modoOscuro;
    }

    public void setModoOscuro(boolean modoOscuro) {
        this.modoOscuro = modoOscuro;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getHoraRecordatorio() {
        return horaRecordatorio;
    }

    public void setHoraRecordatorio(String horaRecordatorio) {
        this.horaRecordatorio = horaRecordatorio;
    }
}

