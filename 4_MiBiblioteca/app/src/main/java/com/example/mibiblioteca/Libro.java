package com.example.mibiblioteca;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// ENTIDAD: Room crea una tabla "libros" con una columna por cada atributo[cite: 14].
@Entity(tableName = "libros")
public class Libro {

    @PrimaryKey(autoGenerate = true) // Clave primaria que Room numera sola: 1, 2, 3...[cite: 14]
    public int id;

    @NonNull
    public String titulo = ""; // La columna no admite valores nulos (NOT NULL)[cite: 14]

    @NonNull
    public String autor = ""; // La columna no admite valores nulos (NOT NULL)[cite: 14]

    public int paginas;

    public boolean leido; // En SQLite los booleanos se guardan internamente como 0 o 1[cite: 14]

    // Room necesita obligatoriamente un constructor vacío para funcionar[cite: 14]
    public Libro() {
    }

    // Constructor cómodo para nosotros. @Ignore le indica a Room que ignore este constructor[cite: 14]
    @Ignore
    public Libro(@NonNull String titulo, @NonNull String autor, int paginas) {
        this.titulo = titulo;
        this.autor = autor;
        this.paginas = paginas;
        this.leido = false; // Por defecto, al crear el libro no está leído[cite: 14]
    }
}