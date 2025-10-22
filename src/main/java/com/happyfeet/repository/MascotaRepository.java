package com.happyfeet.repository;

import com.happyfeet.model.entities.Mascota;

import java.util.List;
import java.util.Optional;

public interface MascotaRepository extends CrudRepository<Mascota, Integer> {
    // Búsquedas comunes
    List<Mascota> findByDuenoId(Integer duenoId);
    List<Mascota> findByNombre(String nombre);
    Mascota findByMicrochip(String microchip);

    // Sobrecargas basadas en Long para interoperar con servicios que usan Long
    default Optional<Mascota> findById(Long id) {
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

    default List<Mascota> findByDuenoId(Long duenoId) {
        if (duenoId == null) return List.of();
        return findByDuenoId(duenoId.intValue());
    }
}
