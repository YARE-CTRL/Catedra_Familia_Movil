package com.example.catedra_fam.models;

/**
 * Request para solicitar código OTP de recuperación
 * POST /auth/recuperar/solicitar
 */
public class OtpSolicitarRequest {
    private String contacto;

    public OtpSolicitarRequest() {}

    public OtpSolicitarRequest(String contacto) {
        this.contacto = contacto;
    }

    // Getters y setters
    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
}
