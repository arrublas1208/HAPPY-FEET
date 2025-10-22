package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una jornada de vacunación masiva.
 */
public class JornadaVacunacion {
    private Integer id;
    private String nombre;
    private LocalDate fecha;
    private String ubicacion;
    private String vacunasDisponibles;
    private BigDecimal precioEspecial;
    private Integer capacidadMaxima;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Lista de registros de vacunación (se carga bajo demanda)
    private List<RegistroVacunacion> registros;

    public JornadaVacunacion() {
        this.activa = true;
        this.capacidadMaxima = 100;
        this.fechaCreacion = LocalDateTime.now();
        this.registros = new ArrayList<>();
    }

    // Builder Pattern
    public static class Builder {
        private final JornadaVacunacion jornada;

        public Builder() {
            this.jornada = new JornadaVacunacion();
        }

        public Builder withNombre(String nombre) {
            jornada.nombre = Objects.requireNonNull(nombre, "Nombre es requerido");
            return this;
        }

        public Builder withFecha(LocalDate fecha) {
            jornada.fecha = Objects.requireNonNull(fecha, "Fecha es requerida");
            return this;
        }

        public Builder withUbicacion(String ubicacion) {
            jornada.ubicacion = Objects.requireNonNull(ubicacion, "Ubicación es requerida");
            return this;
        }

        public Builder withVacunasDisponibles(String vacunas) {
            jornada.vacunasDisponibles = Objects.requireNonNull(vacunas, "Vacunas son requeridas");
            return this;
        }

        public Builder withPrecioEspecial(BigDecimal precio) {
            jornada.precioEspecial = Objects.requireNonNull(precio, "Precio es requerido");
            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Precio no puede ser negativo");
            }
            return this;
        }

        public Builder withCapacidadMaxima(Integer capacidad) {
            jornada.capacidadMaxima = Objects.requireNonNull(capacidad, "Capacidad es requerida");
            if (capacidad <= 0) {
                throw new IllegalArgumentException("Capacidad debe ser mayor a cero");
            }
            return this;
        }

        public JornadaVacunacion build() {
            if (jornada.nombre == null || jornada.nombre.trim().isEmpty()) {
                throw new IllegalStateException("Nombre es requerido");
            }
            if (jornada.fecha == null) {
                throw new IllegalStateException("Fecha es requerida");
            }
            if (jornada.ubicacion == null || jornada.ubicacion.trim().isEmpty()) {
                throw new IllegalStateException("Ubicación es requerida");
            }
            if (jornada.precioEspecial == null) {
                throw new IllegalStateException("Precio especial es requerido");
            }
            return jornada;
        }
    }

    // Métodos de negocio
    public int getCapacidadDisponible() {
        return capacidadMaxima - registros.size();
    }

    public boolean puedeRecibirMas() {
        return activa && getCapacidadDisponible() > 0;
    }

    public void cerrar() {
        this.activa = false;
    }

    public void activar() {
        this.activa = true;
    }

    public boolean esFutura() {
        return fecha.isAfter(LocalDate.now());
    }

    public boolean esHoy() {
        return fecha.isEqual(LocalDate.now());
    }

    public boolean esPasada() {
        return fecha.isBefore(LocalDate.now());
    }

    public int getTotalVacunaciones() {
        return registros.size();
    }

    public double getPorcentajeOcupacion() {
        if (capacidadMaxima == 0) return 0;
        return (registros.size() * 100.0) / capacidadMaxima;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getVacunasDisponibles() {
        return vacunasDisponibles;
    }

    public void setVacunasDisponibles(String vacunasDisponibles) {
        this.vacunasDisponibles = vacunasDisponibles;
    }

    public BigDecimal getPrecioEspecial() {
        return precioEspecial;
    }

    public void setPrecioEspecial(BigDecimal precioEspecial) {
        this.precioEspecial = precioEspecial;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<RegistroVacunacion> getRegistros() {
        return registros;
    }

    public void setRegistros(List<RegistroVacunacion> registros) {
        this.registros = registros != null ? registros : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JornadaVacunacion that = (JornadaVacunacion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s - Cupos: %d/%d - %s",
                nombre, fecha, ubicacion,
                getTotalVacunaciones(), capacidadMaxima,
                activa ? "ACTIVA" : "CERRADA");
    }

    public static Builder builder() {
        return new Builder();
    }
}