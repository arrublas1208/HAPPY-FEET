package com.happyfeet.model.entities;

import java.util.Date;

public class EventoTipo {
    private int id;
    private String nombre;
    private String descripcion;
    private boolean requiereDiagnostico;
    private Date fechaCreacion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isRequiereDiagnostico() {
        return requiereDiagnostico;
    }

    public void setRequiereDiagnostico(boolean requiereDiagnostico) {
        this.requiereDiagnostico = requiereDiagnostico;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}