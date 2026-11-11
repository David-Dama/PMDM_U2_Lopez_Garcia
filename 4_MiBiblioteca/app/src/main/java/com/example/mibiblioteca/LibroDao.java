package com.example.mibiblioteca;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// DAO (Data Access Object): Define las operaciones sobre la tabla.
@Dao
public interface LibroDao {

    // Obtener todos los libros ordenados alfabéticamente por título
    @Query("SELECT * FROM libros ORDER BY titulo")
    List<Libro> obtenerTodos();

    // Contar cuántos libros tienen el campo 'leido' a 1 (verdadero)
    @Query("SELECT COUNT(*) FROM libros WHERE leido = 1")
    int contarLeidos();

    // Insertar un libro nuevo (devuelve el identificador único generado)
    @Insert
    long insertar(Libro libro);

    // Actualizar un libro existente
    @Update
    void actualizar(Libro libro);

    // Eliminar un libro de la base de datos
    @Delete
    void eliminar(Libro libro);
}