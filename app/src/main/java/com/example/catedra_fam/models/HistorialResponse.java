package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo para la respuesta del historial del backend
 * Estructura específica que devuelve el endpoint de historial
 */
public class HistorialResponse {
    @SerializedName("periodo")
    private Periodo periodo;

    @SerializedName("entregas")
    private List<Entrega> entregas;

    @SerializedName("estadisticas")
    private EstadisticasHistorial estadisticas;

    // Getters y Setters
    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public List<Entrega> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<Entrega> entregas) {
        this.entregas = entregas;
    }

    public EstadisticasHistorial getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(EstadisticasHistorial estadisticas) {
        this.estadisticas = estadisticas;
    }

    // Clase interna Periodo
    public static class Periodo {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("fechaInicio")
        private String fechaInicio;

        @SerializedName("fechaFin")
        private String fechaFin;

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

        public String getFechaInicio() {
            return fechaInicio;
        }

        public void setFechaInicio(String fechaInicio) {
            this.fechaInicio = fechaInicio;
        }

        public String getFechaFin() {
            return fechaFin;
        }

        public void setFechaFin(String fechaFin) {
            this.fechaFin = fechaFin;
        }
    }

    // Clase interna EstadisticasHistorial
    public static class EstadisticasHistorial {
        @SerializedName("totalTareas")
        private int totalTareas;

        @SerializedName("tareasCompletadas")
        private int tareasCompletadas;

        @SerializedName("porcentajeCumplimiento")
        private int porcentajeCumplimiento;

        @SerializedName("promedio")
        private double promedio;

        @SerializedName("escala")
        private String escala;

        // Getters y Setters
        public int getTotalTareas() {
            return totalTareas;
        }

        public void setTotalTareas(int totalTareas) {
            this.totalTareas = totalTareas;
        }

        public int getTareasCompletadas() {
            return tareasCompletadas;
        }

        public void setTareasCompletadas(int tareasCompletadas) {
            this.tareasCompletadas = tareasCompletadas;
        }

        public int getPorcentajeCumplimiento() {
            return porcentajeCumplimiento;
        }

        public void setPorcentajeCumplimiento(int porcentajeCumplimiento) {
            this.porcentajeCumplimiento = porcentajeCumplimiento;
        }

        public double getPromedio() {
            return promedio;
        }

        public void setPromedio(double promedio) {
            this.promedio = promedio;
        }

        public String getEscala() {
            return escala;
        }

        public void setEscala(String escala) {
            this.escala = escala;
        }
    }
}



