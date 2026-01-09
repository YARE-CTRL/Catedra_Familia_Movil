package com.example.catedra_fam.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "entregas",
        foreignKeys = {
                @ForeignKey(
                        entity = AsignacionEntity.class,
                        parentColumns = "id",
                        childColumns = "asignacion_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = EstudianteEntity.class,
                        parentColumns = "id",
                        childColumns = "estudiante_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = AcudienteEntity.class,
                        parentColumns = "id",
                        childColumns = "acudiente_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("asignacion_id"), @Index("estudiante_id"), @Index("acudiente_id")})
public class EntregaEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "asignacion_id")
    private int asignacionId;

    @ColumnInfo(name = "estudiante_id")
    private int estudianteId;

    @ColumnInfo(name = "acudiente_id")
    private int acudienteId;

    @ColumnInfo(name = "evidencia_texto")
    private String evidenciaTexto;

    @ColumnInfo(name = "archivos_url")
    private String archivosUrl; // JSON string con URLs

    @ColumnInfo(name = "fecha_entrega")
    private String fechaEntrega;

    @ColumnInfo(name = "estado")
    private String estado; // enviada, revisada, calificada

    @ColumnInfo(name = "nombre_envio")
    private String nombreEnvio;

    @ColumnInfo(name = "sincronizado")
    private boolean sincronizado; // false si está pendiente de sync

    // Constructor vacío
    public EntregaEntity() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(int asignacionId) {
        this.asignacionId = asignacionId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getAcudienteId() {
        return acudienteId;
    }

    public void setAcudienteId(int acudienteId) {
        this.acudienteId = acudienteId;
    }

    public String getEvidenciaTexto() {
        return evidenciaTexto;
    }

    public void setEvidenciaTexto(String evidenciaTexto) {
        this.evidenciaTexto = evidenciaTexto;
    }

    public String getArchivosUrl() {
        return archivosUrl;
    }

    public void setArchivosUrl(String archivosUrl) {
        this.archivosUrl = archivosUrl;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreEnvio() {
        return nombreEnvio;
    }

    public void setNombreEnvio(String nombreEnvio) {
        this.nombreEnvio = nombreEnvio;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}

