package com.happyfeet.repository;

import com.happyfeet.model.entities.Factura;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FacturaRepository {
    Factura save(Factura factura);
    Optional<Factura> findById(Integer id);
    List<Factura> findAll();
    boolean deleteById(Integer id);

    // Método para reportes: Obtener facturación por período
    Map<String, Object> obtenerFacturacionPorPeriodo(LocalDateTime inicio, LocalDateTime fin);

    // Método para reportes: Obtener total facturado
    BigDecimal obtenerTotalFacturado(LocalDateTime inicio, LocalDateTime fin);
}
