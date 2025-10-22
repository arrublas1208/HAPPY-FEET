package com.happyfeet.repository;

import com.happyfeet.model.entities.Raza;

import java.util.List;
import java.util.Optional;

public interface RazaRepository extends CrudRepository<Raza, Integer> {
    // Búsquedas específicas del dominio
    List<Raza> findByEspecieId(Integer especieId);
    Optional<Raza> findByNombreAndEspecieId(String nombre, Integer especieId);
    List<Raza> findByNombre(String nombre);

    // Compatibilidad con Long para interoperar con servicios
    default Optional<Raza> findById(Long id) {
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

    default List<Raza> findByEspecieId(Long especieId) {
        if (especieId == null) return List.of();
        return findByEspecieId(especieId.intValue());
    }
}