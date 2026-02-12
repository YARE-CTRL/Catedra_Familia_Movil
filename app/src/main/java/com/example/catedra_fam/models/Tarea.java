package com.example.catedra_fam.models;

public class Tarea {
    private int id;
    private String titulo;
    private String descripcion;
    private String frecuencia; // semanal, quincenal, mensual, unica
    private String fechaPublicacion;
    private String fechaVencimiento;
    private int diasRestantes;
    private String estado; // pendiente, completada, vencida, calificada, próximo_vencimiento
    private boolean incluyeEnBoletin;
    private String nota; // si está calificada
    private String feedback; // retroalimentación del docente
    private String categoria; // nombre de la categoría
    private String colorCategoria; // color de la categoría

    // Constructor vacío
    public Tarea() {}

    // Constructor con parámetros principales
    public Tarea(int id, String titulo, String descripcion, String fechaVencimiento, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isIncluyeEnBoletin() {
        return incluyeEnBoletin;
    }

    public void setIncluyeEnBoletin(boolean incluyeEnBoletin) {
        this.incluyeEnBoletin = incluyeEnBoletin;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getColorCategoria() {
        return colorCategoria;
    }

    public void setColorCategoria(String colorCategoria) {
        this.colorCategoria = colorCategoria;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(int diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    // Helper para verificar si está vencida
    public boolean isVencida() {
        return "vencida".equals(estado);
    }

    // Helper para verificar si está completada
    public boolean isCompletada() {
        return "completada".equals(estado) || "calificada".equals(estado);
    }

    // Helper para verificar si está calificada
    public boolean isCalificada() {
        return "calificada".equals(estado);
    }
}

