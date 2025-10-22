package com.happyfeet.repository;

import com.happyfeet.model.entities.Dueno;

import java.util.Optional;

public interface DuenoRepository extends CrudRepository<Dueno, Integer> {
    Dueno findByNombre(String nombre);
    Dueno findByDocumentoIdentidad(String documentoIdentidad);
    Dueno findByTelefono(String telefono);
    Dueno findByEmail(String email);
    Dueno findByContactoEmergencia(String contactoEmergencia);
    Dueno findByTipoSangre(String tipoSangre);

    // Compatibilidad con DuenoServiceImpl
    default boolean existsByDocumento(String documento) {
        if (documento == null) return false;
        return findByDocumentoIdentidad(documento) != null;
    }

    // Sobrecargas basadas en Long para interoperar con servicios que usan Long
    default Optional<Dueno> findById(Long id) {
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
