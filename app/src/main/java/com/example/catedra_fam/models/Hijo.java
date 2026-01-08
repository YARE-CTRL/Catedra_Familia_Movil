package com.example.catedra_fam.models;

public class Hijo {
    private int id;
    private String nombres;
    private String apellidos;
    private String cursoNombre;
    private int tareasPendientes;
    private int tareasCompletadas;
    private int tareasCalificadas;
    private String proximaTarea;
    private String fechaProximaTarea;
    private String estado; // al_dia, con_pendientes, con_pendientes_urgentes, con_vencidas

    public Hijo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    // Alias para getCursoNombre
    public String getCurso() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public int getTareasPendientes() {
        return tareasPendientes;
    }

    public void setTareasPendientes(int tareasPendientes) {
        this.tareasPendientes = tareasPendientes;
    }

    public int getTareasCompletadas() {
        return tareasCompletadas;
    }

    public void setTareasCompletadas(int tareasCompletadas) {
        this.tareasCompletadas = tareasCompletadas;
    }

    public int getTareasCalificadas() {
        return tareasCalificadas;
    }

    public void setTareasCalificadas(int tareasCalificadas) {
        this.tareasCalificadas = tareasCalificadas;
    }

    public String getProximaTarea() {
        return proximaTarea;
    }

    public void setProximaTarea(String proximaTarea) {
        this.proximaTarea = proximaTarea;
    }

    public String getFechaProximaTarea() {
        return fechaProximaTarea;
    }

    public void setFechaProximaTarea(String fechaProximaTarea) {
        this.fechaProximaTarea = fechaProximaTarea;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIniciales() {
        String inicial1 = nombres != null && !nombres.isEmpty() ? nombres.substring(0, 1).toUpperCase() : "";
        String inicial2 = apellidos != null && !apellidos.isEmpty() ? apellidos.substring(0, 1).toUpperCase() : "";
        return inicial1 + inicial2;
    }
}

