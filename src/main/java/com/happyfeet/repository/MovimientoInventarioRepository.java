package com.happyfeet.repository;

import com.happyfeet.model.entities.MovimientoInventario;
import com.happyfeet.model.entities.enums.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoInventarioRepository extends CrudRepository<MovimientoInventario, Integer> {
    // Búsquedas específicas del dominio
    List<MovimientoInventario> findByProductoId(Integer productoId);
    List<MovimientoInventario> findByTipoMovimiento(TipoMovimiento tipoMovimiento);
    List<MovimientoInventario> findByFechaMovimientoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoInventario> findByReferenciaIdAndTipo(Integer referenciaId, String referenciaTipo);
    List<MovimientoInventario> findByUsuarioRegistro(String usuario);
    List<MovimientoInventario> findByProductoIdOrderByFechaDesc(Integer productoId);

    // Compatibilidad con Long para interoperar con servicios
    default Optional<MovimientoInventario> findById(Long id) {
        if (id == null) return Optional.empty();
        return findById(id.intValue());
    }

    default boolean deleteById(Long id) {
        if (id == null) return false;
        return deleteById(id.intValue());
    }

    default List<MovimientoInventario> findByProductoId(Long productoId) {
        if (productoId == null) return List.of();
        return findByProductoId(productoId.intValue());
    }
}