package com.example.catedra_fam.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.catedra_fam.database.daos.AsignacionDao;
import com.example.catedra_fam.database.daos.CalificacionDao;
import com.example.catedra_fam.database.daos.EntregaDao;
import com.example.catedra_fam.database.daos.EstudianteDao;
import com.example.catedra_fam.database.daos.NotificacionDao;
import com.example.catedra_fam.database.daos.PeriodoDao;
import com.example.catedra_fam.database.entities.AcudienteEntity;
import com.example.catedra_fam.database.entities.AsignacionEntity;
import com.example.catedra_fam.database.entities.CalificacionEntity;
import com.example.catedra_fam.database.entities.CursoEntity;
import com.example.catedra_fam.database.entities.EntregaEntity;
import com.example.catedra_fam.database.entities.EstudianteEntity;
import com.example.catedra_fam.database.entities.NotificacionEntity;
import com.example.catedra_fam.database.entities.PeriodoEntity;

@Database(entities = {
        AcudienteEntity.class,
        EstudianteEntity.class,
        CursoEntity.class,
        AsignacionEntity.class,
        EntregaEntity.class,
        CalificacionEntity.class,
        NotificacionEntity.class,
        PeriodoEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "catedra_familia_db";
    private static AppDatabase instance;

    // DAOs
    public abstract EstudianteDao estudianteDao();
    public abstract AsignacionDao asignacionDao();
    public abstract EntregaDao entregaDao();
    public abstract CalificacionDao calificacionDao();
    public abstract NotificacionDao notificacionDao();
    public abstract PeriodoDao periodoDao();

    // Singleton pattern
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // Para desarrollo, eliminar en producción
            .build();
        }
        return instance;
    }

    // Método para cerrar la base de datos
    public static void closeDatabase() {
        if (instance != null && instance.isOpen()) {
            instance.close();
            instance = null;
        }
    }
}

