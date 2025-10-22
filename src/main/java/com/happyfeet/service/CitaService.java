package com.happyfeet.service;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.enums.CitaEstado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaService {
    Cita crear(Cita cita);
    Cita actualizar(Long id, Cita cambios);

    void confirmar(Long id);
    void iniciar(Long id);
    void finalizar(Long id);
    void cancelar(Long id);

    // Reprogramar manteniendo estado programado/confirmado si aplica
    Cita reprogramar(Long id, LocalDateTime nuevoInicio, LocalDateTime nuevoFin, String nuevoMotivo);

    Optional<Cita> buscarPorId(Long id);
    List<Cita> listarPorFecha(LocalDate fecha);
    List<Cita> listarPorEstado(CitaEstado estado);
    List<Cita> listarPorVeterinarioYEntre(Long idVet, LocalDateTime desde, LocalDateTime hasta);

    boolean haySolape(Long idVet, LocalDateTime inicio, LocalDateTime fin);
}
