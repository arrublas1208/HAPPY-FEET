package com.happyfeet.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un movimiento de puntos (ganados o canjeados) para un cliente frecuente.
 */
public class MovimientoPuntos {
    private Integer id;
    private Integer duenoId;
    private Integer puntos;
    private String concepto;
    private TipoMovimiento tipo;
    private LocalDateTime fecha;

    // Referencia al dueño
    private Dueno dueno;

    public enum TipoMovimiento {
        GANADOS("Ganados", "Puntos ganados por compra o actividad"),
        CANJEADOS("Canjeados", "Puntos utilizados en beneficios");

        private final String nombre;
        private final String descripcion;

        TipoMovimiento(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public static TipoMovimiento fromString(String tipo) {
            for (TipoMovimiento t : TipoMovimiento.values()) {
                if (t.nombre.equalsIgnoreCase(tipo) || t.name().equalsIgnoreCase(tipo)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Tipo de movimiento no válido: " + tipo);
        }
    }

    public MovimientoPuntos() {
        this.fecha = LocalDateTime.now();
    }

    public MovimientoPuntos(Integer puntos, String concepto, TipoMovimiento tipo) {
        this();
        this.puntos = puntos;
        this.concepto = concepto;
        this.tipo = tipo;
    }

    // Builder Pattern
    public static class Builder {
        private final MovimientoPuntos movimiento;

        public Builder() {
            this.movimiento = new MovimientoPuntos();
        }

        public Builder withDuenoId(Integer duenoId) {
            movimiento.duenoId = Objects.requireNonNull(duenoId, "ID de dueño es requerido");
            return this;
        }

        public Builder withPuntos(Integer puntos) {
            movimiento.puntos = Objects.requireNonNull(puntos, "Puntos son requeridos");
            return this;
        }

        public Builder withConcepto(String concepto) {
            movimiento.concepto = Objects.requireNonNull(concepto, "Concepto es requerido");
            return this;
        }

        public Builder withTipo(TipoMovimiento tipo) {
            movimiento.tipo = Objects.requireNonNull(tipo, "Tipo es requerido");
            return this;
        }

        public Builder withFecha(LocalDateTime fecha) {
            movimiento.fecha = fecha != null ? fecha : LocalDateTime.now();
            return this;
        }

        public MovimientoPuntos build() {
            if (movimiento.duenoId == null) {
                throw new IllegalStateException("ID de dueño es requerido");
            }
            if (movimiento.puntos == null || movimiento.puntos == 0) {
                throw new IllegalStateException("Puntos son requeridos y deben ser diferentes de cero");
            }
            if (movimiento.concepto == null || movimiento.concepto.trim().isEmpty()) {
                throw new IllegalStateException("Concepto es requerido");
            }
            if (movimiento.tipo == null) {
                throw new IllegalStateException("Tipo es requerido");
            }
            return movimiento;
        }
    }

    // Métodos de negocio
    public boolean esGanado() {
        return tipo == TipoMovimiento.GANADOS;
    }

    public boolean esCanjeado() {
        return tipo == TipoMovimiento.CANJEADOS;
    }

    public int getPuntosEfectivos() {
        return esCanjeado() ? -Math.abs(puntos) : Math.abs(puntos);
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDuenoId() {
        return duenoId;
    }

    public void setDuenoId(Integer duenoId) {
        this.duenoId = duenoId;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Dueno getDueno() {
        return dueno;
    }

    public void setDueno(Dueno dueno) {
        this.dueno = dueno;
        if (dueno != null) {
            this.duenoId = dueno.getId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimientoPuntos that = (MovimientoPuntos) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s: %+d puntos - %s (%s)",
                tipo.getNombre(),
                getPuntosEfectivos(),
                concepto,
                fecha != null ? fecha.toLocalDate().toString() : "N/A");
    }

    public static Builder builder() {
        return new Builder();
    }
}