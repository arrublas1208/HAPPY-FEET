package com.happyfeet.repository;

import com.happyfeet.model.entities.MascotaAdopcion;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar mascotas en adopción.
 */
public interface MascotaAdopcionRepository {

    /**
     * Guarda o actualiza una mascota en adopción.
     */
    MascotaAdopcion save(MascotaAdopcion mascota);

    /**
     * Busca una mascota por su ID.
     */
    Optional<MascotaAdopcion> findById(Integer id);

    /**
     * Lista todas las mascotas en adopción.
     */
    List<MascotaAdopcion> findAll();

    /**
     * Lista todas las mascotas disponibles (no adoptadas).
     */
    List<MascotaAdopcion> findDisponibles();

    /**
     * Lista todas las mascotas adoptadas.
     */
    List<MascotaAdopcion> findAdoptadas();

    /**
     * Busca mascotas por especie.
     */
    List<MascotaAdopcion> findByEspecie(String especie);

    /**
     * Busca mascotas por nombre (búsqueda parcial).
     */
    List<MascotaAdopcion> findByNombre(String nombre);

    /**
     * Marca una mascota como adoptada.
     */
    boolean marcarComoAdoptada(Integer id, Integer duenoId);

    /**
     * Elimina una mascota en adopción.
     */
    boolean delete(Integer id);

    /**
     * Cuenta el total de mascotas disponibles.
     */
    long countDisponibles();

    /**
     * Cuenta el total de mascotas adoptadas.
     */
    long countAdoptadas();
}