package com.example.catedra_fam.models;

public class Notificacion {
    private String id;
    private String tipo; // nueva_tarea, recordatorio, calificada, anuncio
    private String titulo;
    private String mensaje;
    private String tiempo;
    private boolean leida;
    private String accion; // VER_TAREA, ENTREGAR, VER_NOTA
    private String referenciaId; // ID de la tarea relacionada

    public Notificacion() {}

    public Notificacion(String id, String tipo, String titulo, String mensaje,
                        String tiempo, boolean leida, String accion, String referenciaId) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tiempo = tiempo;
        this.leida = leida;
        this.accion = accion;
        this.referenciaId = referenciaId;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getReferenciaId() { return referenciaId; }
    public void setReferenciaId(String referenciaId) { this.referenciaId = referenciaId; }

    // Helpers para obtener icono según tipo
    public String getIcono() {
        switch (tipo) {
            case "nueva_tarea":
                return "🔔";
            case "recordatorio":
                return "⚠️";
            case "calificada":
                return "✅";
            case "anuncio":
                return "📢";
            default:
                return "🔔";
        }
    }

    // Helper para texto del botón
    public String getTextoAccion() {
        switch (accion) {
            case "VER_TAREA":
                return "VER TAREA";
            case "ENTREGAR":
                return "ENTREGAR";
            case "VER_NOTA":
                return "VER NOTA";
            default:
                return "VER";
        }
    }
}

