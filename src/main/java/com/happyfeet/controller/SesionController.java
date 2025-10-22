package com.happyfeet.controller;

import com.happyfeet.model.entities.Sesion;
import com.happyfeet.service.SesionService;
import java.util.List;

public class SesionController {
    private final SesionService service;

    public SesionController(SesionService service) {
        this.service = service;
    }

    public void crearSesion(Sesion sesion) {
        service.crearSesion(sesion);
    }

    public Sesion obtenerSesion(int id) {
        return service.obtenerSesion(id);
    }

    public List<Sesion> listarSesiones() {
        return service.listarSesiones();
    }

    public void actualizarSesion(Sesion sesion) {
        service.actualizarSesion(sesion);
    }

    public void eliminarSesion(int id) {
        service.eliminarSesion(id);
    }
}