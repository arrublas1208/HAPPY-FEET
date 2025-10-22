package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.repository.VeterinarioRepository;
import com.happyfeet.service.VeterinarioService;
import java.util.Optional;

public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    public Optional<Veterinario> buscarPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return veterinarioRepository.existsById(id);
    }
}