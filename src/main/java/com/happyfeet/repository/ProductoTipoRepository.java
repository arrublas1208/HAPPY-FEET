package com.happyfeet.repository;

import com.happyfeet.model.entities.ProductoTipo;
import java.util.List;

public interface ProductoTipoRepository {
    void save(ProductoTipo productoTipo);
    ProductoTipo findById(int id);
    List<ProductoTipo> findAll();
    void update(ProductoTipo productoTipo);
    void delete(int id);
}