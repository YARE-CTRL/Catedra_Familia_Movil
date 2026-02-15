package com.example.catedra_fam.models;

/**
 * Response al solicitar código OTP de recuperación
 * POST /auth/recuperar/solicitar
 */
public class OtpSolicitarResponse {
    private boolean success;
    private String message;
    private String metodo;      // "sms" o "email"
    private int expiraEn;       // segundos (900 = 15 min)
    private String codigoDebug; // solo en desarrollo

    public OtpSolicitarResponse() {}

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

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public int getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(int expiraEn) {
        this.expiraEn = expiraEn;
    }

    public String getCodigoDebug() {
        return codigoDebug;
    }

    public void setCodigoDebug(String codigoDebug) {
        this.codigoDebug = codigoDebug;
    }
}
