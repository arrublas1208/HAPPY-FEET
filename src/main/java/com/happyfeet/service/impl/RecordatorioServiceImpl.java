package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Recordatorio;
import com.happyfeet.repository.RecordatorioRepository;
import com.happyfeet.service.RecordatorioService;
import java.util.List;

public class RecordatorioServiceImpl implements RecordatorioService {
    private final RecordatorioRepository repo;
    public RecordatorioServiceImpl(RecordatorioRepository repo) { this.repo = repo; }
    @Override
    public void crearRecordatorio(Recordatorio recordatorio) { repo.save(recordatorio); }
    @Override
    public Recordatorio obtenerRecordatorio(int id) { return repo.findById(id); }
    @Override
    public List<Recordatorio> listarRecordatorios() { return repo.findAll(); }
    @Override
    public void actualizarRecordatorio(Recordatorio recordatorio) { repo.update(recordatorio); }
    @Override
    public void eliminarRecordatorio(int id) { repo.delete(id); }
}