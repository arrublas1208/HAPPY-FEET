package com.happyfeet.service.impl;

import com.happyfeet.model.entities.HistorialMedico;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.service.HistorialMedicoService;
import com.happyfeet.service.InventarioService;
import com.happyfeet.service.dto.UsoInsumo;
import com.happyfeet.util.FileLogger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HistorialMedicoServiceImpl implements HistorialMedicoService {

    private static final FileLogger LOG = FileLogger.getInstance();

    private final HistorialMedicoRepository historialRepo;
    private final InventarioService inventarioService;

    public HistorialMedicoServiceImpl(HistorialMedicoRepository historialRepo,
                                      InventarioService inventarioService) {
        this.historialRepo = Objects.requireNonNull(historialRepo);
        this.inventarioService = Objects.requireNonNull(inventarioService);
    }

    @Override
    public void registrarConsulta(HistorialMedico consulta) {
        historialRepo.save(consulta);
        if (consulta.getMedicamentosRecetados() != null && !consulta.getMedicamentosRecetados().isBlank()) {
            String[] meds = consulta.getMedicamentosRecetados().split(",");
            for (String med : meds) {
                inventarioService.descontarProductoPorNombre(med.trim(), 1);
            }
        }
    }

    @Override
    public List<HistorialMedico> obtenerHistorialPorMascota(Long mascotaId) {
        if (mascotaId == null) return List.of();
        return historialRepo.findByMascotaId(mascotaId.intValue());
    }

    @Override
    public HistorialMedico registrarConsultaConInsumos(HistorialMedico historial, List<UsoInsumo> insumos) {
        Objects.requireNonNull(historial, "historial no puede ser null");
        // Descontar inventario primero para asegurar reglas de negocio
        if (insumos != null) {
            for (UsoInsumo uso : insumos) {
                try {
                    inventarioService.descontarStock(uso.getProductoId(), uso.getCantidad());
                } catch (RuntimeException ex) {
                    LOG.error("Fallo al descontar inventario para producto " + uso.getProductoId(), ex);
                    throw ex; // Propagar para evitar persistir historial inconsistente
                }
            }
        }

        // Persistir historial médico
        HistorialMedico guardado = historialRepo.save(historial);
        LOG.info("Historial médico registrado con ID: " + guardado.getId());
        return guardado;
    }

    @Override
    public Optional<HistorialMedico> obtenerPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return historialRepo.findById(id);
    }

}