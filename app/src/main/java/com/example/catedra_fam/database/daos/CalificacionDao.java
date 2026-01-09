package com.example.catedra_fam.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.catedra_fam.database.entities.CalificacionEntity;

import java.util.List;

@Dao
public interface CalificacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalificacionEntity calificacion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CalificacionEntity> calificaciones);

    @Update
    void update(CalificacionEntity calificacion);

    @Delete
    void delete(CalificacionEntity calificacion);

    @Query("SELECT * FROM calificaciones WHERE estudiante_id = :estudianteId AND periodo_id = :periodoId ORDER BY calificado_en DESC")
    List<CalificacionEntity> getCalificacionesPorEstudianteYPeriodo(int estudianteId, int periodoId);

    @Query("SELECT * FROM calificaciones WHERE entrega_id = :entregaId LIMIT 1")
    CalificacionEntity getCalificacionPorEntrega(int entregaId);

    @Query("SELECT AVG(nota) FROM calificaciones WHERE estudiante_id = :estudianteId AND periodo_id = :periodoId")
    Double getPromedioEstudiante(int estudianteId, int periodoId);

    @Query("SELECT COUNT(*) FROM calificaciones WHERE estudiante_id = :estudianteId AND periodo_id = :periodoId")
    int contarCalificacionesPorEstudiante(int estudianteId, int periodoId);

    @Query("DELETE FROM calificaciones")
    void deleteAll();
}

