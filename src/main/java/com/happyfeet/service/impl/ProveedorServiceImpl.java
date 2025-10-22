package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Proveedor;
import com.happyfeet.repository.ProveedorRepository;
import com.happyfeet.service.ProveedorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de proveedores
 */
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public Proveedor crearProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException("El proveedor no puede ser null");
        }

        // Validar que no exista un proveedor con el mismo NIT
        if (proveedor.getNit() != null && !proveedor.getNit().trim().isEmpty()) {
            if (existePorNit(proveedor.getNit())) {
                throw new IllegalArgumentException("Ya existe un proveedor con el NIT: " + proveedor.getNit());
            }
        }

        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor actualizarProveedor(Integer id, Proveedor cambios) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        if (proveedorOpt.isEmpty()) {
            throw new IllegalArgumentException("Proveedor no encontrado con ID: " + id);
        }

        Proveedor proveedor = proveedorOpt.get();

        // Actualizar campos
        if (cambios.getNombre() != null) {
            proveedor.setNombre(cambios.getNombre());
        }
        if (cambios.getNombreContacto() != null) {
            proveedor.setNombreContacto(cambios.getNombreContacto());
        }
        if (cambios.getTelefono() != null) {
            proveedor.setTelefono(cambios.getTelefono());
        }
        if (cambios.getEmail() != null) {
            proveedor.setEmail(cambios.getEmail());
        }
        if (cambios.getDireccion() != null) {
            proveedor.setDireccion(cambios.getDireccion());
        }
        if (cambios.getCiudad() != null) {
            proveedor.setCiudad(cambios.getCiudad());
        }
        if (cambios.getTipo() != null) {
            proveedor.setTipo(cambios.getTipo());
        }

        // Actualizar fecha de modificación
        proveedor.setFechaActualizacion(LocalDateTime.now());

        return proveedorRepository.update(proveedor);
    }

    @Override
    public void eliminarProveedor(Integer id) {
        cambiarEstado(id, false);
    }

    @Override
    public Optional<Proveedor> buscarPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public List<Proveedor> listarProveedoresActivos() {
        return proveedorRepository.findActivos();
    }

    @Override
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @Override
    public List<Proveedor> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }
        return proveedorRepository.findByNombre(nombre.trim());
    }

    @Override
    public List<Proveedor> buscarPorTipo(Proveedor.TipoProveedor tipo) {
        if (tipo == null) {
            return List.of();
        }
        return proveedorRepository.findByTipo(tipo);
    }

    @Override
    public List<Proveedor> buscarPorCiudad(String ciudad) {
        if (ciudad == null || ciudad.trim().isEmpty()) {
            return List.of();
        }
        return proveedorRepository.findAll().stream()
                .filter(p -> p.getCiudad() != null &&
                           p.getCiudad().toLowerCase().contains(ciudad.toLowerCase()))
                .toList();
    }

    @Override
    public boolean existePorNit(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            return false;
        }
        return proveedorRepository.findByRuc(nit.trim()).isPresent();
    }

    @Override
    public void cambiarEstado(Integer id, boolean activo) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        if (proveedorOpt.isEmpty()) {
            throw new IllegalArgumentException("Proveedor no encontrado con ID: " + id);
        }

        Proveedor proveedor = proveedorOpt.get();
        proveedor.setActivo(activo);
        proveedor.setFechaActualizacion(LocalDateTime.now());

        proveedorRepository.update(proveedor);
    }
}