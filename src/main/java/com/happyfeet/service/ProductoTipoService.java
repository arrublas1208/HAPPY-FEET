package com.happyfeet.service;

import com.happyfeet.model.entities.ProductoTipo;
import java.util.List;

public interface ProductoTipoService {
    void crearProductoTipo(ProductoTipo productoTipo);
    ProductoTipo obtenerProductoTipo(int id);
    List<ProductoTipo> listarProductoTipos();
    void actualizarProductoTipo(ProductoTipo productoTipo);
    void eliminarProductoTipo(int id);
}