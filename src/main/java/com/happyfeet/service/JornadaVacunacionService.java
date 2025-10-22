package com.happyfeet.service;

import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.RegistroVacunacion;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de jornadas de vacunación.
 */
public interface JornadaVacunacionService {

    /**
     * Crea una nueva jornada de vacunación.
     */
    JornadaVacunacion crearJornada(JornadaVacunacion jornada);

    /**
     * Busca una jornada por ID.
     */
    Optional<JornadaVacunacion> buscarPorId(Integer id);

    /**
     * Lista todas las jornadas activas.
     */
    List<JornadaVacunacion> listarActivas();

    /**
     * Lista jornadas futuras y activas.
     */
    List<JornadaVacunacion> listarFuturas();

    /**
     * Registra una vacunación en una jornada.
     */
    RegistroVacunacion registrarVacunacion(Integer jornadaId, String nombreMascota,
                                           String nombreDueno, String telefono,
                                           String vacunaAplicada);

    /**
     * Cierra una jornada (no acepta más registros).
     */
    boolean cerrarJornada(Integer jornadaId);

    /**
     * Actualiza una jornada.
     */
    JornadaVacunacion actualizar(JornadaVacunacion jornada);

    /**
     * Elimina una jornada.
     */
    boolean eliminar(Integer id);

    /**
     * Obtiene el reporte de una jornada.
     */
    String generarReporte(Integer jornadaId);
}