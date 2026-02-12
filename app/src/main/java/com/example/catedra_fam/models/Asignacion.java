package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de Asignación (Tarea)
 * RF-MO-005, RF-MO-006
 */
public class Asignacion {
    @SerializedName("id")
    private int id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("frecuencia")
    private String frecuencia; // semanal, quincenal, mensual

    @SerializedName("fechaInicio")
    private String fechaInicio;

    @SerializedName("fechaVencimiento")
    private String fechaVencimiento;

    @SerializedName("incluirEnBoletin")
    private boolean incluirEnBoletin;

    @SerializedName("categoria")
    private Categoria categoria;

    @SerializedName("tema")
    private String tema;

    @SerializedName("estado")
    private String estado; // pendiente, completada, vencida

    @SerializedName("entrega")
    private Entrega entrega; // Puede ser null si no ha entregado

    @SerializedName("calificacion")
    private Calificacion calificacion; // Puede ser null si no está calificada

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

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isIncluirEnBoletin() {
        return incluirEnBoletin;
    }

    public void setIncluirEnBoletin(boolean incluirEnBoletin) {
        this.incluirEnBoletin = incluirEnBoletin;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Calificacion calificacion) {
        this.calificacion = calificacion;
    }

    // Clase interna Categoria
    public static class Categoria {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("color")
        private String color;

        @SerializedName("icono")
        private String icono;

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getIcono() {
            return icono;
        }

        public void setIcono(String icono) {
            this.icono = icono;
        }
    }

    // Clase interna Calificacion
    public static class Calificacion {
        @SerializedName("nota")
        private double nota;

        @SerializedName("escala")
        private String escala; // bajo, basico, alto, superior

        @SerializedName("notaCualitativa")
        private String notaCualitativa;

        @SerializedName("retroalimentacion")
        private String retroalimentacion;

        @SerializedName("calificadoEn")
        private String calificadoEn;

        // Getters y Setters
        public double getNota() {
            return nota;
        }

        public void setNota(double nota) {
            this.nota = nota;
        }

        public String getEscala() {
            return escala;
        }

        public void setEscala(String escala) {
            this.escala = escala;
        }

        public String getNotaCualitativa() {
            return notaCualitativa;
        }

        public void setNotaCualitativa(String notaCualitativa) {
            this.notaCualitativa = notaCualitativa;
        }

        public String getRetroalimentacion() {
            return retroalimentacion;
        }

        public void setRetroalimentacion(String retroalimentacion) {
            this.retroalimentacion = retroalimentacion;
        }

        public String getCalificadoEn() {
            return calificadoEn;
        }

        public void setCalificadoEn(String calificadoEn) {
            this.calificadoEn = calificadoEn;
        }
    }
}

