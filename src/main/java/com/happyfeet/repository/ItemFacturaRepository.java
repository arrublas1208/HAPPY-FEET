package com.happyfeet.repository;

import com.happyfeet.model.entities.ItemFactura;

import java.util.List;
import java.util.Optional;

public interface ItemFacturaRepository extends CrudRepository<ItemFactura, Integer> {
    // Búsquedas específicas del dominio
    List<ItemFactura> findByFacturaId(Integer facturaId);
    List<ItemFactura> findByServicioId(Integer servicioId);
    List<ItemFactura> findByProductoId(Integer productoId);
    List<ItemFactura> findByTipoItem(ItemFactura.TipoItem tipoItem);

    // Compatibilidad con Long para interoperar con servicios
    default Optional<ItemFactura> findById(Long id) {
        if (id == null) return Optional.empty();
        return findById(id.intValue());
    }

    default boolean deleteById(Long id) {
        if (id == null) return false;
        return deleteById(id.intValue());
    }

    default List<ItemFactura> findByFacturaId(Long facturaId) {
        if (facturaId == null) return List.of();
        return findByFacturaId(facturaId.intValue());
    }
}