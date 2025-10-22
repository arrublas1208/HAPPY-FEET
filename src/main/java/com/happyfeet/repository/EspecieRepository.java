package com.happyfeet.repository;

import com.happyfeet.model.entities.Especie;
import java.util.Optional;

public interface EspecieRepository extends CrudRepository<Especie, Integer> {
    Optional<Especie> findByNombre(String nombre);
}