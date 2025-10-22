package com.happyfeet.repository;

import com.happyfeet.model.entities.HistorialMedico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio para la entidad HistorialMedico.
 * Extiende las operaciones CRUD básicas y define búsquedas específicas.
 */
public interface HistorialMedicoRepository extends CrudRepository<HistorialMedico, Integer> {

    // Buscar todos los historiales de una mascota
    List<HistorialMedico> findByMascotaId(Integer mascotaId);

    // Buscar historiales en un rango de fechas de evento
    List<HistorialMedico> findByFechaEventoBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar historiales de un veterinario
    List<HistorialMedico> findByVeterinarioId(Integer veterinarioId);

    // Buscar un historial específico por mascota y fecha exacta
    Optional<HistorialMedico> findByMascotaIdAndFechaEvento(Integer mascotaId, LocalDateTime fechaEvento);

    // Buscar historiales que requieren seguimiento
    List<HistorialMedico> findByRequiereSeguimientoTrue();

    // Método para reportes: Obtener servicios más solicitados
    List<Map<String, Object>> obtenerServiciosMasSolicitados(int limite);

    // Método para reportes: Obtener desempeño de veterinarios
    List<Map<String, Object>> obtenerDesempenoVeterinarios();
}
