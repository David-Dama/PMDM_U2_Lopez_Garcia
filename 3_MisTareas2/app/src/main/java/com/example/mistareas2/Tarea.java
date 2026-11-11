package com.example.mistareas2;

public class Tarea {
    // Constantes para las prioridades (evitamos números mágicos en el código)
    public static final int PRIORIDAD_BAJA = 0;
    public static final int PRIORIDAD_MEDIA = 1;
    public static final int PRIORIDAD_ALTA = 2;

    private String titulo;
    private String descripcion;
    private int prioridad;
    private boolean completada;
    private String fechaLimite;

    // Constructor: al crear una tarea nueva, por defecto no está completada
    public Tarea(String titulo, String descripcion, int prioridad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.completada = false;
        this.fechaLimite = "";
    }

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
}