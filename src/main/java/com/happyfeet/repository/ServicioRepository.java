package com.happyfeet.repository;

import com.happyfeet.model.entities.Servicio;
import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends CrudRepository<Servicio, Integer> {
    Optional<Servicio> findByNombre(String nombre);
    List<Servicio> findByActivo(boolean activo);
    List<Servicio> findByCategoria(String categoria);
}