package com.happyfeet.service.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO para registrar el uso de insumos en consultas médicas
 */
public class UsoInsumo {
    private final Integer productoId;
    private final Integer cantidad;
    private String nombreInsumo;
    private Integer cantidadUsada;
    private LocalDateTime fechaUso;
    private String motivo;

    public UsoInsumo(Integer productoId, Integer cantidad) {
        this.productoId = Objects.requireNonNull(productoId);
        this.cantidad = Objects.requireNonNull(cantidad);
        this.cantidadUsada = cantidad;
        this.fechaUso = LocalDateTime.now();
        if (cantidad <= 0) throw new IllegalArgumentException("cantidad debe ser > 0");
    }

    public UsoInsumo() {
        this.productoId = null;
        this.cantidad = null;
        this.fechaUso = LocalDateTime.now();
    }

    public Integer getProductoId() { return productoId; }
    public Integer getCantidad() { return cantidad; }

    // Getters y setters adicionales
    public String getNombreInsumo() {
        return nombreInsumo;
    }

    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    public Integer getCantidadUsada() {
        return cantidadUsada != null ? cantidadUsada : cantidad;
    }

    public void setCantidadUsada(Integer cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }

    public LocalDateTime getFechaUso() {
        return fechaUso;
    }

    public void setFechaUso(LocalDateTime fechaUso) {
        this.fechaUso = fechaUso;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        return String.format("UsoInsumo{nombre='%s', cantidad=%d, fecha=%s}",
                nombreInsumo, getCantidadUsada(), fechaUso);
    }
}
