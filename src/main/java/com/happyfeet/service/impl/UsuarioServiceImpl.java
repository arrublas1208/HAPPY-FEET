package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Usuario;
import com.happyfeet.repository.UsuarioRepository;
import com.happyfeet.service.UsuarioService;
import java.util.List;

public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repo;
    public UsuarioServiceImpl(UsuarioRepository repo) { this.repo = repo; }
    @Override
    public void crearUsuario(Usuario usuario) { repo.save(usuario); }
    @Override
    public Usuario obtenerUsuario(int id) { return repo.findById(id); }
    @Override
    public List<Usuario> listarUsuarios() { return repo.findAll(); }
    @Override
    public void actualizarUsuario(Usuario usuario) { repo.update(usuario); }
    @Override
    public void eliminarUsuario(int id) { repo.delete(id); }
}