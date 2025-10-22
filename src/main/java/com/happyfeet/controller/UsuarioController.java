package com.happyfeet.controller;

import com.happyfeet.model.entities.Usuario;
import com.happyfeet.service.UsuarioService;
import java.util.List;

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    public void crearUsuario(Usuario usuario) {
        service.crearUsuario(usuario);
    }

    public Usuario obtenerUsuario(int id) {
        return service.obtenerUsuario(id);
    }

    public List<Usuario> listarUsuarios() {
        return service.listarUsuarios();
    }

    public void actualizarUsuario(Usuario usuario) {
        service.actualizarUsuario(usuario);
    }

    public void eliminarUsuario(int id) {
        service.eliminarUsuario(id);
    }
}