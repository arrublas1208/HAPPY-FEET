package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Inventario {
    private Integer id;
    private String codigo;
    private String nombreProducto;
    private BigDecimal precioVenta;

    // Nuevos campos coherentes con schema.sql y reglas de negocio
    private Integer cantidadStock = 0;
    private Integer stockMinimo = 5;
    private LocalDate fechaVencimiento; // null si no aplica

    // Reglas de dominio esperadas por InventarioService
    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    public boolean tieneStock(Integer cantidad) {
        int c = cantidad == null ? 0 : cantidad;
        return c > 0 && cantidadStock != null && cantidadStock >= c;
    }

    public void descontarStock(Integer cantidad) {
        int c = Objects.requireNonNull(cantidad, "cantidad no puede ser null");
        if (c <= 0) throw new IllegalArgumentException("cantidad debe ser > 0");
        if (!tieneStock(c)) throw new IllegalStateException("Stock insuficiente");
        this.cantidadStock -= c;
    }

    // Getters y setters existentes
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    // Getters y setters nuevos
    public Integer getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(Integer cantidadStock) { this.cantidadStock = cantidadStock; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}