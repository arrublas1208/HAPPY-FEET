package com.happyfeet.repository;

import com.happyfeet.model.entities.EventoTipo;
import java.util.List;

public interface EventoTipoRepository {
    void save(EventoTipo eventoTipo);
    EventoTipo findById(int id);
    List<EventoTipo> findAll();
    void update(EventoTipo eventoTipo);
    void delete(int id);
}