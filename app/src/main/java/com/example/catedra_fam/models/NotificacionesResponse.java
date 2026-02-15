package com.example.catedra_fam.models;

import java.util.List;

/**
 * Response para listar notificaciones con paginación y metadata
 * GET /movil/notificaciones
 */
public class NotificacionesResponse {
    private boolean success;
    private List<Notificacion> data;
    private NotificacionesMeta meta;

    public NotificacionesResponse() {}

    // Getters y setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Notificacion> getData() {
        return data;
    }

    public void setData(List<Notificacion> data) {
        this.data = data;
    }

    public NotificacionesMeta getMeta() {
        return meta;
    }

    public void setMeta(NotificacionesMeta meta) {
        this.meta = meta;
    }

    /**
     * Metadata de paginación y contadores
     */
    public static class NotificacionesMeta {
        private int total;
        private int noLeidas;
        private int page;
        private int limit;
        private List<String> tipos;

        public NotificacionesMeta() {}

        // Getters y setters
        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getNoLeidas() {
            return noLeidas;
        }

        public void setNoLeidas(int noLeidas) {
            this.noLeidas = noLeidas;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public List<String> getTipos() {
            return tipos;
        }

        public void setTipos(List<String> tipos) {
            this.tipos = tipos;
        }
    }
}
