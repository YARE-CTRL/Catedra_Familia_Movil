package com.example.catedra_fam.api;

import com.google.gson.annotations.SerializedName;

public class Curso {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("grado")
    private String grado;

    @SerializedName("jornada")
    private String jornada;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }
}
