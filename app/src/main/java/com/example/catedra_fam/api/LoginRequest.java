package com.example.catedra_fam.api;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("documento")
    private String documento;

    @SerializedName("password")
    private String password;

    @SerializedName("tokenFCM")
    private String tokenFCM;

    public LoginRequest(String documento, String password) {
        this.documento = documento;
        this.password = password;
        this.tokenFCM = null; // Optional, can be set later
    }

    public LoginRequest(String documento, String password, String tokenFCM) {
        this.documento = documento;
        this.password = password;
        this.tokenFCM = tokenFCM;
    }

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
