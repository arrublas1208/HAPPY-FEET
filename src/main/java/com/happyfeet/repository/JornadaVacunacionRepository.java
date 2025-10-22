package com.happyfeet.repository;

import com.happyfeet.model.entities.JornadaVacunacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar jornadas de vacunación.
 */
public interface JornadaVacunacionRepository {

    /**
     * Guarda o actualiza una jornada de vacunación.
     */
    JornadaVacunacion save(JornadaVacunacion jornada);

    /**
     * Busca una jornada por su ID.
     */
    Optional<JornadaVacunacion> findById(Integer id);

    /**
     * Lista todas las jornadas de vacunación.
     */
    List<JornadaVacunacion> findAll();

    /**
     * Lista todas las jornadas activas.
     */
    List<JornadaVacunacion> findActivas();

    /**
     * Lista jornadas por rango de fechas.
     */
    List<JornadaVacunacion> findByFechaRange(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Lista jornadas futuras activas.
     */
    List<JornadaVacunacion> findFuturasActivas();

    /**
     * Obtiene el número de registros de vacunación de una jornada.
     */
    int countRegistrosByJornada(Integer jornadaId);

    /**
     * Cierra una jornada (marca como inactiva).
     */
    boolean cerrarJornada(Integer id);

    /**
     * Elimina una jornada.
     */
    boolean delete(Integer id);

    /**
     * Cuenta el total de jornadas activas.
     */
    long countActivas();
}