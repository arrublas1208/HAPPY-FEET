package com.happyfeet.repository;

import com.happyfeet.model.entities.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends CrudRepository<Proveedor, Integer> {
    // Búsquedas específicas del dominio
    Optional<Proveedor> findByRuc(String ruc);
    List<Proveedor> findByNombre(String nombre);
    List<Proveedor> findByTipo(Proveedor.TipoProveedor tipo);
    List<Proveedor> findActivos();
    Optional<Proveedor> findByEmail(String email);

    // Compatibilidad con Long para interoperar con servicios
    default Optional<Proveedor> findById(Long id) {
        if (id == null) return Optional.empty();
        return findById(id.intValue());
    }

    default boolean deleteById(Long id) {
        if (id == null) return false;
        return deleteById(id.intValue());
    }

    default boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}