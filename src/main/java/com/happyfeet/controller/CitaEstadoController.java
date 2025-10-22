package com.happyfeet.controller;

import com.happyfeet.model.entities.CitaEstado;
import com.happyfeet.service.CitaEstadoService;
import java.util.List;

public class CitaEstadoController {
    private final CitaEstadoService service;

    public CitaEstadoController(CitaEstadoService service) {
        this.service = service;
    }

    public void crearCitaEstado(CitaEstado estado) {
        service.crearCitaEstado(estado);
    }

    public CitaEstado obtenerCitaEstado(int id) {
        return service.obtenerCitaEstado(id);
    }

    public List<CitaEstado> listarCitaEstados() {
        return service.listarCitaEstados();
    }

    public void actualizarCitaEstado(CitaEstado estado) {
        service.actualizarCitaEstado(estado);
    }

    public void eliminarCitaEstado(int id) {
        service.eliminarCitaEstado(id);
    }
}