package com.happyfeet.service.impl;

import com.happyfeet.model.entities.EventoTipo;
import com.happyfeet.repository.EventoTipoRepository;
import com.happyfeet.service.EventoTipoService;
import java.util.List;

public class EventoTipoServiceImpl implements EventoTipoService {
    private final EventoTipoRepository repo;
    public EventoTipoServiceImpl(EventoTipoRepository repo) { this.repo = repo; }
    @Override
    public void crearEventoTipo(EventoTipo eventoTipo) { repo.save(eventoTipo); }
    @Override
    public EventoTipo obtenerEventoTipo(int id) { return repo.findById(id); }
    @Override
    public List<EventoTipo> listarEventoTipos() { return repo.findAll(); }
    @Override
    public void actualizarEventoTipo(EventoTipo eventoTipo) { repo.update(eventoTipo); }
    @Override
    public void eliminarEventoTipo(int id) { repo.delete(id); }
}