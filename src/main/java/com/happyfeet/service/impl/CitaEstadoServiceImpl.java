package com.happyfeet.service.impl;

import com.happyfeet.model.entities.CitaEstado;
import com.happyfeet.repository.CitaEstadoRepository;
import com.happyfeet.service.CitaEstadoService;

import java.util.List;

/**
 * Implementación del servicio de CitaEstado
 */
public class CitaEstadoServiceImpl implements CitaEstadoService {
    private final CitaEstadoRepository repository;

    public CitaEstadoServiceImpl(CitaEstadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void crearCitaEstado(CitaEstado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        if (estado.getNombre() == null || estado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado es obligatorio");
        }
        repository.save(estado);
    }

    @Override
    public CitaEstado obtenerCitaEstado(int id) {
        CitaEstado estado = repository.findById(id);
        if (estado == null) {
            throw new IllegalArgumentException("Estado no encontrado con ID: " + id);
        }
        return estado;
    }

    @Override
    public List<CitaEstado> listarCitaEstados() {
        return repository.findAll();
    }

    @Override
    public void actualizarCitaEstado(CitaEstado estado) {
        if (estado == null || estado.getId() <= 0) {
            throw new IllegalArgumentException("Estado inválido");
        }
        repository.update(estado);
    }

    @Override
    public void eliminarCitaEstado(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        repository.delete(id);
    }
}