package com.happyfeet.service;

import com.happyfeet.model.entities.Inventario;
import java.util.List;

public interface InventarioService {

    /**
     * Descuenta una cantidad de un producto por su nombre.
     */
    void descontarProductoPorNombre(String nombreProducto, int cantidad);

    /**
     * Descuenta stock de un producto por ID.
     * Se mantiene el nombre del método tal como está en tu código para no romper llamadas existentes
     */
    void descontarStostck(Integer productoId, Integer cantidad);

    /**
     * Nueva API con el nombre correcto, manteniendo compatibilidad hacia atrás
     */
    void descontarStock(Integer productoId, Integer cantidad);

    /**
     * Verifica si hay disponibilidad de un producto por nombre
     */
    boolean verificarDisponibilidad(String nombreProducto, int cantidad);

    /**
     * Descuenta stock de un producto por nombre
     */
    void descontarStock(String nombreProducto, int cantidad);

    /**
     * Obtiene productos con stock bajo el mínimo
     */
    List<Inventario> obtenerProductosBajoStockMinimo();

    /**
     * Obtiene productos próximos a vencer
     */
    List<Inventario> obtenerProductosProximosAVencer();
}