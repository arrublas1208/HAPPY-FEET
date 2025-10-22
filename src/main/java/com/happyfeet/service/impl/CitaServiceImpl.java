package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.enums.CitaEstado;
import com.happyfeet.repository.CitaRepository;
import com.happyfeet.service.CitaService;
import com.happyfeet.util.FileLogger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de citas médicas.
 * Maneja todas las operaciones CRUD y reglas de negocio para citas.
 */
public class CitaServiceImpl implements CitaService {

    private static final FileLogger LOG = FileLogger.getInstance();

    private final CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = Objects.requireNonNull(citaRepository, "CitaRepository no puede ser null");
        LOG.info("CitaServiceImpl inicializado");
    }

    @Override
    public Cita crear(Cita cita) {
        validarNoNull(cita, "cita");
        validarCitaParaCreacion(cita);

        // Asignar estado inicial si no tiene
        if (cita.getEstado() == null) {
            cita.setEstado(CitaEstado.PROGRAMADA);
        }

        // Validar que no haya solape de horarios
        if (haySolape(cita.getIdVeterinario(), cita.getInicio(), cita.getFin())) {
            throw new ConflictoHorarioException("El veterinario ya tiene una cita en ese horario");
        }

        try {
            Cita citaCreada = citaRepository.save(cita);
            LOG.info("Cita creada: ID " + citaCreada.getId() + " para veterinario " + cita.getIdVeterinario());
            return citaCreada;
        } catch (Exception e) {
            LOG.error("Error creando cita", e);
            throw new ServiceException("Error al crear la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public Cita actualizar(Long id, Cita cambios) {
        validarNoNull(id, "id");
        validarNoNull(cambios, "cambios");

        Optional<Cita> citaExistente = citaRepository.findById(id);
        if (citaExistente.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontró cita con ID: " + id);
        }

        Cita cita = citaExistente.get();

        // Validar que se pueda modificar
        if (!cita.getEstado().esModificable()) {
            throw new EstadoInvalidoException("No se puede modificar una cita en estado: " + cita.getEstado().getNombre());
        }

        // Aplicar cambios
        if (cambios.getInicio() != null) {
            // Validar solape si se cambia la hora
            if (!cambios.getInicio().equals(cita.getInicio())) {
                LocalDateTime nuevoFin = cambios.getFin() != null ? cambios.getFin() : cambios.getInicio().plusHours(1);
                if (haySolape(cita.getIdVeterinario(), cambios.getInicio(), nuevoFin)) {
                    throw new ConflictoHorarioException("El veterinario ya tiene una cita en el nuevo horario");
                }
            }
            cita.setInicio(cambios.getInicio());
        }

        if (cambios.getFin() != null) {
            cita.setFin(cambios.getFin());
        }

        if (cambios.getMotivo() != null && !cambios.getMotivo().trim().isEmpty()) {
            cita.setMotivo(cambios.getMotivo());
        }

        try {
            Cita actualizada = citaRepository.update(cita);
            LOG.info("Cita actualizada: ID " + id);
            return actualizada;
        } catch (Exception e) {
            LOG.error("Error actualizando cita ID " + id, e);
            throw new ServiceException("Error al actualizar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public void confirmar(Long id) {
        cambiarEstado(id, CitaEstado.CONFIRMADA);
    }

    @Override
    public void iniciar(Long id) {
        cambiarEstado(id, CitaEstado.EN_CURSO);
    }

    @Override
    public void finalizar(Long id) {
        cambiarEstado(id, CitaEstado.FINALIZADA);
    }

    @Override
    public void cancelar(Long id) {
        cambiarEstado(id, CitaEstado.CANCELADA);
    }

    @Override
    public Cita reprogramar(Long id, LocalDateTime nuevoInicio, LocalDateTime nuevoFin, String nuevoMotivo) {
        validarNoNull(id, "id");
        validarNoNull(nuevoInicio, "nuevoInicio");

        Optional<Cita> citaOpt = citaRepository.findById(id);
        if (citaOpt.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontró cita con ID: " + id);
        }

        Cita cita = citaOpt.get();

        // Validar que se pueda reprogramar
        if (!cita.getEstado().puedeTransicionarA(CitaEstado.REPROGRAMADA)) {
            throw new EstadoInvalidoException("No se puede reprogramar una cita en estado: " + cita.getEstado().getNombre());
        }

        // Validar nuevo horario
        if (nuevoInicio.isBefore(LocalDateTime.now())) {
            throw new ValidationException("No se puede reprogramar para una fecha pasada");
        }

        LocalDateTime finCalculado = nuevoFin != null ? nuevoFin : nuevoInicio.plusHours(1);
        if (haySolape(cita.getIdVeterinario(), nuevoInicio, finCalculado)) {
            throw new ConflictoHorarioException("El veterinario ya tiene una cita en el nuevo horario");
        }

        // Actualizar datos
        cita.setInicio(nuevoInicio);
        cita.setFin(finCalculado);
        if (nuevoMotivo != null && !nuevoMotivo.trim().isEmpty()) {
            cita.setMotivo(nuevoMotivo);
        }

        // Cambiar estado
        cita.setEstado(CitaEstado.REPROGRAMADA);

        try {
            Cita reprogramada = citaRepository.update(cita);
            LOG.info("Cita reprogramada: ID " + id + " para " + nuevoInicio);
            return reprogramada;
        } catch (Exception e) {
            LOG.error("Error reprogramando cita ID " + id, e);
            throw new ServiceException("Error al reprogramar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cita> buscarPorId(Long id) {
        validarNoNull(id, "id");

        try {
            return citaRepository.findById(id);
        } catch (Exception e) {
            LOG.error("Error buscando cita por ID " + id, e);
            throw new ServiceException("Error al buscar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cita> listarPorFecha(LocalDate fecha) {
        validarNoNull(fecha, "fecha");

        try {
            return citaRepository.findByDate(fecha);
        } catch (Exception e) {
            LOG.error("Error listando citas por fecha " + fecha, e);
            throw new ServiceException("Error al listar citas por fecha: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cita> listarPorEstado(CitaEstado estado) {
        validarNoNull(estado, "estado");

        try {
            return citaRepository.findByEstado(estado);
        } catch (Exception e) {
            LOG.error("Error listando citas por estado " + estado, e);
            throw new ServiceException("Error al listar citas por estado: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cita> listarPorVeterinarioYEntre(Long idVet, LocalDateTime desde, LocalDateTime hasta) {
        validarNoNull(idVet, "idVet");
        validarNoNull(desde, "desde");
        validarNoNull(hasta, "hasta");

        if (hasta.isBefore(desde)) {
            throw new ValidationException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }

        try {
            return citaRepository.findByVeterinarioAndRange(idVet, desde, hasta);
        } catch (Exception e) {
            LOG.error("Error listando citas por veterinario y rango", e);
            throw new ServiceException("Error al listar citas por veterinario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean haySolape(Long idVet, LocalDateTime inicio, LocalDateTime fin) {
        validarNoNull(idVet, "idVet");
        validarNoNull(inicio, "inicio");

        if (fin == null) {
            fin = inicio.plusHours(1); // Duración por defecto
        }

        if (fin.isBefore(inicio) || fin.equals(inicio)) {
            throw new ValidationException("La hora de fin debe ser posterior a la de inicio");
        }

        try {
            return citaRepository.existsOverlap(idVet, inicio, fin);
        } catch (Exception e) {
            LOG.error("Error verificando solape de citas", e);
            throw new ServiceException("Error al verificar disponibilidad: " + e.getMessage(), e);
        }
    }

    // ============ MÉTODOS PRIVADOS AUXILIARES ============

    private void cambiarEstado(Long citaId, CitaEstado nuevoEstado) {
        validarNoNull(citaId, "citaId");
        validarNoNull(nuevoEstado, "nuevoEstado");

        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontró cita con ID: " + citaId);
        }

        Cita cita = citaOpt.get();
        CitaEstado estadoActual = cita.getEstado();

        // Validar transición de estado
        if (!estadoActual.puedeTransicionarA(nuevoEstado)) {
            throw new EstadoInvalidoException(
                    String.format("No se puede cambiar de %s a %s",
                            estadoActual.getNombre(), nuevoEstado.getNombre()));
        }

        // Validaciones específicas por estado
        switch (nuevoEstado) {
            case EN_CURSO -> {
                if (cita.getInicio().isAfter(LocalDateTime.now().plusMinutes(15))) {
                    throw new ValidationException("No se puede iniciar una cita con más de 15 minutos de anticipación");
                }
            }
            case FINALIZADA -> {
                if (estadoActual != CitaEstado.EN_CURSO) {
                    throw new EstadoInvalidoException("Solo se pueden finalizar citas que estén en curso");
                }
            }
        }

        cita.setEstado(nuevoEstado);

        try {
            citaRepository.update(cita);
            LOG.info("Estado de cita cambiado: ID " + citaId + " -> " + nuevoEstado.getNombre());
        } catch (Exception e) {
            LOG.error("Error cambiando estado de cita ID " + citaId, e);
            throw new ServiceException("Error al cambiar estado de la cita: " + e.getMessage(), e);
        }
    }

    private void validarCitaParaCreacion(Cita cita) {
        // Validar campos obligatorios
        if (cita.getIdVeterinario() == null) {
            throw new ValidationException("ID del veterinario es obligatorio");
        }

        if (cita.getIdMascota() == null) {
            throw new ValidationException("ID de la mascota es obligatorio");
        }

        if (cita.getInicio() == null) {
            throw new ValidationException("Fecha y hora de inicio es obligatoria");
        }

        if (cita.getMotivo() == null || cita.getMotivo().trim().isEmpty()) {
            throw new ValidationException("Motivo de la consulta es obligatorio");
        }

        // Validar lógica de negocio
        if (cita.getInicio().isBefore(LocalDateTime.now())) {
            throw new ValidationException("No se pueden crear citas en fechas pasadas");
        }

        if (cita.getFin() != null && cita.getFin().isBefore(cita.getInicio())) {
            throw new ValidationException("La hora de fin no puede ser anterior al inicio");
        }

        // Validar horarios de trabajo (ejemplo: 8 AM a 6 PM)
        int horaInicio = cita.getInicio().getHour();
        if (horaInicio < 8 || horaInicio > 18) {
            throw new ValidationException("Las citas deben programarse entre las 8:00 AM y 6:00 PM");
        }
    }

    private void validarNoNull(Object objeto, String nombreCampo) {
        if (objeto == null) {
            throw new ValidationException(nombreCampo + " no puede ser null");
        }
    }

    // ============ EXCEPCIONES ESPECÍFICAS ============

    public static class ServiceException extends RuntimeException {
        public ServiceException(String message) {
            super(message);
        }

        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String message) {
            super(message);
        }
    }

    public static class ConflictoHorarioException extends RuntimeException {
        public ConflictoHorarioException(String message) {
            super(message);
        }
    }

    public static class EstadoInvalidoException extends RuntimeException {
        public EstadoInvalidoException(String message) {
            super(message);
        }
    }

    // ============ GETTER PARA TESTING ============

    public CitaRepository getCitaRepository() {
        return citaRepository;
    }
}