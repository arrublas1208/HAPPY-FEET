package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Proveedor;
import com.happyfeet.repository.ProveedorRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de proveedores
 */
public class ProveedorRepositoryImpl implements ProveedorRepository {

    private final Map<Integer, Proveedor> proveedores = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    @Override
    public Proveedor save(Proveedor proveedor) {
        if (proveedor.getId() == null) {
            proveedor.setId(idSequence.getAndIncrement());
        }
        proveedores.put(proveedor.getId(), proveedor);
        return proveedor;
    }

    @Override
    public Optional<Proveedor> findById(Integer id) {
        return Optional.ofNullable(proveedores.get(id));
    }

    @Override
    public List<Proveedor> findAll() {
        return new ArrayList<>(proveedores.values());
    }

    @Override
    public Proveedor update(Proveedor proveedor) {
        if (proveedor.getId() != null && proveedores.containsKey(proveedor.getId())) {
            proveedores.put(proveedor.getId(), proveedor);
            return proveedor;
        }
        throw new IllegalArgumentException("Proveedor no encontrado para actualizar");
    }

    @Override
    public boolean deleteById(Integer id) {
        return proveedores.remove(id) != null;
    }

    @Override
    public Optional<Proveedor> findByRuc(String ruc) {
        return proveedores.values().stream()
                .filter(p -> Objects.equals(p.getNit(), ruc))
                .findFirst();
    }

    @Override
    public List<Proveedor> findByNombre(String nombre) {
        return proveedores.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Proveedor> findByTipo(Proveedor.TipoProveedor tipo) {
        return proveedores.values().stream()
                .filter(p -> p.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Proveedor> findActivos() {
        return proveedores.values().stream()
                .filter(Proveedor::isActivo)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Proveedor> findByEmail(String email) {
        return proveedores.values().stream()
                .filter(p -> Objects.equals(p.getEmail(), email))
                .findFirst();
    }
}