package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.repository.DuenoRepository;
import com.happyfeet.service.DuenoService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class DuenoServiceImpl implements DuenoService {

    private final DuenoRepository duenoRepository;

    public DuenoServiceImpl(DuenoRepository duenoRepository) {
        this.duenoRepository = Objects.requireNonNull(duenoRepository, "duenoRepository no puede ser null");
    }

    // CORREGIDO: Cambiado de 'crear' a 'crearDueno' para coincidir con la interfaz
    @Override
    public Dueno crearDueno(Dueno dueno) {
        validarNoNull(dueno, "dueno");
        validarNegocio(dueno);
        validarDocumentoUnico(dueno.getDocumentoIdentidad());
        return duenoRepository.save(dueno);
    }

    // CORREGIDO: Cambiado de 'actualizar' a 'actualizarDueno' para coincidir con la interfaz
    @Override
    public Dueno actualizarDueno(Long id, Dueno cambios) {
        validarNoNull(id, "id");
        validarNoNull(cambios, "cambios");
        var existente = duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueño con id " + id + " no existe"));

        // Validaciones previas a aplicar cambios
        if (cambios.getDocumentoIdentidad() != null &&
                !cambios.getDocumentoIdentidad().isBlank() &&
                !cambios.getDocumentoIdentidad().equals(existente.getDocumentoIdentidad())) {
            validarDocumentoUnico(cambios.getDocumentoIdentidad());
        }

        // Aplicación de cambios de forma segura
        var actualizado = mergeDueno(existente, cambios);
        validarNegocio(actualizado);

        return duenoRepository.save(actualizado); // CORREGIDO: Cambiado de 'update' a 'save'
    }

    // CORREGIDO: Cambiado de 'eliminar' a 'eliminarDueno' para coincidir con la interfaz
    @Override
    public void eliminarDueno(Long id) {
        validarNoNull(id, "id");
        if (!duenoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Dueño con id " + id + " no existe");
        }
        duenoRepository.deleteById(id);
    }

    @Override
    public Optional<Dueno> buscarPorId(Long id) {
        validarNoNull(id, "id");
        return duenoRepository.findById(id);
    }

    @Override
    public List<Dueno> listarTodos() {
        return duenoRepository.findAll().stream()
                .sorted(Comparator.comparing(Dueno::getApellido)
                        .thenComparing(Dueno::getNombre)
                        .thenComparing(Dueno::getId))
                .toList();
    }

    // CORREGIDO: Cambiado de 'buscarPorNombre' a 'buscarPorDueno' para coincidir con la interfaz
    @Override
    public List<Dueno> buscarPorDueno(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarTodos();
        }

        var filtro = normalizar(termino);
        Predicate<Dueno> nombreMatch = d -> normalizar(d.getNombre()).contains(filtro);
        Predicate<Dueno> apellidoMatch = d -> normalizar(d.getApellido()).contains(filtro);
        Predicate<Dueno> documentoMatch = d -> normalizar(d.getDocumentoIdentidad()).contains(filtro);
        Predicate<Dueno> emailMatch = d -> normalizar(d.getEmail()).contains(filtro);

        return duenoRepository.findAll().stream()
                .filter(nombreMatch.or(apellidoMatch).or(documentoMatch).or(emailMatch))
                .sorted(Comparator.comparing(Dueno::getApellido).thenComparing(Dueno::getNombre))
                .toList();
    }

    @Override
    public boolean existePorDocumento(String documento) {
        validarTextoNoVacio(documento, "documento");
        return duenoRepository.existsByDocumento(documento.trim());
    }

    // MÉTODO ADICIONAL: Para soportar la funcionalidad del controller
    public boolean existePorEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String emailNormalizado = email.trim().toLowerCase();
        return duenoRepository.findAll().stream()
                .anyMatch(d -> d.getEmail() != null &&
                        d.getEmail().toLowerCase().equals(emailNormalizado));
    }

    // ------------------ Helpers de validación y util ------------------

    private void validarNegocio(Dueno d) {
        validarTextoNoVacio(d.getNombreCompleto(), "nombreCompleto");
        validarTextoNoVacio(d.getDocumentoIdentidad(), "documentoIdentidad");
        validarTextoNoVacio(d.getEmail(), "email");

        if (d.getTelefono() != null && d.getTelefono().length() > 20) {
            throw new ValidacionException("teléfono excede longitud permitida");
        }

        // Validación de email mejorada
        if (d.getEmail() != null && !d.getEmail().isBlank()) {
            if (!d.getEmail().contains("@")) {
                throw new ValidacionException("email inválido: debe contener @");
            }
            if (d.getEmail().length() > 100) {
                throw new ValidacionException("email excede longitud permitida");
            }
        }
    }

    private void validarDocumentoUnico(String documento) {
        validarTextoNoVacio(documento, "documentoIdentidad");
        if (existePorDocumento(documento.trim())) {
            throw new ConflictoDeDatosException("Ya existe un dueño con documento " + documento);
        }
    }

    private static Dueno mergeDueno(Dueno base, Dueno cambios) {
        // Estrategia: si el campo en cambios es null o blank, se mantiene el valor base.
        if (cambios.getNombreCompleto() != null && !cambios.getNombreCompleto().isBlank()) {
            base.setNombreCompleto(cambios.getNombreCompleto().trim());
        }
        if (cambios.getDocumentoIdentidad() != null && !cambios.getDocumentoIdentidad().isBlank()) {
            base.setDocumentoIdentidad(cambios.getDocumentoIdentidad().trim());
        }
        if (cambios.getTelefono() != null && !cambios.getTelefono().isBlank()) {
            base.setTelefono(cambios.getTelefono().trim());
        }
        if (cambios.getEmail() != null && !cambios.getEmail().isBlank()) {
            base.setEmail(cambios.getEmail().trim());
        }
        if (cambios.getDireccion() != null && !cambios.getDireccion().isBlank()) {
            base.setDireccion(cambios.getDireccion().trim());
        }
        if (cambios.getContactoEmergencia() != null && !cambios.getContactoEmergencia().isBlank()) {
            base.setContactoEmergencia(cambios.getContactoEmergencia().trim());
        }
        if (cambios.getTipoSangre() != null && !cambios.getTipoSangre().isBlank()) {
            base.setTipoSangre(cambios.getTipoSangre().trim());
        }
        if (cambios.getAlergia() != null && !cambios.getAlergia().isBlank()) {
            base.setAlergia(cambios.getAlergia().trim());
        }
        if (cambios.getFechaNacimiento() != null) {
            base.setFechaNacimiento(cambios.getFechaNacimiento());
        }

        return base;
    }

    private static String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static void validarNoNull(Object o, String nombreCampo) {
        if (o == null) throw new ValidacionException(nombreCampo + " no puede ser null");
    }

    private static void validarTextoNoVacio(String s, String nombreCampo) {
        if (s == null || s.isBlank()) {
            throw new ValidacionException(nombreCampo + " es obligatorio");
        }
    }

    // ------------------ Excepciones de dominio ------------------

    public static class ValidacionException extends RuntimeException {
        public ValidacionException(String msg) { super(msg); }
    }

    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String msg) { super(msg); }
    }

    public static class ConflictoDeDatosException extends RuntimeException {
        public ConflictoDeDatosException(String msg) { super(msg); }
    }
}