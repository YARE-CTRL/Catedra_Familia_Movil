package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para login móvil
 * RF-MO-001
 * URL: https://escuelaparapadres-backend-1.onrender.com/api/movil/auth/login/movil
 */
public class LoginRequest {
    @SerializedName("documento")
    private String documento;

    @SerializedName("password")
    private String password;

    @SerializedName("tokenFCM")
    private String tokenFCM; // Opcional

    public LoginRequest(String documento, String password) {
        this.documento = documento;
        this.password = password;
    }

    public LoginRequest(String documento, String password, String tokenFCM) {
        this.documento = documento;
        this.password = password;
        this.tokenFCM = tokenFCM;
    }

    // Getters y Setters
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenFCM() {
        return tokenFCM;
    }

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }
}

