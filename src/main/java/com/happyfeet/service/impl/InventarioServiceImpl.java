package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Inventario;
import com.happyfeet.repository.InventarioRepository;
import com.happyfeet.service.InventarioService;
import java.util.List;

/**
 * Implementación del servicio de inventario.
 */
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public void descontarProductoPorNombre(String nombreProducto, int cantidad) {
        var productos = inventarioRepository.findByNombreProducto(nombreProducto);
        if (productos.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado: " + nombreProducto);
        }

        // Buscar el primer producto disponible (no vencido y con stock)
        Inventario producto = productos.stream()
                .filter(p -> !p.estaVencido() && p.tieneStock(cantidad))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay stock disponible o productos vencidos para: " + nombreProducto));

        producto.descontarStock(cantidad);
        inventarioRepository.update(producto);
    }

    @Override
    public void descontarStostck(Integer productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser un entero positivo");
        }
        Inventario producto = inventarioRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

        if (producto.estaVencido()) {
            throw new IllegalStateException("No se puede vender un producto vencido: " + producto.getNombreProducto());
        }
        if (!producto.tieneStock(cantidad)) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombreProducto());
        }

        producto.descontarStock(cantidad);
        inventarioRepository.update(producto);
    }

    @Override
    public void descontarStock(Integer productoId, Integer cantidad) {
        descontarStostck(productoId, cantidad);
    }

    @Override
    public boolean verificarDisponibilidad(String nombreProducto, int cantidad) {
        var productos = inventarioRepository.findByNombreProducto(nombreProducto);
        if (productos.isEmpty()) {
            return false;
        }

        // Verificar si existe al menos un producto disponible (no vencido y con stock suficiente)
        return productos.stream()
                .anyMatch(p -> !p.estaVencido() && p.tieneStock(cantidad));
    }

    @Override
    public void descontarStock(String nombreProducto, int cantidad) {
        descontarProductoPorNombre(nombreProducto, cantidad);
    }

    @Override
    public List<Inventario> obtenerProductosBajoStockMinimo() {
        return inventarioRepository.findProductosBajoStockMinimo();
    }

    @Override
    public List<Inventario> obtenerProductosProximosAVencer() {
        return inventarioRepository.findProductosProximosAVencer();
    }
}