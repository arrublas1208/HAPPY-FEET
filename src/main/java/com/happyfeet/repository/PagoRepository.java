package com.happyfeet.repository;

import com.happyfeet.model.entities.Pago;
import java.util.List;

public interface PagoRepository {
    void save(Pago pago);
    Pago findById(int id);
    List<Pago> findAll();
    void update(Pago pago);
    void delete(int id);
}