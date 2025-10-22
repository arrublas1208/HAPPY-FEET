package com.happyfeet.service;

import com.happyfeet.model.entities.Mascota;
import java.util.List;
import java.util.Optional;

public interface MascotaService {
    Mascota crearMascota(Mascota mascota);
    Mascota actualizarMascota(Long id, Mascota cambios);
    void eliminarMascota(Long id);
    Optional<Mascota> buscarPorId(Long id);
    List<Mascota> listarTodas();
    List<Mascota> buscarPorDueno(Long duenoId);
    List<Mascota> buscarPorNombre(String termino);
    boolean existePorMicrochip(String microchip);
}