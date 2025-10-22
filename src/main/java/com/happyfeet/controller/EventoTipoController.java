package com.happyfeet.controller;

import com.happyfeet.model.entities.EventoTipo;
import com.happyfeet.service.EventoTipoService;
import java.util.List;

public class EventoTipoController {
    private final EventoTipoService service;

    public EventoTipoController(EventoTipoService service) {
        this.service = service;
    }

    public void crearEventoTipo(EventoTipo eventoTipo) {
        service.crearEventoTipo(eventoTipo);
    }

    public EventoTipo obtenerEventoTipo(int id) {
        return service.obtenerEventoTipo(id);
    }

    public List<EventoTipo> listarEventoTipos() {
        return service.listarEventoTipos();
    }

    public void actualizarEventoTipo(EventoTipo eventoTipo) {
        service.actualizarEventoTipo(eventoTipo);
    }

    public void eliminarEventoTipo(int id) {
        service.eliminarEventoTipo(id);
    }
}