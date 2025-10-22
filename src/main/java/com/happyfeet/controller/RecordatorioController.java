package com.happyfeet.controller;

import com.happyfeet.model.entities.Recordatorio;
import com.happyfeet.service.RecordatorioService;
import java.util.List;

public class RecordatorioController {
    private final RecordatorioService service;

    public RecordatorioController(RecordatorioService service) {
        this.service = service;
    }

    public void crearRecordatorio(Recordatorio recordatorio) {
        service.crearRecordatorio(recordatorio);
    }

    public Recordatorio obtenerRecordatorio(int id) {
        return service.obtenerRecordatorio(id);
    }

    public List<Recordatorio> listarRecordatorios() {
        return service.listarRecordatorios();
    }

    public void actualizarRecordatorio(Recordatorio recordatorio) {
        service.actualizarRecordatorio(recordatorio);
    }

    public void eliminarRecordatorio(int id) {
        service.eliminarRecordatorio(id);
    }
}