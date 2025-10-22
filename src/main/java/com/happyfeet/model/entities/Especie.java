package com.happyfeet.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;


public class Especie {
    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaCreacion;

    // Builder Pattern para Especie
    public static class Builder {
        private final String nombre;
        private String descripcion;

        public Builder(String nombre) {
            this.nombre = Objects.requireNonNull(nombre, "Nombre de especie es requerido");
            if (nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("Nombre de especie no puede estar vacío");
            }
        }

        public Builder withDescripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Especie build() {
            Especie especie = new Especie();
            especie.nombre = this.nombre.trim();
            especie.descripcion = this.descripcion;
            especie.fechaCreacion = LocalDateTime.now();
            return especie;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) {
        if (id != null && id <= 0) throw new IllegalArgumentException("ID debe ser positivo");
        this.id = id;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        if (nombre.trim().isEmpty()) throw new IllegalArgumentException("Nombre no puede estar vacío");
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Especie otraEspecie = (Especie) obj;
        return Objects.equals(nombre, otraEspecie.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return String.format("Especie{id=%d, nombre='%s', descripcion='%s'}",
                id, nombre, descripcion);
    }

    public static Builder builder(String nombre) {
        return new Builder(nombre);
    }
}
