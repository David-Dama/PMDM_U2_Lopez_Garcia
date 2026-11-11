
package com.example.noticiasfp;

public class Noticia {

    private String titulo;
    private String descripcion;
    private String categoria;
    private String fecha;
    private int imagen;

    public Noticia(String titulo, String descripcion,
                   String categoria, String fecha, int imagen) {

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fecha = fecha;
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public int getImagen() {
        return imagen;
    }
}