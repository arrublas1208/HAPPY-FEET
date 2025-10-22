package com.happyfeet.service;

import com.happyfeet.model.entities.Pago;
import java.util.List;

public interface PagoService {
    void registrarPago(Pago pago);
    Pago obtenerPago(int id);
    List<Pago> listarPagos();
    void actualizarPago(Pago pago);
    void eliminarPago(int id);
}