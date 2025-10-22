package com.happyfeet.repository;

import com.happyfeet.model.entities.PuntosCliente;
import java.util.List;

public interface PuntosClienteRepository {
    void save(PuntosCliente puntosCliente);
    PuntosCliente findById(int id);
    List<PuntosCliente> findAll();
    void update(PuntosCliente puntosCliente);
    void delete(int id);
    PuntosCliente findByDuenoId(Integer duenoId);
}