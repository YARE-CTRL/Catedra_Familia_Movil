package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para la respuesta de datos del estudiante
 * GET /api/movil/estudiantes
 */
public class EstudianteInfo {
    @SerializedName("estudiante")
    private EstudianteDetalle estudiante;

    // Getters y Setters
    public EstudianteDetalle getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(EstudianteDetalle estudiante) {
        this.estudiante = estudiante;
    }

    // Clase interna para el detalle del estudiante
    public static class EstudianteDetalle {
        @SerializedName("id")
        private int id;

        @SerializedName("nombres")
        private String nombres;

        @SerializedName("apellidos")
        private String apellidos;

        @SerializedName("documento")
        private String documento;

        @SerializedName("fechaNacimiento")
        private String fechaNacimiento;

        @SerializedName("grado")
        private String grado;

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

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(String fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getGrado() {
            return grado;
        }

        public void setGrado(String grado) {
            this.grado = grado;
        }

        // Método de conveniencia para nombre completo
        public String getNombreCompleto() {
            return nombres + " " + apellidos;
        }
    }
}
