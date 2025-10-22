package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una compra registrada para el club de clientes frecuentes.
 */
public class CompraClubFrecuente {
    private Integer id;
    private Integer duenoId;
    private BigDecimal monto;
    private Integer puntosGenerados;
    private LocalDateTime fecha;
    private String descripcion;

    // Referencia al dueño
    private Dueno dueno;

    public CompraClubFrecuente() {
        this.fecha = LocalDateTime.now();
        this.puntosGenerados = 0;
    }

    // Builder Pattern
    public static class Builder {
        private final CompraClubFrecuente compra;

        public Builder() {
            this.compra = new CompraClubFrecuente();
        }

        public Builder withDuenoId(Integer duenoId) {
            compra.duenoId = Objects.requireNonNull(duenoId, "ID de dueño es requerido");
            return this;
        }

        public Builder withMonto(BigDecimal monto) {
            compra.monto = Objects.requireNonNull(monto, "Monto es requerido");
            if (monto.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Monto no puede ser negativo");
            }
            return this;
        }

        public Builder withPuntosGenerados(Integer puntos) {
            compra.puntosGenerados = puntos != null ? puntos : 0;
            return this;
        }

        public Builder withDescripcion(String descripcion) {
            compra.descripcion = descripcion;
            return this;
        }

        public Builder withFecha(LocalDateTime fecha) {
            compra.fecha = fecha != null ? fecha : LocalDateTime.now();
            return this;
        }

        public CompraClubFrecuente build() {
            if (compra.duenoId == null) {
                throw new IllegalStateException("ID de dueño es requerido");
            }
            if (compra.monto == null) {
                throw new IllegalStateException("Monto es requerido");
            }
            return compra;
        }
    }

    // Métodos de negocio
    public static int calcularPuntos(BigDecimal monto) {
        // 1 punto por cada $1000 gastados
        return monto.divide(new BigDecimal("1000"), 0, BigDecimal.ROUND_DOWN).intValue();
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(Integer puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        CompraClubFrecuente that = (CompraClubFrecuente) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Compra: $%.2f - %d puntos - %s",
                monto,
                puntosGenerados,
                fecha != null ? fecha.toLocalDate().toString() : "N/A");
    }

    public static Builder builder() {
        return new Builder();
    }
}