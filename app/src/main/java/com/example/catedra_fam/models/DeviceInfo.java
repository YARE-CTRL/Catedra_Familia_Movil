package com.example.catedra_fam.models;

public class DeviceInfo {
    private String fcmToken;
    private String plataforma;
    private String versionApp;
    private String modeloDispositivo;
    private String versionOs;

    public DeviceInfo() {}

    public DeviceInfo(String fcmToken, String plataforma, String versionApp, String modeloDispositivo, String versionOs) {
        this.fcmToken = fcmToken;
        this.plataforma = plataforma;
        this.versionApp = versionApp;
        this.modeloDispositivo = modeloDispositivo;
        this.versionOs = versionOs;
    }

    // Getters y Setters
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getVersionApp() {
        return versionApp;
    }

    public void setVersionApp(String versionApp) {
        this.versionApp = versionApp;
    }

    public String getModeloDispositivo() {
        return modeloDispositivo;
    }

    public void setModeloDispositivo(String modeloDispositivo) {
        this.modeloDispositivo = modeloDispositivo;
    }

    public String getVersionOs() {
        return versionOs;
    }

    public void setVersionOs(String versionOs) {
        this.versionOs = versionOs;
    }
}
