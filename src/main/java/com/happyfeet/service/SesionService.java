package com.happyfeet.service;

import com.happyfeet.model.entities.Sesion;
import java.util.List;

public interface SesionService {
    void crearSesion(Sesion sesion);
    Sesion obtenerSesion(int id);
    List<Sesion> listarSesiones();
    void actualizarSesion(Sesion sesion);
    void eliminarSesion(int id);
}