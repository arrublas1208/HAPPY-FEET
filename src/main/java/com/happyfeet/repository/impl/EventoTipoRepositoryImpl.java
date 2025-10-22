package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.EventoTipo;
import com.happyfeet.repository.EventoTipoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EventoTipoRepositoryImpl implements EventoTipoRepository {
    private final Map<Integer, EventoTipo> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @Override
    public void save(EventoTipo eventoTipo) {
        if (eventoTipo == null) return;
        if (eventoTipo.getId() <= 0) {
            eventoTipo.setId(idGenerator.incrementAndGet());
        }
        storage.put(eventoTipo.getId(), eventoTipo);
    }

    @Override
    public EventoTipo findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<EventoTipo> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(EventoTipo eventoTipo) {
        if (eventoTipo == null || eventoTipo.getId() <= 0) return;
        if (storage.containsKey(eventoTipo.getId())) {
            storage.put(eventoTipo.getId(), eventoTipo);
        }
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }
}