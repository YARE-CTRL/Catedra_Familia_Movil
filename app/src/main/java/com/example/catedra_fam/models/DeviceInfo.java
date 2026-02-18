package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo DeviceInfo - Información del dispositivo para registro FCM
 * ✅ ACTUALIZADO: Agregado campo "plataforma" requerido por backend (16 Feb 2026)
 */
public class DeviceInfo {
    @SerializedName("fcmToken")
    private String fcmToken;

    @SerializedName("dispositivo")
    private String dispositivo;

    /**
     * ✅ NUEVO CAMPO - Requerido por backend actualizado
     * Valores: "Android" o "iOS"
     */
    @SerializedName("plataforma")
    private String plataforma;

    /**
     * Campo legacy - mantener para compatibilidad
     * Valores: "android" o "ios" (lowercase)
     */
    @SerializedName("sistemaOperativo")
    private String sistemaOperativo;

    @SerializedName("versionApp")
    private String versionApp;

    public DeviceInfo() {}

    /**
     * Constructor actualizado con campo plataforma
     * @param fcmToken Token FCM del dispositivo
     * @param dispositivo Modelo del dispositivo (ej: "moto e40")
     * @param plataforma Plataforma con mayúscula ("Android" o "iOS")
     * @param sistemaOperativo Sistema operativo lowercase ("android" o "ios")
     * @param versionApp Versión de la app (ej: "1.0.0")
     */
    public DeviceInfo(String fcmToken, String dispositivo, String plataforma, String sistemaOperativo, String versionApp) {
        this.fcmToken = fcmToken;
        this.dispositivo = dispositivo;
        this.plataforma = plataforma;
        this.sistemaOperativo = sistemaOperativo;
        this.versionApp = versionApp;
    }

    // Getters y Setters
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getVersionApp() {
        return versionApp;
    }

    public void setVersionApp(String versionApp) {
        this.versionApp = versionApp;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "fcmToken='" + fcmToken + '\'' +
                ", dispositivo='" + dispositivo + '\'' +
                ", plataforma='" + plataforma + '\'' +
                ", sistemaOperativo='" + sistemaOperativo + '\'' +
                ", versionApp='" + versionApp + '\'' +
                '}';
    }
}
