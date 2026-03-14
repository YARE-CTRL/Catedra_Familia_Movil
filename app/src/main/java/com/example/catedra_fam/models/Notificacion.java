package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Modelo de Notificación
 * RF-MO-015, RF-MO-016
 * ✅ ACTUALIZADO: Campos alineados con backend (16 Feb 2026)
 * Backend usa: titulo, cuerpo, datos, leida
 */
public class Notificacion {
    @SerializedName("id")
    private int id;

    @SerializedName("tipo")
    private String tipo; // tarea, evento, recordatorio, general, urgente

    /**
     * ✅ ACTUALIZADO: Backend usa "titulo" (antes era "asunto")
     */
    @SerializedName("titulo")
    private String titulo;

    /**
     * ✅ ACTUALIZADO: Backend usa "cuerpo" pero mapeamos a "mensaje" local
     * Esto permite usar getMensaje() en el código existente
     */
    @SerializedName("cuerpo")
    private String mensaje;

    /**
     * ✅ NUEVO: Campo "datos" JSON del backend
     * Contiene metadata como: {"tipo": "nueva_tarea", "id": 42}
     */
    @SerializedName("datos")
    private Map<String, Object> datos;

    /**
     * ✅ ACTUALIZADO: Backend usa "leida" (boolean) en lugar de "estado"
     */
    @SerializedName("leida")
    private boolean leida;

    /**
     * ✅ ACTUALIZADO: Backend usa "leidaEn" (snake_case se convierte a camelCase)
     */
    @SerializedName("leidaEn")
    private String leidaEn;

    /**
     * ✅ NUEVO: Backend incluye "creadaEn" (timestamp de creación)
     */
    @SerializedName("creadaEn")
    private String creadaEn;

    // CAMPOS LEGACY - Mantener para compatibilidad con código existente
    @SerializedName("asignacionId")
    private Integer asignacionId; // Puede extraerse de "datos" si existe

    @SerializedName("metadatos")
    private Metadatos metadatos; // Puede ser null

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Alias para compatibilidad con código existente que usa getAsunto()
     */
    public String getAsunto() {
        return titulo;
    }

    public void setAsunto(String asunto) {
        this.titulo = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Map<String, Object> getDatos() {
        return datos;
    }

    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    /**
     * Alias para compatibilidad con código que usa getEstado()
     * @return "leida" o "enviada" basado en el campo leida
     */
    public String getEstado() {
        return leida ? "leida" : "enviada";
    }

    public void setEstado(String estado) {
        this.leida = "leida".equals(estado);
    }

    public String getLeidaEn() {
        return leidaEn;
    }

    public void setLeidaEn(String leidaEn) {
        this.leidaEn = leidaEn;
    }

    /**
     * Alias para compatibilidad
     */
    public String getLeidoEn() {
        return leidaEn;
    }

    public void setLeidoEn(String leidoEn) {
        this.leidaEn = leidoEn;
    }

    public String getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(String creadaEn) {
        this.creadaEn = creadaEn;
    }

    /**
     * Alias para compatibilidad - obtiene timestamp de envío
     */
    public String getEnviadoEn() {
        return creadaEn;
    }

    public void setEnviadoEn(String enviadoEn) {
        this.creadaEn = enviadoEn;
    }

    public Integer getAsignacionId() {
        // Intentar extraer de datos si no está definido directamente
        if (asignacionId == null && datos != null) {
            Object id = datos.get("id");
            if (id instanceof Number) {
                asignacionId = ((Number) id).intValue();
            }
        }
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

    // Métodos helper para UI
    public String getAccion() {
        // Generar acción según tipo
        switch (tipo) {
            case "nueva_tarea":
            case "tarea":  // ✅ Backend usa "tarea"
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

    public String getIcono() {
        switch (tipo) {
            case "nueva_tarea":
            case "tarea":  // ✅ Backend usa "tarea"
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

    public String getTiempo() {
        if (creadaEn == null) return "";
        return formatearTiempoRelativo(creadaEn);
    }

    public String getTextoAccion() {
        switch (tipo) {
            case "nueva_tarea":
            case "tarea":
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

