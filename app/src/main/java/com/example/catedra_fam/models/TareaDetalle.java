package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo para detalle completo de tarea según especificaciones del backend
 * GET /asignaciones/{id}/detalle
 */
public class TareaDetalle {
    @SerializedName("id")
    private int id;

    @SerializedName("tarea")
    private TareaInfo tarea;

    @SerializedName("fechaPublicacion")
    private String fechaPublicacion;

    @SerializedName("fechaVencimiento")
    private String fechaVencimiento;

    @SerializedName("diasRestantes")
    private int diasRestantes;

    @SerializedName("frecuencia")
    private String frecuencia;

    @SerializedName("estado")
    private String estado;

    @SerializedName("entrega")
    private Entrega entrega; // Puede ser null

    @SerializedName("calificacion")
    private Calificacion calificacion; // Puede ser null

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TareaInfo getTarea() {
        return tarea;
    }

    public void setTarea(TareaInfo tarea) {
        this.tarea = tarea;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(int diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
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

    // Clase interna para información de la tarea
    public static class TareaInfo {
        @SerializedName("id")
        private int id;

        @SerializedName("titulo")
        private String titulo;

        @SerializedName("descripcion")
        private String descripcion;

        @SerializedName("categoria")
        private String categoria;

        @SerializedName("tipoCalificacion")
        private String tipoCalificacion;

        @SerializedName("archivosAdjuntos")
        private List<ArchivoAdjunto> archivosAdjuntos;

        @SerializedName("enlaces")
        private List<Enlace> enlaces;

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

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getTipoCalificacion() {
            return tipoCalificacion;
        }

        public void setTipoCalificacion(String tipoCalificacion) {
            this.tipoCalificacion = tipoCalificacion;
        }

        public List<ArchivoAdjunto> getArchivosAdjuntos() {
            return archivosAdjuntos;
        }

        public void setArchivosAdjuntos(List<ArchivoAdjunto> archivosAdjuntos) {
            this.archivosAdjuntos = archivosAdjuntos;
        }

        public List<Enlace> getEnlaces() {
            return enlaces;
        }

        public void setEnlaces(List<Enlace> enlaces) {
            this.enlaces = enlaces;
        }
    }

    // Clases auxiliares
    public static class ArchivoAdjunto {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("url")
        private String url;

        @SerializedName("tipo")
        private String tipo;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
    }

    public static class Enlace {
        @SerializedName("id")
        private int id;

        @SerializedName("titulo")
        private String titulo;

        @SerializedName("url")
        private String url;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
