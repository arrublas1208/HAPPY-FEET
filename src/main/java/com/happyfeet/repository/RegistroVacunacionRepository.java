package com.happyfeet.repository;

import com.happyfeet.model.entities.RegistroVacunacion;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar registros de vacunación en jornadas.
 */
public interface RegistroVacunacionRepository {

    /**
     * Guarda un registro de vacunación.
     */
    RegistroVacunacion save(RegistroVacunacion registro);

    /**
     * Busca un registro por su ID.
     */
    Optional<RegistroVacunacion> findById(Integer id);

    /**
     * Lista todos los registros de una jornada específica.
     */
    List<RegistroVacunacion> findByJornadaId(Integer jornadaId);

    /**
     * Lista todos los registros de vacunación.
     */
    List<RegistroVacunacion> findAll();

    /**
     * Busca registros por nombre de mascota.
     */
    List<RegistroVacunacion> findByNombreMascota(String nombreMascota);

    /**
     * Busca registros por nombre de dueño.
     */
    List<RegistroVacunacion> findByNombreDueno(String nombreDueno);

    /**
     * Elimina un registro.
     */
    boolean delete(Integer id);

    /**
     * Cuenta el total de registros de una jornada.
     */
    int countByJornadaId(Integer jornadaId);

    /**
     * Cuenta el total de vacunaciones realizadas.
     */
    long countTotal();
}