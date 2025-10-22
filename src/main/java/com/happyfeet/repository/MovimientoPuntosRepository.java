package com.happyfeet.repository;

import com.happyfeet.model.entities.MovimientoPuntos;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar movimientos de puntos de clientes frecuentes.
 */
public interface MovimientoPuntosRepository {

    /**
     * Guarda un movimiento de puntos.
     */
    MovimientoPuntos save(MovimientoPuntos movimiento);

    /**
     * Busca un movimiento por su ID.
     */
    Optional<MovimientoPuntos> findById(Integer id);

    /**
     * Lista todos los movimientos de un dueño.
     */
    List<MovimientoPuntos> findByDuenoId(Integer duenoId);

    /**
     * Lista todos los movimientos de un dueño ordenados por fecha descendente.
     */
    List<MovimientoPuntos> findByDuenoIdOrderByFechaDesc(Integer duenoId);

    /**
     * Lista todos los movimientos.
     */
    List<MovimientoPuntos> findAll();

    /**
     * Calcula el total de puntos acumulados de un dueño.
     */
    int calcularTotalPuntos(Integer duenoId);

    /**
     * Elimina un movimiento.
     */
    boolean delete(Integer id);
}