package com.example.catedra_fam.models;

/**
 * Request para verificar código OTP de recuperación
 * POST /auth/recuperar/verificar
 */
public class OtpVerificarRequest {
    private String contacto;
    private String codigo;

    public OtpVerificarRequest() {}

    public OtpVerificarRequest(String contacto, String codigo) {
        this.contacto = contacto;
        this.codigo = codigo;
    }

    // Getters y setters
    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
