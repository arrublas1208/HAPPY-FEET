package com.happyfeet.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    // Crear o guardar un registro
    T save(T entity);

    // Leer o buscar un registro por id
    Optional<T> findById(ID id);

    // Leer o buscar todos los registros
    List<T> findAll();

    // Actualizar un registro
    T update(T entity);

    // Eliminar un registro por id
    boolean deleteById(ID id);
}
