package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * ✅ NUEVO (15/Feb/2026): Modelo para el objeto meta del endpoint GET /movil/estudiantes/:id/tareas
 *
 * Estructura del backend:
 * {
 *   "success": true,
 *   "data": [...],
 *   "meta": {
 *     "periodo": { "id": 1, "nombre": "2026" },
 *     "total": 5,
 *     "pendientes": 0,
 *     "vencidas": 4,     // ✅ NUEVO: Agregado por backend
 *     "entregadas": 0,
 *     "calificadas": 1,
 *     "ultimaSincronizacion": "2026-02-16T00:07:28.737+00:00"
 *   }
 * }
 */
public class TareasMeta {

    @SerializedName("periodo")
    private Periodo periodo;

    @SerializedName("total")
    private int total;

    @SerializedName("pendientes")
    private int pendientes;

    @SerializedName("vencidas")
    private int vencidas; // ✅ NUEVO: Contador de tareas vencidas

    @SerializedName("entregadas")
    private int entregadas;

    @SerializedName("calificadas")
    private int calificadas;

    @SerializedName("ultimaSincronizacion")
    private String ultimaSincronizacion;

    // Constructor vacío
    public TareasMeta() {
    }

    // Getters y Setters
    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

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

    public String getUltimaSincronizacion() {
        return ultimaSincronizacion;
    }

    public void setUltimaSincronizacion(String ultimaSincronizacion) {
        this.ultimaSincronizacion = ultimaSincronizacion;
    }

    /**
     * Calcula el total de tareas "por atender" (pendientes + vencidas)
     */
    public int getPorAtender() {
        return pendientes + vencidas;
    }

    @Override
    public String toString() {
        return "TareasMeta{" +
                "periodo=" + (periodo != null ? periodo.getNombre() : "null") +
                ", total=" + total +
                ", pendientes=" + pendientes +
                ", vencidas=" + vencidas +
                ", entregadas=" + entregadas +
                ", calificadas=" + calificadas +
                ", porAtender=" + getPorAtender() +
                '}';
    }

    /**
     * Clase interna para el periodo académico
     */
    public static class Periodo {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        public Periodo() {
        }

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

