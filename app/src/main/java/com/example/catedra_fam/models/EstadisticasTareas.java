package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * ✅ MODELO ACTUALIZADO (13/Feb/2026): Para el endpoint GET /movil/estudiantes/:id/estadisticas
 * Estructura del backend:
 * {
 *   "data": {
 *     "periodo": { "id": 1, "nombre": "Primer Periodo" },
 *     "estudianteId": 42,
 *     "estadisticas": {
 *       "pendientes": 5,
 *       "vencidas": 2,
 *       "completadas": 10,
 *       "entregadas": 6,
 *       "calificadas": 4
 *     },
 *     "total": {
 *       "porAtender": 7,
 *       "completadas": 10
 *     }
 *   }
 * }
 */
public class EstadisticasTareas {

    @SerializedName("periodo")
    private Periodo periodo;

    @SerializedName("estudianteId")
    private int estudianteId;

    @SerializedName("estadisticas")
    private EstadisticasDetalle estadisticas;

    @SerializedName("total")
    private TotalEstadisticas total;

    // Getters y Setters
    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public EstadisticasDetalle getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(EstadisticasDetalle estadisticas) {
        this.estadisticas = estadisticas;
    }

    public TotalEstadisticas getTotal() {
        return total;
    }

    public void setTotal(TotalEstadisticas total) {
        this.total = total;
    }

    /**
     * Clase interna para el objeto "estadisticas"
     */
    public static class EstadisticasDetalle {
        @SerializedName("pendientes")
        private int pendientes;

        @SerializedName("vencidas")
        private int vencidas;

        @SerializedName("completadas")
        private int completadas;

        @SerializedName("entregadas")
        private int entregadas;

        @SerializedName("calificadas")
        private int calificadas;

        // Getters y Setters
        public int getPendientes() {
            return pendientes;
        }

        public void setPendientes(int pendientes) {
            this.pendientes = pendientes;
        }

        public int getVencidas() {
            return vencidas;
        }

        public void setVencidas(int vencidas) {
            this.vencidas = vencidas;
        }

        public int getCompletadas() {
            return completadas;
        }

        public void setCompletadas(int completadas) {
            this.completadas = completadas;
        }

        public int getEntregadas() {
            return entregadas;
        }

        public void setEntregadas(int entregadas) {
            this.entregadas = entregadas;
        }

        public int getCalificadas() {
            return calificadas;
        }

        public void setCalificadas(int calificadas) {
            this.calificadas = calificadas;
        }
    }

    /**
     * Clase interna para el objeto "total"
     */
    public static class TotalEstadisticas {
        @SerializedName("porAtender")
        private int porAtender;

        @SerializedName("completadas")
        private int completadas;

        // Getters y Setters
        public int getPorAtender() {
            return porAtender;
        }

        public void setPorAtender(int porAtender) {
            this.porAtender = porAtender;
        }

        public int getCompletadas() {
            return completadas;
        }

        public void setCompletadas(int completadas) {
            this.completadas = completadas;
        }
    }

    /**
     * ✅ CLASE NUEVA: Para el objeto "periodo"
     */
    public static class Periodo {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

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
    }
}
