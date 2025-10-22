package com.happyfeet.service;

import com.happyfeet.model.entities.Dueno;
import java.util.List;
import java.util.Optional;

public interface DuenoService {
    Dueno crearDueno(Dueno dueno);
    Dueno actualizarDueno(Long id, Dueno cambios);
    void eliminarDueno(Long id);
    Optional<Dueno> buscarPorId(Long id);
    List<Dueno> listarTodos();
    List<Dueno> buscarPorDueno(String termino); // Cambiado de buscarPorNombre
    boolean existePorDocumento(String documento);

    boolean existePorEmail(String email);
}