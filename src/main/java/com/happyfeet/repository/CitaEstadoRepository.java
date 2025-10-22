package com.happyfeet.repository;

import com.happyfeet.model.entities.CitaEstado;
import java.util.List;

public interface CitaEstadoRepository {
    void save(CitaEstado estado);
    CitaEstado findById(int id);
    List<CitaEstado> findAll();
    void update(CitaEstado estado);
    void delete(int id);
}