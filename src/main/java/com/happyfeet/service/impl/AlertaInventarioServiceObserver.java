package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Inventario;

import java.time.LocalDate;

/**
 * Servicio de alertas de inventario simple y reutilizable.
 *
 * Esta clase ya no depende de un patrón Observer acoplado al servicio.
 * Puede invocarse manualmente (p. ej., tras una venta o al cargar listado de productos)
 * mediante el método notificar(Inventario) o usando los métodos específicos.
 */
public class AlertaInventarioServiceObserver {

    private int diasAvisoVencimiento = 7; // configurable

    public AlertaInventarioServiceObserver() {}

    public AlertaInventarioServiceObserver(int diasAvisoVencimiento) {
        if (diasAvisoVencimiento > 0) {
            this.diasAvisoVencimiento = diasAvisoVencimiento;
        }
    }

    // Método principal de uso: decide y emite las alertas correspondientes
    public void notificar(Inventario producto) {
        if (producto == null) return;
        if (producto.estaVencido()) {
            onProductoVencido(producto);
            return;
        }
        if (esStockBajo(producto)) {
            onStockBajo(producto);
        } else if (estaPorVencer(producto)) {
            onProductoPorVencer(producto);
        }
    }

    // Compatibilidad con la API previa del Observer (ya no con @Override)
    public void onStockBajo(Inventario producto) {
        if (producto == null) return;
        System.out.printf("[ALERTA STOCK] Producto %s (codigo=%s) stock=%d, mínimo=%d%n",
                safe(producto.getNombreProducto()), safe(producto.getCodigo()),
                nz(producto.getCantidadStock()), nz(producto.getStockMinimo()));
    }

    public void onProductoVencido(Inventario producto) {
        if (producto == null) return;
        System.out.printf("[ALERTA VENCIDO] Producto %s (codigo=%s) vencido el %s%n",
                safe(producto.getNombreProducto()), safe(producto.getCodigo()),
                producto.getFechaVencimiento());
    }

    public void onProductoPorVencer(Inventario producto) {
        if (producto == null) return;
        System.out.printf("[ALERTA VENCIMIENTO PRÓXIMO] Producto %s (codigo=%s) vence el %s (aviso %d días) %n",
                safe(producto.getNombreProducto()), safe(producto.getCodigo()),
                producto.getFechaVencimiento(), diasAvisoVencimiento);
    }

    // Utilidades de decisión
    public boolean estaPorVencer(Inventario producto) {
        if (producto == null || producto.getFechaVencimiento() == null) return false;
        LocalDate hoy = LocalDate.now();
        LocalDate fv = producto.getFechaVencimiento();
        return !fv.isBefore(hoy) && !fv.isAfter(hoy.plusDays(diasAvisoVencimiento));
    }

    public boolean esStockBajo(Inventario producto) {
        if (producto == null) return false;
        Integer stock = producto.getCantidadStock();
        Integer min = producto.getStockMinimo();
        return stock != null && min != null && stock <= min;
    }

    // Configuración
    public int getDiasAvisoVencimiento() { return diasAvisoVencimiento; }
    public void setDiasAvisoVencimiento(int diasAvisoVencimiento) {
        if (diasAvisoVencimiento > 0) this.diasAvisoVencimiento = diasAvisoVencimiento;
    }

    // Helpers
    private static String safe(String s) { return s == null ? "" : s; }
    private static int nz(Integer i) { return i == null ? 0 : i; }
}
