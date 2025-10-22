package com.happyfeet.service.impl;

import com.happyfeet.model.entities.ProductoTipo;
import com.happyfeet.repository.ProductoTipoRepository;
import com.happyfeet.service.ProductoTipoService;
import java.util.List;

public class ProductoTipoServiceImpl implements ProductoTipoService {
    private final ProductoTipoRepository repo;
    public ProductoTipoServiceImpl(ProductoTipoRepository repo) { this.repo = repo; }
    @Override
    public void crearProductoTipo(ProductoTipo productoTipo) { repo.save(productoTipo); }
    @Override
    public ProductoTipo obtenerProductoTipo(int id) { return repo.findById(id); }
    @Override
    public List<ProductoTipo> listarProductoTipos() { return repo.findAll(); }
    @Override
    public void actualizarProductoTipo(ProductoTipo productoTipo) { repo.update(productoTipo); }
    @Override
    public void eliminarProductoTipo(int id) { repo.delete(id); }
}