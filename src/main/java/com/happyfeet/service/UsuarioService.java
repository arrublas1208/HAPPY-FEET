package com.happyfeet.service;

import com.happyfeet.model.entities.Usuario;
import java.util.List;

public interface UsuarioService {
    void crearUsuario(Usuario usuario);
    Usuario obtenerUsuario(int id);
    List<Usuario> listarUsuarios();
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(int id);
}