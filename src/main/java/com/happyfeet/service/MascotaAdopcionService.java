package com.happyfeet.service;

import com.happyfeet.model.entities.MascotaAdopcion;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de mascotas en adopción.
 */
public interface MascotaAdopcionService {

    /**
     * Registra una nueva mascota para adopción.
     */
    MascotaAdopcion registrarMascota(MascotaAdopcion mascota);

    /**
     * Busca una mascota por ID.
     */
    Optional<MascotaAdopcion> buscarPorId(Integer id);

    /**
     * Lista todas las mascotas disponibles para adopción.
     */
    List<MascotaAdopcion> listarDisponibles();

    /**
     * Lista todas las mascotas adoptadas.
     */
    List<MascotaAdopcion> listarAdoptadas();

    /**
     * Busca mascotas por especie.
     */
    List<MascotaAdopcion> buscarPorEspecie(String especie);

    /**
     * Procesa la adopción de una mascota.
     */
    boolean procesarAdopcion(Integer mascotaId, Integer duenoId);

    /**
     * Actualiza información de una mascota.
     */
    MascotaAdopcion actualizar(MascotaAdopcion mascota);

    /**
     * Elimina una mascota del sistema de adopción.
     */
    boolean eliminar(Integer id);

    /**
     * Obtiene estadísticas de adopciones.
     */
    String obtenerEstadisticas();
}