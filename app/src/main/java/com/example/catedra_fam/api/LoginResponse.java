package com.example.catedra_fam.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    @SerializedName("estudiantes")
    private List<EstudianteLogin> estudiantes;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<EstudianteLogin> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<EstudianteLogin> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
