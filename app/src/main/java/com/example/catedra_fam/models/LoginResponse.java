package com.example.catedra_fam.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Respuesta del login móvil
 * RF-MO-001
 * URL: https://escuelaparapadres-backend-1.onrender.com/api/movil/auth/login/movil
 */
public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    @SerializedName("estudiantes")
    private List<EstudianteLogin> estudiantes;

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<EstudianteLogin> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<EstudianteLogin> estudiantes) {
        this.estudiantes = estudiantes;
    }

    // Para compatibilidad con código existente
    public boolean isDebeCambiarPassword() {
        return user != null && user.isMustChangePassword();
    }

    // Clase interna User (estructura del backend)
    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("documento")
        private String documento;

        @SerializedName("firstName")
        private String firstName;

        @SerializedName("lastName")
        private String lastName;

        @SerializedName("phone")
        private String phone;

        @SerializedName("email")
        private String email;

        @SerializedName("roleId")
        private String roleId;

        @SerializedName("mustChangePassword")
        private boolean mustChangePassword;

        @SerializedName("lastLogin")
        private String lastLogin;

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public boolean isMustChangePassword() {
            return mustChangePassword;
        }

        public void setMustChangePassword(boolean mustChangePassword) {
            this.mustChangePassword = mustChangePassword;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getNombreCompleto() {
            return firstName + " " + lastName;
        }
    }

    // Clase interna EstudianteLogin
    public static class EstudianteLogin {
        @SerializedName("id")
        private int id;

        @SerializedName("firstName")
        private String firstName;

        @SerializedName("lastName")
        private String lastName;

        @SerializedName("documento")
        private String documento;

        @SerializedName("foto")
        private String foto;

        @SerializedName("curso")
        private CursoLogin curso;

        @SerializedName("institucion")
        private InstitucionLogin institucion;

        @SerializedName("parentesco")
        private String parentesco;

        @SerializedName("esPrincipal")
        private boolean esPrincipal;

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getFoto() {
            return foto;
        }

        public void setFoto(String foto) {
            this.foto = foto;
        }

        public CursoLogin getCurso() {
            return curso;
        }

        public void setCurso(CursoLogin curso) {
            this.curso = curso;
        }

        public InstitucionLogin getInstitucion() {
            return institucion;
        }

        public void setInstitucion(InstitucionLogin institucion) {
            this.institucion = institucion;
        }

        public String getParentesco() {
            return parentesco;
        }

        public void setParentesco(String parentesco) {
            this.parentesco = parentesco;
        }

        public boolean isEsPrincipal() {
            return esPrincipal;
        }

        public void setEsPrincipal(boolean esPrincipal) {
            this.esPrincipal = esPrincipal;
        }

        public String getNombreCompleto() {
            return firstName + " " + lastName;
        }
    }

    // Clase interna CursoLogin
    public static class CursoLogin {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("grado")
        private String grado;

        @SerializedName("jornada")
        private String jornada;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

    // Clase interna InstitucionLogin
    public static class InstitucionLogin {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

