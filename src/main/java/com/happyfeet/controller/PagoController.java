package com.happyfeet.controller;

import com.happyfeet.model.entities.Pago;
import com.happyfeet.service.PagoService;

import java.util.List;

public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    public void crearPago(Pago pago) {
        pagoService.registrarPago(pago);
    }

    public Pago obtenerPago(int id) {
        return pagoService.obtenerPago(id);
    }

    public List<Pago> listarPagos() {
        return pagoService.listarPagos();
    }

    public void actualizarPago(Pago pago) {
        pagoService.actualizarPago(pago);
    }

    public void eliminarPago(int id) {
        pagoService.eliminarPago(id);
    }
}