package com.happyfeet.service.impl;

import com.happyfeet.model.entities.MascotaAdopcion;
import com.happyfeet.repository.MascotaAdopcionRepository;
import com.happyfeet.service.LoggerManager;
import com.happyfeet.service.MascotaAdopcionService;

import java.util.List;
import java.util.Optional;

public class MascotaAdopcionServiceImpl implements MascotaAdopcionService {
    private final MascotaAdopcionRepository repository;
    private final LoggerManager logger;

    public MascotaAdopcionServiceImpl(MascotaAdopcionRepository repository, LoggerManager logger) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public MascotaAdopcion registrarMascota(MascotaAdopcion mascota) {
        logger.logInfo("Registrando mascota para adopción: " + mascota.getNombre());
        return repository.save(mascota);
    }

    @Override
    public Optional<MascotaAdopcion> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<MascotaAdopcion> listarDisponibles() {
        return repository.findDisponibles();
    }

    @Override
    public List<MascotaAdopcion> listarAdoptadas() {
        return repository.findAdoptadas();
    }

    @Override
    public List<MascotaAdopcion> buscarPorEspecie(String especie) {
        return repository.findByEspecie(especie);
    }

    @Override
    public boolean procesarAdopcion(Integer mascotaId, Integer duenoId) {
        logger.logInfo("Procesando adopción - Mascota ID: " + mascotaId + " Dueño ID: " + duenoId);
        return repository.marcarComoAdoptada(mascotaId, duenoId);
    }

    @Override
    public MascotaAdopcion actualizar(MascotaAdopcion mascota) {
        logger.logInfo("Actualizando mascota ID: " + mascota.getId());
        return repository.save(mascota);
    }

    @Override
    public boolean eliminar(Integer id) {
        logger.logInfo("Eliminando mascota ID: " + id);
        return repository.delete(id);
    }

    @Override
    public String obtenerEstadisticas() {
        long disponibles = repository.countDisponibles();
        long adoptadas = repository.countAdoptadas();
        long total = disponibles + adoptadas;

        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DE ADOPCIÓN ===\n");
        sb.append("Total de mascotas: ").append(total).append("\n");
        sb.append("Disponibles: ").append(disponibles);
        if (total > 0) {
            sb.append(String.format(" (%.1f%%)", (disponibles * 100.0 / total)));
        }
        sb.append("\n");
        sb.append("Adoptadas: ").append(adoptadas);
        if (total > 0) {
            sb.append(String.format(" (%.1f%%)", (adoptadas * 100.0 / total)));
        }
        sb.append("\n");

        return sb.toString();
    }
}