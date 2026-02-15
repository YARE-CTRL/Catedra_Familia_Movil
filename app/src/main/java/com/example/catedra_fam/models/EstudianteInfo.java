package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 🔴 CAMBIO BREAKING (13/Feb/2026): Backend SIEMPRE retorna array
 * Estructura actual del backend:
 * SIEMPRE → { "data": { "estudiantes": [...] } }
 * El campo "estudiante" (singular) YA NO EXISTE
 */
public class EstudianteInfo {

    @SerializedName("estudiantes")
    private List<EstudianteDetalle> estudiantes;

    // ❌ CAMPO REMOVIDO - El backend ya no envía "estudiante" singular
    // @SerializedName("estudiante")
    // private EstudianteDetalle estudiante;

    // Getters y Setters
    public List<EstudianteDetalle> getEstudiantes() {
        return estudiantes != null ? estudiantes : new java.util.ArrayList<>();
    }

    public void setEstudiantes(List<EstudianteDetalle> estudiantes) {
        this.estudiantes = estudiantes;
    }

    /**
     * ✅ MÉTODO ACTUALIZADO: Ahora simplemente retorna la lista
     * ya que el backend siempre envía array
     */
    public List<EstudianteDetalle> getTodosLosEstudiantes() {
        return getEstudiantes();
    }

    /**
     * ✅ MÉTODO NUEVO: Para obtener el primer estudiante cuando hay solo uno
     */
    public EstudianteDetalle getPrimerEstudiante() {
        List<EstudianteDetalle> lista = getEstudiantes();
        return lista.isEmpty() ? null : lista.get(0);
    }

    // ...existing code...

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
