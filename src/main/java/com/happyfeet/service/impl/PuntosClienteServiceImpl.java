package com.happyfeet.service.impl;

import com.happyfeet.model.entities.PuntosCliente;
import com.happyfeet.repository.PuntosClienteRepository;
import com.happyfeet.service.PuntosClienteService;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class PuntosClienteServiceImpl implements PuntosClienteService {
    private final PuntosClienteRepository repo;
    public PuntosClienteServiceImpl(PuntosClienteRepository repo) { this.repo = repo; }
    @Override
    public void crearPuntosCliente(PuntosCliente puntosCliente) { repo.save(puntosCliente); }
    @Override
    public PuntosCliente obtenerPuntosCliente(int id) { return repo.findById(id); }
    @Override
    public List<PuntosCliente> listarPuntosClientes() { return repo.findAll(); }
    @Override
    public void actualizarPuntosCliente(PuntosCliente puntosCliente) { repo.update(puntosCliente); }
    @Override
    public void eliminarPuntosCliente(int id) { repo.delete(id); }

    @Override
    public void acumularPuntos(Integer duenoId, Integer puntos, String concepto) {
        // Buscar puntos existentes del cliente o crear nuevos
        PuntosCliente puntosCliente = repo.findByDuenoId(duenoId);
        if (puntosCliente == null) {
            puntosCliente = new PuntosCliente();
            puntosCliente.setDuenoId(duenoId);
            puntosCliente.setPuntosAcumulados(puntos);
            repo.save(puntosCliente);
        } else {
            puntosCliente.setPuntosAcumulados(puntosCliente.getPuntosAcumulados() + puntos);
            repo.update(puntosCliente);
        }
    }

    @Override
    public boolean canjearPuntos(Integer duenoId, Integer puntos, String concepto) {
        PuntosCliente puntosCliente = repo.findByDuenoId(duenoId);
        if (puntosCliente != null && puntosCliente.getPuntosAcumulados() >= puntos) {
            puntosCliente.setPuntosAcumulados(puntosCliente.getPuntosAcumulados() - puntos);
            repo.update(puntosCliente);
            return true;
        }
        return false;
    }

    @Override
    public List<String> obtenerBeneficios(Integer duenoId) {
        PuntosCliente puntosCliente = repo.findByDuenoId(duenoId);
        if (puntosCliente == null) {
            return new ArrayList<>();
        }

        List<String> beneficios = new ArrayList<>();
        int puntos = puntosCliente.getPuntosAcumulados();

        if (puntos >= 100) beneficios.add("Descuento 5% en consultas");
        if (puntos >= 200) beneficios.add("Vacuna gratuita");
        if (puntos >= 500) beneficios.add("Descuento 10% en productos");
        if (puntos >= 1000) beneficios.add("Consulta gratuita");

        return beneficios;
    }

    @Override
    public List<String> obtenerHistorialPuntos(Integer duenoId) {
        // Implementación básica - en una aplicación real esto sería más complejo
        PuntosCliente puntosCliente = repo.findByDuenoId(duenoId);
        if (puntosCliente == null) {
            return Arrays.asList("No hay historial de puntos disponible");
        }

        return Arrays.asList(
            "Puntos actuales: " + puntosCliente.getPuntosAcumulados(),
            "Puntos redimidos: " + puntosCliente.getPuntosRedimidos(),
            "Cliente desde: " + (puntosCliente.getFechaCreacion() != null ? puntosCliente.getFechaCreacion() : "Fecha no disponible"),
            "Última actividad: " + (puntosCliente.getFechaUltimaActualizacion() != null ? puntosCliente.getFechaUltimaActualizacion() : "No disponible")
        );
    }
}