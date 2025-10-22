package com.happyfeet.service;

import com.happyfeet.model.entities.PuntosCliente;
import java.util.List;

public interface PuntosClienteService {
    void crearPuntosCliente(PuntosCliente puntosCliente);
    PuntosCliente obtenerPuntosCliente(int id);
    List<PuntosCliente> listarPuntosClientes();
    void actualizarPuntosCliente(PuntosCliente puntosCliente);
    void eliminarPuntosCliente(int id);

    // Métodos adicionales para gestión de puntos
    void acumularPuntos(Integer duenoId, Integer puntos, String concepto);
    boolean canjearPuntos(Integer duenoId, Integer puntos, String concepto);
    List<String> obtenerBeneficios(Integer duenoId);
    List<String> obtenerHistorialPuntos(Integer duenoId);
}