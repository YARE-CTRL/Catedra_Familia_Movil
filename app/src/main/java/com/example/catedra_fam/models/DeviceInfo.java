package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

public class DeviceInfo {
    @SerializedName("fcmToken")
    private String fcmToken;

    @SerializedName("dispositivo")
    private String dispositivo;

    @SerializedName("sistemaOperativo")
    private String sistemaOperativo;

    @SerializedName("versionApp")
    private String versionApp;

    public DeviceInfo() {}

    public DeviceInfo(String fcmToken, String dispositivo, String sistemaOperativo, String versionApp) {
        this.fcmToken = fcmToken;
        this.dispositivo = dispositivo;
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
                ", sistemaOperativo='" + sistemaOperativo + '\'' +
                ", versionApp='" + versionApp + '\'' +
                '}';
    }
}
