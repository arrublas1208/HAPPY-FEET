package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Pago;
import com.happyfeet.repository.PagoRepository;
import com.happyfeet.service.PagoService;
import java.util.List;

public class PagoServiceImpl implements PagoService {
    private final PagoRepository pagoRepository;

    public PagoServiceImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void registrarPago(Pago pago) {
        pagoRepository.save(pago);
    }

    @Override
    public Pago obtenerPago(int id) {
        return pagoRepository.findById(id);
    }

    @Override
    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    @Override
    public void actualizarPago(Pago pago) {
        pagoRepository.update(pago);
    }

    @Override
    public void eliminarPago(int id) {
        pagoRepository.delete(id);
    }
}