package com.happyfeet.service;

import com.happyfeet.model.entities.Veterinario;
import java.util.Optional;

public interface VeterinarioService {
    Optional<Veterinario> buscarPorId(Long id);
    boolean existePorId(Long id);
}