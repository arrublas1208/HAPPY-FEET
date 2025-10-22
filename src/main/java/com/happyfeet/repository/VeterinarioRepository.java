package com.happyfeet.repository;

import com.happyfeet.model.entities.Veterinario;
import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository extends CrudRepository<Veterinario, Integer> {
    List<Veterinario> findByEspecialidad(String especialidad);
    List<Veterinario> findActivos();
    Optional<Veterinario> findByDocumento(String documento);
    Optional<Veterinario> findByEmail(String email);

    // Compatibilidad con Long para interoperar con servicios
    default Optional<Veterinario> findById(Long id) {
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