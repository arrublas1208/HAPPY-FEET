package com.happyfeet.controller;

import com.happyfeet.model.entities.ProductoTipo;
import com.happyfeet.service.ProductoTipoService;
import java.util.List;

public class ProductoTipoController {
    private final ProductoTipoService service;

    public ProductoTipoController(ProductoTipoService service) {
        this.service = service;
    }

    public void crearProductoTipo(ProductoTipo productoTipo) {
        service.crearProductoTipo(productoTipo);
    }

    public ProductoTipo obtenerProductoTipo(int id) {
        return service.obtenerProductoTipo(id);
    }

    public List<ProductoTipo> listarProductoTipos() {
        return service.listarProductoTipos();
    }

    public void actualizarProductoTipo(ProductoTipo productoTipo) {
        service.actualizarProductoTipo(productoTipo);
    }

    public void eliminarProductoTipo(int id) {
        service.eliminarProductoTipo(id);
    }
}