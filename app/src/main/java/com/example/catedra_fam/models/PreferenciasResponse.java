package com.example.catedra_fam.models;

/**
 * Response para preferencias de usuario
 * GET /usuarios/preferencias
 */
public class PreferenciasResponse {
    private boolean success;
    private PreferenciasData data;

    public PreferenciasResponse() {}

    // Getters y setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PreferenciasData getData() {
        return data;
    }

    public void setData(PreferenciasData data) {
        this.data = data;
    }

    /**
     * Estructura anidada de preferencias
     */
    public static class PreferenciasData {
        private PreferenciasNotificaciones notificaciones;

        public PreferenciasData() {}

        public PreferenciasNotificaciones getNotificaciones() {
            return notificaciones;
        }

        public void setNotificaciones(PreferenciasNotificaciones notificaciones) {
            this.notificaciones = notificaciones;
        }
    }

    /**
     * Preferencias específicas de notificaciones
     */
    public static class PreferenciasNotificaciones {
        private boolean nuevasTareas;
        private boolean calificaciones;
        private boolean recordatorios;
        private boolean eventos;
        private boolean general;

        public PreferenciasNotificaciones() {}

        // Getters y setters
        public boolean isNuevasTareas() {
            return nuevasTareas;
        }

        public void setNuevasTareas(boolean nuevasTareas) {
            this.nuevasTareas = nuevasTareas;
        }

        public boolean isCalificaciones() {
            return calificaciones;
        }

        public void setCalificaciones(boolean calificaciones) {
            this.calificaciones = calificaciones;
        }

        public boolean isRecordatorios() {
            return recordatorios;
        }

        public void setRecordatorios(boolean recordatorios) {
            this.recordatorios = recordatorios;
        }

        public boolean isEventos() {
            return eventos;
        }

        public void setEventos(boolean eventos) {
            this.eventos = eventos;
        }

        public boolean isGeneral() {
            return general;
        }

        public void setGeneral(boolean general) {
            this.general = general;
        }
    }
}
