package com.example.catedra_fam.models;

public class Entrega {
    private String id;
    private String tareaId;
    private String tareaTitulo;
    private String fechaEntrega;
    private String estado; // enviada, en_revision, calificada
    private String evidenciaTexto;
    private float nota;
    private String notaCualitativa; // Superior, Alto, Básico, Bajo
    private String feedback;
    private String calificadoPor;
    private String calificadoEn;

    public Entrega() {}

    public Entrega(String id, String tareaId, String tareaTitulo, String fechaEntrega,
                   String estado, String evidenciaTexto, float nota, String notaCualitativa,
                   String feedback, String calificadoPor, String calificadoEn) {
        this.id = id;
        this.tareaId = tareaId;
        this.tareaTitulo = tareaTitulo;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.evidenciaTexto = evidenciaTexto;
        this.nota = nota;
        this.notaCualitativa = notaCualitativa;
        this.feedback = feedback;
        this.calificadoPor = calificadoPor;
        this.calificadoEn = calificadoEn;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTareaId() { return tareaId; }
    public void setTareaId(String tareaId) { this.tareaId = tareaId; }

    public String getTareaTitulo() { return tareaTitulo; }
    public void setTareaTitulo(String tareaTitulo) { this.tareaTitulo = tareaTitulo; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getEvidenciaTexto() { return evidenciaTexto; }
    public void setEvidenciaTexto(String evidenciaTexto) { this.evidenciaTexto = evidenciaTexto; }

    public float getNota() { return nota; }
    public void setNota(float nota) { this.nota = nota; }

    public String getNotaCualitativa() { return notaCualitativa; }
    public void setNotaCualitativa(String notaCualitativa) { this.notaCualitativa = notaCualitativa; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getCalificadoPor() { return calificadoPor; }
    public void setCalificadoPor(String calificadoPor) { this.calificadoPor = calificadoPor; }

    public String getCalificadoEn() { return calificadoEn; }
    public void setCalificadoEn(String calificadoEn) { this.calificadoEn = calificadoEn; }

    // Helpers
    public boolean isCalificada() {
        return "calificada".equals(estado);
    }

    public boolean isEnRevision() {
        return "en_revision".equals(estado);
    }

    public boolean isEnviada() {
        return "enviada".equals(estado);
    }
}

