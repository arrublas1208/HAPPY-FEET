package com.happyfeet.repository;

import com.happyfeet.model.entities.Sesion;
import java.util.List;

public interface SesionRepository {
    void save(Sesion sesion);
    Sesion findById(int id);
    List<Sesion> findAll();
    void update(Sesion sesion);
    void delete(int id);
}