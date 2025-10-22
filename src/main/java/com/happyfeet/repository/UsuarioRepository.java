package com.happyfeet.repository;

import com.happyfeet.model.entities.Usuario;
import java.util.List;

public interface UsuarioRepository {
    void save(Usuario usuario);
    Usuario findById(int id);
    List<Usuario> findAll();
    void update(Usuario usuario);
    void delete(int id);
}