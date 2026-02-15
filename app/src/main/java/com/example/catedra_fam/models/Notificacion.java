package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de Notificación
 * RF-MO-015, RF-MO-016
 */
public class Notificacion {
    @SerializedName("id")
    private int id;

    @SerializedName("tipo")
    private String tipo; // nueva_tarea, calificacion, recordatorio

    @SerializedName("asunto")
    private String asunto;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("estado")
    private String estado; // enviada, leida

    @SerializedName("enviadoEn")
    private String enviadoEn;

    @SerializedName("leidoEn")
    private String leidoEn;

    @SerializedName("asignacionId")
    private Integer asignacionId; // Puede ser null

    @SerializedName("metadatos")
    private Metadatos metadatos;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEnviadoEn() {
        return enviadoEn;
    }

    public void setEnviadoEn(String enviadoEn) {
        this.enviadoEn = enviadoEn;
    }

    public String getLeidoEn() {
        return leidoEn;
    }

    public void setLeidoEn(String leidoEn) {
        this.leidoEn = leidoEn;
    }

    public Integer getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(Integer asignacionId) {
        this.asignacionId = asignacionId;
    }

    public Metadatos getMetadatos() {
        return metadatos;
    }

    public void setMetadatos(Metadatos metadatos) {
        this.metadatos = metadatos;
    }

    public boolean isLeida() {
        return leidoEn != null;
    }

    public void setLeida(boolean leida) {
        if (leida && leidoEn == null) {
            // Marcar como leída ahora
            this.leidoEn = java.time.LocalDateTime.now().toString();
            this.estado = "leida";
        } else if (!leida) {
            this.leidoEn = null;
            this.estado = "enviada";
        }
    }

    public String getAccion() {
        // Generar acción según tipo
        switch (tipo) {
            case "nueva_tarea":
                return "VER_TAREA";
            case "calificacion":
                return "VER_NOTA";
            case "recordatorio":
                return "ENTREGAR";
            case "vencimiento":
                return "VER_TAREA";
            default:
                return "VER";
        }
    }

    public String getReferenciaId() {
        return asignacionId != null ? String.valueOf(asignacionId) : null;
    }

    // Métodos helper para UI
    public String getIcono() {
        switch (tipo) {
            case "nueva_tarea":
                return "📋";
            case "calificacion":
                return "⭐";
            case "recordatorio":
                return "⏰";
            case "vencimiento":
                return "⚠️";
            default:
                return "🔔";
        }
    }

    public String getTitulo() {
        if (asunto != null && !asunto.isEmpty()) {
            return asunto;
        }

        // Generar título según tipo si no hay asunto
        switch (tipo) {
            case "nueva_tarea":
                return "Nueva tarea asignada";
            case "calificacion":
                return "Tarea calificada";
            case "recordatorio":
                return "Recordatorio de tarea";
            case "vencimiento":
                return "Tarea próxima a vencer";
            default:
                return "Notificación";
        }
    }

    public String getTiempo() {
        if (enviadoEn == null) return "";

        // Aquí deberías implementar la lógica para calcular "hace X tiempo"
        // Por ahora retorno el enviadoEn directo
        // En producción, usa una librería como TimeAgo o implementa tu lógica
        return formatearTiempoRelativo(enviadoEn);
    }

    public String getTextoAccion() {
        switch (tipo) {
            case "nueva_tarea":
                return "Ver tarea";
            case "calificacion":
                return "Ver nota";
            case "recordatorio":
                return "Entregar";
            case "vencimiento":
                return "Ver detalle";
            default:
                return "Ver";
        }
    }

    private String formatearTiempoRelativo(String fechaISO) {
        // ✅ IMPLEMENTACIÓN REAL - Cálculo de tiempo relativo
        try {
            if (fechaISO == null || fechaISO.isEmpty()) {
                return "Reciente";
            }

            // Parser para formato ISO del backend: "2026-02-14T15:30:00.000-05:00"
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.getDefault());
            java.util.Date fechaNotificacion = parser.parse(fechaISO);

            if (fechaNotificacion == null) {
                return "Reciente";
            }

            long tiempoActual = System.currentTimeMillis();
            long tiempoNotificacion = fechaNotificacion.getTime();
            long diferenciaMilis = tiempoActual - tiempoNotificacion;

            // Convertir a diferentes unidades de tiempo
            long segundos = diferenciaMilis / 1000;
            long minutos = segundos / 60;
            long horas = minutos / 60;
            long dias = horas / 24;

            // Formatear según el tiempo transcurrido
            if (segundos < 60) {
                return segundos <= 5 ? "Ahora mismo" : "Hace " + segundos + " seg";
            } else if (minutos < 60) {
                return "Hace " + minutos + " min";
            } else if (horas < 24) {
                return "Hace " + horas + " h";
            } else if (dias < 7) {
                return "Hace " + dias + (dias == 1 ? " día" : " días");
            } else {
                // Para fechas muy antiguas, mostrar fecha formateada
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault());
                return formatter.format(fechaNotificacion);
            }
        } catch (Exception e) {
            android.util.Log.e("Notificacion", "Error formateando tiempo relativo: " + e.getMessage());
            return "Reciente";
        }
    }

    // Clase interna Metadatos
    public static class Metadatos {
        @SerializedName("estudianteNombre")
        private String estudianteNombre;

        @SerializedName("tareaTitulo")
        private String tareaTitulo;

        @SerializedName("fechaVencimiento")
        private String fechaVencimiento;

        @SerializedName("nota")
        private Double nota;

        @SerializedName("prioridad")
        private String prioridad; // alta, media, baja

        // Getters y Setters
        public String getEstudianteNombre() {
            return estudianteNombre;
        }

        public void setEstudianteNombre(String estudianteNombre) {
            this.estudianteNombre = estudianteNombre;
        }

        public String getTareaTitulo() {
            return tareaTitulo;
        }

        public void setTareaTitulo(String tareaTitulo) {
            this.tareaTitulo = tareaTitulo;
        }

        public String getFechaVencimiento() {
            return fechaVencimiento;
        }

        public void setFechaVencimiento(String fechaVencimiento) {
            this.fechaVencimiento = fechaVencimiento;
        }

        public Double getNota() {
            return nota;
        }

        public void setNota(Double nota) {
            this.nota = nota;
        }

        public String getPrioridad() {
            return prioridad;
        }

        public void setPrioridad(String prioridad) {
            this.prioridad = prioridad;
        }
    }
}

