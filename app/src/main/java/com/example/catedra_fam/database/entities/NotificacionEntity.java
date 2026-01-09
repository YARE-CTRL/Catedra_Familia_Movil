package com.example.catedra_fam.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notificaciones")
public class NotificacionEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "tipo")
    private String tipo; // tarea_asignada, tarea_calificada, recordatorio, etc

    @ColumnInfo(name = "asunto")
    private String asunto;

    @ColumnInfo(name = "mensaje")
    private String mensaje;

    @ColumnInfo(name = "estado")
    private String estado; // pendiente, enviada, leida

    @ColumnInfo(name = "leido")
    private boolean leido;

    @ColumnInfo(name = "leido_en")
    private String leidoEn;

    @ColumnInfo(name = "asignacion_id")
    private Integer asignacionId; // Puede ser null

    @ColumnInfo(name = "creado_en")
    private String creadoEn;

    // Constructor vacío
    public NotificacionEntity() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public String getLeidoEn() {
        return leidoEn;
    }

    public void setLeidoEn(String leidoEn) {
        this.leidoEn = leidoEn;
    }

    public Integer getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(Integer asignacionId) {
        this.asignacionId = asignacionId;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(String creadoEn) {
        this.creadoEn = creadoEn;
    }
}

