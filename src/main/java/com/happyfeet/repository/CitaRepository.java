package com.happyfeet.repository;



// /src/main/java/com/happyfeet/repository/CitaRepository.java

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.enums.CitaEstado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository {
    Cita save(Cita cita);
    Cita update(Cita cita);
    Optional<Cita> findById(Long id);
    void deleteById(Long id);

    List<Cita> findByVeterinarioAndRange(Long idVet, LocalDateTime desde, LocalDateTime hasta);
    List<Cita> findByDate(LocalDate fecha);
    List<Cita> findByEstado(CitaEstado estado);

    // Para chequeos rápidos
    boolean existsOverlap(Long idVet, LocalDateTime inicio, LocalDateTime fin);
}