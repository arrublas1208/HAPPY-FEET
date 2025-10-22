package com.happyfeet.service.impl;

import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.RegistroVacunacion;
import com.happyfeet.repository.JornadaVacunacionRepository;
import com.happyfeet.repository.RegistroVacunacionRepository;
import com.happyfeet.service.JornadaVacunacionService;
import com.happyfeet.service.LoggerManager;

import java.util.List;
import java.util.Optional;

public class JornadaVacunacionServiceImpl implements JornadaVacunacionService {
    private final JornadaVacunacionRepository jornadaRepository;
    private final RegistroVacunacionRepository registroRepository;
    private final LoggerManager logger;

    public JornadaVacunacionServiceImpl(JornadaVacunacionRepository jornadaRepository,
                                       RegistroVacunacionRepository registroRepository,
                                       LoggerManager logger) {
        this.jornadaRepository = jornadaRepository;
        this.registroRepository = registroRepository;
        this.logger = logger;
    }

    @Override
    public JornadaVacunacion crearJornada(JornadaVacunacion jornada) {
        logger.logInfo("Creando jornada de vacunación: " + jornada.getNombre());
        return jornadaRepository.save(jornada);
    }

    @Override
    public Optional<JornadaVacunacion> buscarPorId(Integer id) {
        return jornadaRepository.findById(id);
    }

    @Override
    public List<JornadaVacunacion> listarActivas() {
        return jornadaRepository.findActivas();
    }

    @Override
    public List<JornadaVacunacion> listarFuturas() {
        return jornadaRepository.findFuturasActivas();
    }

    @Override
    public RegistroVacunacion registrarVacunacion(Integer jornadaId, String nombreMascota,
                                                  String nombreDueno, String telefono,
                                                  String vacunaAplicada) {
        logger.logInfo("Registrando vacunación en jornada ID: " + jornadaId);

        // Verificar que la jornada existe y está activa
        Optional<JornadaVacunacion> jornadaOpt = jornadaRepository.findById(jornadaId);
        if (jornadaOpt.isEmpty()) {
            throw new IllegalArgumentException("Jornada no encontrada");
        }

        JornadaVacunacion jornada = jornadaOpt.get();
        if (!jornada.getActiva()) {
            throw new IllegalStateException("La jornada no está activa");
        }

        // Verificar capacidad
        int registrosActuales = jornadaRepository.countRegistrosByJornada(jornadaId);
        if (registrosActuales >= jornada.getCapacidadMaxima()) {
            throw new IllegalStateException("La jornada ha alcanzado su capacidad máxima");
        }

        // Crear registro
        RegistroVacunacion registro = RegistroVacunacion.builder()
                .withJornadaId(jornadaId)
                .withNombreMascota(nombreMascota)
                .withNombreDueno(nombreDueno)
                .withTelefono(telefono)
                .withVacunaAplicada(vacunaAplicada)
                .build();

        return registroRepository.save(registro);
    }

    @Override
    public boolean cerrarJornada(Integer jornadaId) {
        logger.logInfo("Cerrando jornada ID: " + jornadaId);
        return jornadaRepository.cerrarJornada(jornadaId);
    }

    @Override
    public JornadaVacunacion actualizar(JornadaVacunacion jornada) {
        logger.logInfo("Actualizando jornada ID: " + jornada.getId());
        return jornadaRepository.save(jornada);
    }

    @Override
    public boolean eliminar(Integer id) {
        logger.logInfo("Eliminando jornada ID: " + id);
        return jornadaRepository.delete(id);
    }

    @Override
    public String generarReporte(Integer jornadaId) {
        Optional<JornadaVacunacion> jornadaOpt = jornadaRepository.findById(jornadaId);
        if (jornadaOpt.isEmpty()) {
            return "Jornada no encontrada";
        }

        JornadaVacunacion jornada = jornadaOpt.get();
        List<RegistroVacunacion> registros = registroRepository.findByJornadaId(jornadaId);

        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE JORNADA DE VACUNACIÓN ===\n");
        sb.append("Nombre: ").append(jornada.getNombre()).append("\n");
        sb.append("Fecha: ").append(jornada.getFecha()).append("\n");
        sb.append("Ubicación: ").append(jornada.getUbicacion()).append("\n");
        sb.append("Precio especial: $").append(jornada.getPrecioEspecial()).append("\n");
        sb.append("Capacidad: ").append(registros.size()).append("/").append(jornada.getCapacidadMaxima()).append("\n");
        sb.append("Estado: ").append(jornada.getActiva() ? "ACTIVA" : "CERRADA").append("\n");
        sb.append("\n--- VACUNACIONES REALIZADAS ---\n");

        if (registros.isEmpty()) {
            sb.append("No hay vacunaciones registradas\n");
        } else {
            for (int i = 0; i < registros.size(); i++) {
                RegistroVacunacion r = registros.get(i);
                sb.append(String.format("%d. %s - %s (%s) - %s\n",
                        i + 1, r.getNombreMascota(), r.getNombreDueno(),
                        r.getTelefono(), r.getVacunaAplicada()));
            }
        }

        return sb.toString();
    }
}