package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request para cambiar contraseña
 * RF-MO-002
 */
public class CambiarPasswordRequest {
    @SerializedName("passwordActual")
    private String passwordActual;

    @SerializedName("passwordNuevo")
    private String passwordNuevo;

    public CambiarPasswordRequest(String passwordActual, String passwordNuevo) {
        this.passwordActual = passwordActual;
        this.passwordNuevo = passwordNuevo;
    }

    // Getters y Setters
    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNuevo() {
        return passwordNuevo;
    }

    public void setPasswordNuevo(String passwordNuevo) {
        this.passwordNuevo = passwordNuevo;
    }
}

