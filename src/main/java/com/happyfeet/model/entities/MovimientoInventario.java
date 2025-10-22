package com.happyfeet.model.entities;

import com.happyfeet.model.entities.enums.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad MovimientoInventario para registrar movimientos de stock.
 */
public class MovimientoInventario {
    private Integer id;
    private Integer productoId;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private String motivo;
    private Integer referenciaId;
    private String referenciaTipo;
    private LocalDateTime fechaMovimiento;
    private String usuarioRegistro;

    // Referencias a entidades relacionadas
    private Inventario producto;

    // Constructor por defecto
    public MovimientoInventario() {
        this.fechaMovimiento = LocalDateTime.now();
    }

    // Constructor con parámetros básicos
    public MovimientoInventario(Integer productoId, TipoMovimiento tipoMovimiento,
                                Integer cantidad, String motivo) {
        this();
        this.productoId = Objects.requireNonNull(productoId, "ID del producto es requerido");
        this.tipoMovimiento = Objects.requireNonNull(tipoMovimiento, "Tipo de movimiento es requerido");
        this.cantidad = Objects.requireNonNull(cantidad, "Cantidad es requerida");
        this.motivo = Objects.requireNonNull(motivo, "Motivo es requerido");
    }

    // Builder Pattern
    public static class Builder {
        private final MovimientoInventario movimiento;

        public Builder(Integer productoId, TipoMovimiento tipoMovimiento) {
            this.movimiento = new MovimientoInventario();
            this.movimiento.productoId = Objects.requireNonNull(productoId);
            this.movimiento.tipoMovimiento = Objects.requireNonNull(tipoMovimiento);
        }

        public Builder withCantidad(Integer cantidad) {
            movimiento.cantidad = cantidad;
            return this;
        }

        public Builder withCantidadAnterior(Integer cantidadAnterior) {
            movimiento.cantidadAnterior = cantidadAnterior;
            return this;
        }

        public Builder withCantidadNueva(Integer cantidadNueva) {
            movimiento.cantidadNueva = cantidadNueva;
            return this;
        }

        public Builder withMotivo(String motivo) {
            movimiento.motivo = motivo;
            return this;
        }

        public Builder withReferencia(Integer referenciaId, String referenciaTipo) {
            movimiento.referenciaId = referenciaId;
            movimiento.referenciaTipo = referenciaTipo;
            return this;
        }

        public Builder withUsuario(String usuarioRegistro) {
            movimiento.usuarioRegistro = usuarioRegistro;
            return this;
        }

        public MovimientoInventario build() {
            // Validaciones
            Objects.requireNonNull(movimiento.cantidad, "Cantidad es requerida");
            Objects.requireNonNull(movimiento.motivo, "Motivo es requerido");

            // Calcular cantidad nueva si no se especificó
            if (movimiento.cantidadNueva == null && movimiento.cantidadAnterior != null) {
                int factor = movimiento.tipoMovimiento.getFactorStock();
                movimiento.cantidadNueva = movimiento.cantidadAnterior + (factor * movimiento.cantidad);
            }

            return movimiento;
        }
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) {
        this.productoId = Objects.requireNonNull(productoId, "ID del producto no puede ser null");
    }

    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = Objects.requireNonNull(tipoMovimiento, "Tipo de movimiento no puede ser null");
    }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) {
        if (cantidad != null && cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser positiva");
        }
        this.cantidad = cantidad;
    }

    public Integer getCantidadAnterior() { return cantidadAnterior; }
    public void setCantidadAnterior(Integer cantidadAnterior) { this.cantidadAnterior = cantidadAnterior; }

    public Integer getCantidadNueva() { return cantidadNueva; }
    public void setCantidadNueva(Integer cantidadNueva) { this.cantidadNueva = cantidadNueva; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) {
        this.motivo = Objects.requireNonNull(motivo, "Motivo no puede ser null");
    }

    public Integer getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Integer referenciaId) { this.referenciaId = referenciaId; }

    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = Objects.requireNonNullElse(fechaMovimiento, LocalDateTime.now());
    }

    public String getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(String usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public Inventario getProducto() { return producto; }
    public void setProducto(Inventario producto) { this.producto = producto; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovimientoInventario)) return false;
        MovimientoInventario that = (MovimientoInventario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    // toString
    @Override
    public String toString() {
        return "MovimientoInventario{" +
                "id=" + id +
                ", productoId=" + productoId +
                ", tipoMovimiento=" + tipoMovimiento +
                ", cantidad=" + cantidad +
                ", cantidadAnterior=" + cantidadAnterior +
                ", cantidadNueva=" + cantidadNueva +
                ", motivo='" + motivo + '\'' +
                ", referenciaId=" + referenciaId +
                ", referenciaTipo='" + referenciaTipo + '\'' +
                ", fechaMovimiento=" + fechaMovimiento +
                ", usuarioRegistro='" + usuarioRegistro + '\'' +
                '}';
    }
}
