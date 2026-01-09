package com.example.catedra_fam.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.catedra_fam.database.entities.NotificacionEntity;

import java.util.List;

@Dao
public interface NotificacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotificacionEntity notificacion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NotificacionEntity> notificaciones);

    @Update
    void update(NotificacionEntity notificacion);

    @Delete
    void delete(NotificacionEntity notificacion);

    @Query("SELECT * FROM notificaciones ORDER BY creado_en DESC LIMIT :limit")
    List<NotificacionEntity> getNotificaciones(int limit);

    @Query("SELECT * FROM notificaciones WHERE leido = 0 ORDER BY creado_en DESC")
    List<NotificacionEntity> getNotificacionesNoLeidas();

    @Query("SELECT COUNT(*) FROM notificaciones WHERE leido = 0")
    int contarNoLeidas();

    @Query("UPDATE notificaciones SET leido = 1, leido_en = :fecha WHERE id = :id")
    void marcarComoLeida(int id, String fecha);

    @Query("DELETE FROM notificaciones")
    void deleteAll();
}

