package com.example.catedra_fam.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "estudiantes",
        foreignKeys = @ForeignKey(
                entity = CursoEntity.class,
                parentColumns = "id",
                childColumns = "curso_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("curso_id")})
public class EstudianteEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "nombres")
    private String nombres;

    @ColumnInfo(name = "apellidos")
    private String apellidos;

    @ColumnInfo(name = "tipo_documento")
    private String tipoDocumento;

    @ColumnInfo(name = "numero_documento")
    private String numeroDocumento;

    @ColumnInfo(name = "fecha_nacimiento")
    private String fechaNacimiento;

    @ColumnInfo(name = "sexo")
    private String sexo;

    @ColumnInfo(name = "curso_id")
    private int cursoId;

    @ColumnInfo(name = "curso_nombre")
    private String cursoNombre; // Desnormalizado para facilitar queries

    @ColumnInfo(name = "acudiente_id")
    private int acudienteId; // Referencia al acudiente principal

    // Constructor vacío
    public EstudianteEntity() {
    }

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

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public int getAcudienteId() {
        return acudienteId;
    }

    public void setAcudienteId(int acudienteId) {
        this.acudienteId = acudienteId;
    }
}

