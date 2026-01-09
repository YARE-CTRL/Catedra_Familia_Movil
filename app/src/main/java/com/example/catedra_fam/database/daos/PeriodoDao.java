package com.example.catedra_fam.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.catedra_fam.database.entities.PeriodoEntity;

import java.util.List;

@Dao
public interface PeriodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PeriodoEntity periodo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PeriodoEntity> periodos);

    @Query("SELECT * FROM periodos WHERE esta_activo = 1 LIMIT 1")
    PeriodoEntity getPeriodoActivo();

    @Query("SELECT * FROM periodos ORDER BY fecha_inicio DESC")
    List<PeriodoEntity> getTodosLosPeriodos();

    @Query("DELETE FROM periodos")
    void deleteAll();
}

