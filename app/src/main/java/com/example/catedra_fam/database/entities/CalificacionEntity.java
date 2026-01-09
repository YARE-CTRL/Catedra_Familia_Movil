package com.example.catedra_fam.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "calificaciones",
        foreignKeys = {
                @ForeignKey(
                        entity = EntregaEntity.class,
                        parentColumns = "id",
                        childColumns = "entrega_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = EstudianteEntity.class,
                        parentColumns = "id",
                        childColumns = "estudiante_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = AsignacionEntity.class,
                        parentColumns = "id",
                        childColumns = "asignacion_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("entrega_id"), @Index("estudiante_id"), @Index("asignacion_id")})
public class CalificacionEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "entrega_id")
    private int entregaId;

    @ColumnInfo(name = "estudiante_id")
    private int estudianteId;

    @ColumnInfo(name = "asignacion_id")
    private int asignacionId;

    @ColumnInfo(name = "nota")
    private double nota; // 0.0 a 5.0

    @ColumnInfo(name = "escala")
    private String escala; // bajo, basico, alto, superior

    @ColumnInfo(name = "nota_cualitativa")
    private String notaCualitativa;

    @ColumnInfo(name = "retroalimentacion")
    private String retroalimentacion;

    @ColumnInfo(name = "calificado_en")
    private String calificadoEn;

    @ColumnInfo(name = "periodo_id")
    private int periodoId;

    // Constructor vacío
    public CalificacionEntity() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntregaId() {
        return entregaId;
    }

    public void setEntregaId(int entregaId) {
        this.entregaId = entregaId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(int asignacionId) {
        this.asignacionId = asignacionId;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getEscala() {
        return escala;
    }

    public void setEscala(String escala) {
        this.escala = escala;
    }

    public String getNotaCualitativa() {
        return notaCualitativa;
    }

    public void setNotaCualitativa(String notaCualitativa) {
        this.notaCualitativa = notaCualitativa;
    }

    public String getRetroalimentacion() {
        return retroalimentacion;
    }

    public void setRetroalimentacion(String retroalimentacion) {
        this.retroalimentacion = retroalimentacion;
    }

    public String getCalificadoEn() {
        return calificadoEn;
    }

    public void setCalificadoEn(String calificadoEn) {
        this.calificadoEn = calificadoEn;
    }

    public int getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(int periodoId) {
        this.periodoId = periodoId;
    }
}

