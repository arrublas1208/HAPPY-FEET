package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Sesion;
import com.happyfeet.repository.SesionRepository;
import com.happyfeet.service.SesionService;
import java.util.List;

public class SesionServiceImpl implements SesionService {
    private final SesionRepository repo;
    public SesionServiceImpl(SesionRepository repo) { this.repo = repo; }
    @Override
    public void crearSesion(Sesion sesion) { repo.save(sesion); }
    @Override
    public Sesion obtenerSesion(int id) { return repo.findById(id); }
    @Override
    public List<Sesion> listarSesiones() { return repo.findAll(); }
    @Override
    public void actualizarSesion(Sesion sesion) { repo.update(sesion); }
    @Override
    public void eliminarSesion(int id) { repo.delete(id); }
}