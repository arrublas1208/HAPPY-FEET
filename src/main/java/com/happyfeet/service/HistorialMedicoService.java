package com.happyfeet.service;
import com.happyfeet.model.entities.HistorialMedico;
import java.util.List;
import java.util.Optional;
import com.happyfeet.service.dto.UsoInsumo;

/**
 * Servicio para registrar consultas/procedimientos médicos y aplicar reglas de negocio
 * relacionadas (por ejemplo, descuento de inventario al usar insumos o prescribir medicamentos).
 */
public interface HistorialMedicoService {

    void registrarConsulta(HistorialMedico consulta);
    List<HistorialMedico> obtenerHistorialPorMascota(Long mascotaId);
    HistorialMedico registrarConsultaConInsumos(HistorialMedico historial, List<UsoInsumo> insumos);
    Optional<HistorialMedico> obtenerPorId(Integer id);
}
