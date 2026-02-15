package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para login móvil
 * RF-MO-001
 * URL: https://escuelaparapadres-backend-1.onrender.com/api/movil/auth/login/movil
 * CORREGIDO: Usar campos documento y password según backend
 */
public class LoginRequest {
    @SerializedName("documento")
    private String numeroDocumento;

    @SerializedName("password")
    private String contrasena;

    @SerializedName("tokenFCM")
    private String tokenFCM; // Opcional

    public LoginRequest(String numeroDocumento, String contrasena) {
        this.numeroDocumento = numeroDocumento;
        this.contrasena = contrasena;
    }

    public LoginRequest(String numeroDocumento, String contrasena, String tokenFCM) {
        this.numeroDocumento = numeroDocumento;
        this.contrasena = contrasena;
        this.tokenFCM = tokenFCM;
    }

    // Getters y Setters
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTokenFCM() {
        return tokenFCM;
    }

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }
}

