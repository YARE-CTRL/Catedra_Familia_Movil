package com.example.catedra_fam.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.catedra_fam.database.entities.EstudianteEntity;

import java.util.List;

@Dao
public interface EstudianteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EstudianteEntity estudiante);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EstudianteEntity> estudiantes);

    @Update
    void update(EstudianteEntity estudiante);

    @Delete
    void delete(EstudianteEntity estudiante);

    @Query("SELECT * FROM estudiantes WHERE acudiente_id = :acudienteId ORDER BY nombres ASC")
    List<EstudianteEntity> getEstudiantesPorAcudiente(int acudienteId);

    @Query("SELECT * FROM estudiantes WHERE id = :id LIMIT 1")
    EstudianteEntity getEstudiantePorId(int id);

    @Query("SELECT COUNT(*) FROM estudiantes WHERE acudiente_id = :acudienteId")
    int contarEstudiantesPorAcudiente(int acudienteId);

    @Query("DELETE FROM estudiantes")
    void deleteAll();
}

