package com.example.mibiblioteca;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// BASE DE DATOS: Une las entidades y da acceso a los DAO.
@Database(entities = {Libro.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Método abstracto para que Room nos devuelva el DAO
    public abstract LibroDao libroDao();

    // Instancia única (Patrón Singleton)
    private static volatile AppDatabase instancia;

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "biblioteca.db" // Nombre físico del fichero SQLite en el móvil
                    ).build();
                }
            }
        }
        return instancia;
    }
}