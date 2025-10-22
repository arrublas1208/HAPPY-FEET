package com.happyfeet.repository;

import com.happyfeet.model.entities.Inventario;

import java.util.List;
import java.util.Optional;


public interface InventarioRepository extends CrudRepository<Inventario, Integer> {

    // Consultas propias del dominio Inventario
    Optional<Inventario> findByCodigo(String codigo);          // suele ser único
    List<Inventario> findByNombreProducto(String nombre);      // pueden existir varios con mismo nombre
    List<Inventario> findProductosBajoStockMinimo();
    List<Inventario> findProductosProximosAVencer();
        // Utilidades (opcionales) para interoperar con servicios que usan Long
        List<Inventario> findAll();
    default Optional<Inventario> findById(Long id) {
        if (id == null) return Optional.empty();
        return findById(id.intValue());
    }

    default boolean deleteById(Long id) {
        if (id == null) return false;
        return deleteById(id.intValue());
    }

    default boolean existsByCodigo(String codigo) {
        return codigo != null && findByCodigo(codigo).isPresent();
    }
}