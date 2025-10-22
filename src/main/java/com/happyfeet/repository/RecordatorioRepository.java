package com.happyfeet.repository;

import com.happyfeet.model.entities.Recordatorio;
import java.util.List;

public interface RecordatorioRepository {
    void save(Recordatorio recordatorio);
    Recordatorio findById(int id);
    List<Recordatorio> findAll();
    void update(Recordatorio recordatorio);
    void delete(int id);
}