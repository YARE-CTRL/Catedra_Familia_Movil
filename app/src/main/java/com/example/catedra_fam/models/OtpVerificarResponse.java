package com.example.catedra_fam.models;

/**
 * Response al verificar código OTP de recuperación
 * POST /auth/recuperar/verificar
 */
public class OtpVerificarResponse {
    private boolean success;
    private String message;
    private String token;           // Token para paso 3 (restablecer)
    private int intentosRestantes;  // Solo cuando success = false

    public OtpVerificarResponse() {}

    // Getters y setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }

    public void setIntentosRestantes(int intentosRestantes) {
        this.intentosRestantes = intentosRestantes;
    }
}
