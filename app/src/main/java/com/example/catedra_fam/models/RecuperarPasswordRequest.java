package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request para recuperar contraseña
 * RF-MO-003
 */
public class RecuperarPasswordRequest {
    @SerializedName("documento")
    private String documento;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("passwordNuevo")
    private String passwordNuevo;

    public RecuperarPasswordRequest(String documento, String codigo, String passwordNuevo) {
        this.documento = documento;
        this.codigo = codigo;
        this.passwordNuevo = passwordNuevo;
    }

    // Getters y Setters
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPasswordNuevo() {
        return passwordNuevo;
    }

    public void setPasswordNuevo(String passwordNuevo) {
        this.passwordNuevo = passwordNuevo;
    }
}
