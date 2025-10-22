package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.ProductoTipo;
import com.happyfeet.repository.ProductoTipoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductoTipoRepositoryImpl implements ProductoTipoRepository {
    private final Map<Integer, ProductoTipo> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @Override
    public void save(ProductoTipo productoTipo) {
        if (productoTipo == null) return;
        if (productoTipo.getId() <= 0) {
            productoTipo.setId(idGenerator.incrementAndGet());
        }
        storage.put(productoTipo.getId(), productoTipo);
    }

    @Override
    public ProductoTipo findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<ProductoTipo> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(ProductoTipo productoTipo) {
        if (productoTipo == null || productoTipo.getId() <= 0) return;
        if (storage.containsKey(productoTipo.getId())) {
            storage.put(productoTipo.getId(), productoTipo);
        }
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }
}