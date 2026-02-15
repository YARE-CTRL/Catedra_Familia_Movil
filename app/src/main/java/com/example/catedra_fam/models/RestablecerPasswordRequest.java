package com.example.catedra_fam.models;

/**
 * Request para restablecer contraseña usando token OTP
 * POST /auth/recuperar/restablecer
 */
public class RestablecerPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;

    public RestablecerPasswordRequest() {}

    public RestablecerPasswordRequest(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
