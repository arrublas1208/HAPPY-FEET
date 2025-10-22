package com.happyfeet.service;

import com.happyfeet.model.entities.Recordatorio;
import java.util.List;

public interface RecordatorioService {
    void crearRecordatorio(Recordatorio recordatorio);
    Recordatorio obtenerRecordatorio(int id);
    List<Recordatorio> listarRecordatorios();
    void actualizarRecordatorio(Recordatorio recordatorio);
    void eliminarRecordatorio(int id);
}