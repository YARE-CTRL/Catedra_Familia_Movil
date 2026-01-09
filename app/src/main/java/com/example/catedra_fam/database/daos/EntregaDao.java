package com.example.catedra_fam.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.catedra_fam.database.entities.EntregaEntity;

import java.util.List;

@Dao
public interface EntregaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(EntregaEntity entrega);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EntregaEntity> entregas);

    @Update
    void update(EntregaEntity entrega);

    @Delete
    void delete(EntregaEntity entrega);

    @Query("SELECT * FROM entregas WHERE estudiante_id = :estudianteId ORDER BY fecha_entrega DESC")
    List<EntregaEntity> getEntregasPorEstudiante(int estudianteId);

    @Query("SELECT * FROM entregas WHERE asignacion_id = :asignacionId AND estudiante_id = :estudianteId LIMIT 1")
    EntregaEntity getEntregaPorAsignacionYEstudiante(int asignacionId, int estudianteId);

    @Query("SELECT * FROM entregas WHERE sincronizado = 0")
    List<EntregaEntity> getEntregasPendientesSincronizacion();

    @Query("SELECT COUNT(*) FROM entregas WHERE estudiante_id = :estudianteId AND estado = 'enviada'")
    int contarEntregasPorEstudiante(int estudianteId);

    @Query("DELETE FROM entregas")
    void deleteAll();
}

