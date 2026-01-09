package com.example.catedra_fam.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.catedra_fam.database.entities.AsignacionEntity;

import java.util.List;

@Dao
public interface AsignacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AsignacionEntity asignacion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AsignacionEntity> asignaciones);

    @Update
    void update(AsignacionEntity asignacion);

    @Delete
    void delete(AsignacionEntity asignacion);

    @Query("SELECT * FROM asignaciones WHERE curso_id = :cursoId AND periodo_id = :periodoId ORDER BY fecha_vencimiento ASC")
    List<AsignacionEntity> getAsignacionesPorCursoYPeriodo(int cursoId, int periodoId);

    @Query("SELECT * FROM asignaciones WHERE id = :id LIMIT 1")
    AsignacionEntity getAsignacionPorId(int id);

    @Query("SELECT * FROM asignaciones WHERE estado = :estado AND curso_id = :cursoId ORDER BY fecha_vencimiento ASC")
    List<AsignacionEntity> getAsignacionesPorEstado(String estado, int cursoId);

    @Query("SELECT COUNT(*) FROM asignaciones WHERE estado = 'pendiente' AND curso_id = :cursoId")
    int contarPendientesPorCurso(int cursoId);

    @Query("DELETE FROM asignaciones")
    void deleteAll();
}

