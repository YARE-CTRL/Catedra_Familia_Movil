package com.example.catedra_fam.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "asignaciones",
        foreignKeys = {
                @ForeignKey(
                        entity = CursoEntity.class,
                        parentColumns = "id",
                        childColumns = "curso_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = PeriodoEntity.class,
                        parentColumns = "id",
                        childColumns = "periodo_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("curso_id"), @Index("periodo_id")})
public class AsignacionEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "frecuencia")
    private String frecuencia; // semanal, quincenal, mensual

    @ColumnInfo(name = "fecha_inicio")
    private String fechaInicio;

    @ColumnInfo(name = "fecha_vencimiento")
    private String fechaVencimiento;

    @ColumnInfo(name = "incluir_en_boletin")
    private boolean incluirEnBoletin;

    @ColumnInfo(name = "curso_id")
    private int cursoId;

    @ColumnInfo(name = "periodo_id")
    private int periodoId;

    @ColumnInfo(name = "tema")
    private String tema;

    @ColumnInfo(name = "estado")
    private String estado; // pendiente, entregada, calificada, vencida

    // Constructor vacío
    public AsignacionEntity() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isIncluirEnBoletin() {
        return incluirEnBoletin;
    }

    public void setIncluirEnBoletin(boolean incluirEnBoletin) {
        this.incluirEnBoletin = incluirEnBoletin;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public int getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(int periodoId) {
        this.periodoId = periodoId;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

