package com.happyfeet.repository;

import com.happyfeet.model.entities.CompraClubFrecuente;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar compras del club de clientes frecuentes.
 */
public interface CompraClubFrecuenteRepository {

    /**
     * Guarda una compra.
     */
    CompraClubFrecuente save(CompraClubFrecuente compra);

    /**
     * Busca una compra por su ID.
     */
    Optional<CompraClubFrecuente> findById(Integer id);

    /**
     * Lista todas las compras de un dueño.
     */
    List<CompraClubFrecuente> findByDuenoId(Integer duenoId);

    /**
     * Lista todas las compras de un dueño ordenadas por fecha descendente.
     */
    List<CompraClubFrecuente> findByDuenoIdOrderByFechaDesc(Integer duenoId);

    /**
     * Lista todas las compras.
     */
    List<CompraClubFrecuente> findAll();

    /**
     * Calcula el total gastado por un dueño.
     */
    BigDecimal calcularTotalGastado(Integer duenoId);

    /**
     * Elimina una compra.
     */
    boolean delete(Integer id);
}