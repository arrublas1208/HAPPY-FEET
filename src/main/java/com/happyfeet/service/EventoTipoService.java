package com.happyfeet.service;

import com.happyfeet.model.entities.EventoTipo;
import java.util.List;

public interface EventoTipoService {
    void crearEventoTipo(EventoTipo eventoTipo);
    EventoTipo obtenerEventoTipo(int id);
    List<EventoTipo> listarEventoTipos();
    void actualizarEventoTipo(EventoTipo eventoTipo);
    void eliminarEventoTipo(int id);
}