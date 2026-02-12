package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de Estudiante (Hijo)
 * RF-MO-012, RF-MO-013
 */
public class Estudiante {
    @SerializedName("id")
    private int id;

    @SerializedName("firstName")
    private String nombres;

    @SerializedName("lastName")
    private String apellidos;

    @SerializedName("documento")
    private String numeroDocumento;

    @SerializedName("curso")
    private Curso curso;

    @SerializedName("tareasPendientes")
    private int tareasPendientes;

    @SerializedName("tareasCompletadas")
    private int tareasCompletadas;

    @SerializedName("tareasVencidas")
    private int tareasVencidas;

    @SerializedName("promedioGeneral")
    private double promedioGeneral;

    @SerializedName("proximaTarea")
    private ProximaTarea proximaTarea;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public int getTareasPendientes() {
        return tareasPendientes;
    }

    public void setTareasPendientes(int tareasPendientes) {
        this.tareasPendientes = tareasPendientes;
    }

    public int getTareasCompletadas() {
        return tareasCompletadas;
    }

    public void setTareasCompletadas(int tareasCompletadas) {
        this.tareasCompletadas = tareasCompletadas;
    }

    public int getTareasVencidas() {
        return tareasVencidas;
    }

    public void setTareasVencidas(int tareasVencidas) {
        this.tareasVencidas = tareasVencidas;
    }

    public double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public ProximaTarea getProximaTarea() {
        return proximaTarea;
    }

    public void setProximaTarea(ProximaTarea proximaTarea) {
        this.proximaTarea = proximaTarea;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // Clases internas
    public static class Curso {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String nombre;

        @SerializedName("grado")
        private String grado;

        @SerializedName("jornada")
        private String jornada;

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

        public String getGrado() {
            return grado;
        }

        public void setGrado(String grado) {
            this.grado = grado;
        }

        public String getJornada() {
            return jornada;
        }

        public void setJornada(String jornada) {
            this.jornada = jornada;
        }
    }

    public static class ProximaTarea {
        @SerializedName("titulo")
        private String titulo;

        @SerializedName("fechaVencimiento")
        private String fechaVencimiento;

        // Getters y Setters
        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getFechaVencimiento() {
            return fechaVencimiento;
        }

        public void setFechaVencimiento(String fechaVencimiento) {
            this.fechaVencimiento = fechaVencimiento;
        }
    }
}

