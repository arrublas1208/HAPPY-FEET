package com.happyfeet.service;

import com.happyfeet.model.entities.CitaEstado;
import java.util.List;

public interface CitaEstadoService {
    void crearCitaEstado(CitaEstado estado);
    CitaEstado obtenerCitaEstado(int id);
    List<CitaEstado> listarCitaEstados();
    void actualizarCitaEstado(CitaEstado estado);
    void eliminarCitaEstado(int id);
}