package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo de Entrega (Evidencia de tarea)
 * RF-MO-007, RF-MO-008, RF-MO-010
 */
public class Entrega {
    @SerializedName("id")
    private int id;

    @SerializedName("asignacionId")
    private int asignacionId;

    @SerializedName("estudianteId")
    private int estudianteId;

    @SerializedName("acudienteId")
    private int acudienteId;

    @SerializedName("evidenciaTexto")
    private String evidenciaTexto;

    @SerializedName("archivosUrl")
    private List<String> archivosUrl;

    @SerializedName("fechaEntrega")
    private String fechaEntrega;

    @SerializedName("estado")
    private String estado; // enviada, calificada

    @SerializedName("nombreEnvio")
    private String nombreEnvio;

    @SerializedName("asignacion")
    private AsignacionResumen asignacion;

    @SerializedName("calificacion")
    private CalificacionResumen calificacion;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(int asignacionId) {
        this.asignacionId = asignacionId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getAcudienteId() {
        return acudienteId;
    }

    public void setAcudienteId(int acudienteId) {
        this.acudienteId = acudienteId;
    }

    public String getEvidenciaTexto() {
        return evidenciaTexto;
    }

    public void setEvidenciaTexto(String evidenciaTexto) {
        this.evidenciaTexto = evidenciaTexto;
    }

    public List<String> getArchivosUrl() {
        return archivosUrl;
    }

    public void setArchivosUrl(List<String> archivosUrl) {
        this.archivosUrl = archivosUrl;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreEnvio() {
        return nombreEnvio;
    }

    public void setNombreEnvio(String nombreEnvio) {
        this.nombreEnvio = nombreEnvio;
    }

    public AsignacionResumen getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(AsignacionResumen asignacion) {
        this.asignacion = asignacion;
    }

    public CalificacionResumen getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(CalificacionResumen calificacion) {
        this.calificacion = calificacion;
    }

    // Métodos helper para acceso rápido
    public String getTareaTitulo() {
        return asignacion != null ? asignacion.getTitulo() : "";
    }

    public Double getNota() {
        return calificacion != null ? calificacion.getNota() : null;
    }

    public String getNotaCualitativa() {
        return calificacion != null ? calificacion.getEscala() : "";
    }

    public String getFeedback() {
        return calificacion != null ? calificacion.getRetroalimentacion() : null;
    }

    public boolean isCalificada() {
        return calificacion != null && calificacion.getNota() > 0;
    }

    // Compatibilidad para TareaDetalleActivity
    public String getDescripcion() {
        return evidenciaTexto;
    }
    public List<String> getArchivos() {
        return archivosUrl;
    }
    public String getFechaEntregaPublic() {
        return fechaEntrega;
    }

    // Clases internas
    public static class AsignacionResumen {
        @SerializedName("id")
        private int id;

        @SerializedName("titulo")
        private String titulo;

        @SerializedName("frecuencia")
        private String frecuencia;

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

        public String getFrecuencia() {
            return frecuencia;
        }

        public void setFrecuencia(String frecuencia) {
            this.frecuencia = frecuencia;
        }
    }

    public static class CalificacionResumen {
        @SerializedName("nota")
        private double nota;

        @SerializedName("escala")
        private String escala;

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

