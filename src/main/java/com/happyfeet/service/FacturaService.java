package com.happyfeet.service;

import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.Inventario;
import com.happyfeet.model.entities.Servicio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
public interface FacturaService {
    Factura crearFactura(Factura factura);
    Factura crearFacturaConItems(Factura factura, List<Factura.ItemFactura> items);
    Optional<Factura> obtenerPorId(Integer id);
    List<Factura> listarTodas();
    boolean eliminar(Integer id);

    Factura agregarServicio(Factura factura, Servicio servicio);
    Factura agregarProducto(Factura factura, Inventario producto, BigDecimal cantidad);
    Factura recalcularTotales(Factura factura);

    Factura generarFacturaPorConsulta(Integer consultaId);
}