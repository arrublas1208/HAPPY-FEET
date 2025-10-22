package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Mascota;
import com.happyfeet.repository.MascotaRepository;
import com.happyfeet.service.MascotaService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository) {
        this.mascotaRepository = Objects.requireNonNull(mascotaRepository, "mascotaRepository no puede ser null");
    }

    public Mascota crearMascota(Mascota mascota) {
        validarNoNull(mascota, "mascota");
        validarNegocio(mascota);
        if (mascota.getMicrochip() != null && !mascota.getMicrochip().isBlank() && existePorMicrochip(mascota.getMicrochip())) {
            throw new ConflictoDeDatosException("Ya existe una mascota con microchip " + mascota.getMicrochip());
        }
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizarMascota(Long id, Mascota cambios) {
        validarNoNull(id, "id");
        validarNoNull(cambios, "cambios");
        Mascota existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota con id " + id + " no existe"));

        // Validar unicidad de microchip si cambia
        if (cambios.getMicrochip() != null && !cambios.getMicrochip().isBlank() &&
                !cambios.getMicrochip().equals(existente.getMicrochip())) {
            if (existePorMicrochip(cambios.getMicrochip())) {
                throw new ConflictoDeDatosException("Ya existe una mascota con microchip " + cambios.getMicrochip());
            }
        }

        Mascota actualizado = mergeMascota(existente, cambios);
        validarNegocio(actualizado);
        return mascotaRepository.update(actualizado);
    }

    public void eliminarMascota(Long id) {
        validarNoNull(id, "id");
        if (!mascotaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Mascota con id " + id + " no existe");
        }
        mascotaRepository.deleteById(id);
    }

    public Optional<Mascota> buscarPorId(Long id) {
        validarNoNull(id, "id");
        return mascotaRepository.findById(id);
    }

    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll().stream()
                .sorted(Comparator.comparing(Mascota::getNombre).thenComparing(Mascota::getId))
                .toList();
    }

    public List<Mascota> buscarPorDueno(Long duenoId) {
        validarNoNull(duenoId, "duenoId");
        return mascotaRepository.findByDuenoId(duenoId);
    }

    @Override
    public List<Mascota> buscarPorNombre(String termino) {
        String filtro = normalizar(termino);
        Predicate<Mascota> nombreMatch = m -> normalizar(m.getNombre()).contains(filtro);
        return mascotaRepository.findAll().stream()
                .filter(nombreMatch)
                .sorted(Comparator.comparing(Mascota::getNombre))
                .toList();
    }

    @Override
    public boolean existePorMicrochip(String microchip) {
        if (microchip == null) return false;
        Mascota m = mascotaRepository.findByMicrochip(microchip.trim());
        return m != null;
    }

    // ---------- Validaciones y merge ----------
    private void validarNegocio(Mascota m) {
        validarTextoNoVacio(m.getNombre(), "nombre");
        validarNoNull(m.getDuenoId(), "duenoId");
        validarNoNull(m.getSexo(), "sexo");
        if (m.getMicrochip() != null && m.getMicrochip().length() > 50) {
            throw new ValidacionException("microchip excede longitud permitida");
        }
        if (m.getUrlFoto() != null && m.getUrlFoto().length() > 255) {
            throw new ValidacionException("urlFoto excede longitud permitida");
        }
    }

    private static Mascota mergeMascota(Mascota base, Mascota cambios) {
        if (cambios.getNombre() != null && !cambios.getNombre().isBlank()) {
            base.setNombre(cambios.getNombre().trim());
        }
        if (cambios.getRazaId() != null) {
            base.setRazaId(cambios.getRazaId());
        }
        if (cambios.getFechaNacimiento() != null) {
            base.setFechaNacimiento(cambios.getFechaNacimiento());
        }
        if (cambios.getSexo() != null) {
            base.setSexo(cambios.getSexo());
        }
        if (cambios.getColor() != null && !cambios.getColor().isBlank()) {
            base.setColor(cambios.getColor());
        }
        if (cambios.getSeniasParticulares() != null && !cambios.getSeniasParticulares().isBlank()) {
            base.setSeniasParticulares(cambios.getSeniasParticulares());
        }
        if (cambios.getUrlFoto() != null && !cambios.getUrlFoto().isBlank()) {
            base.setUrlFoto(cambios.getUrlFoto());
        }
        if (cambios.getAlergias() != null && !cambios.getAlergias().isBlank()) {
            base.setAlergias(cambios.getAlergias());
        }
        if (cambios.getCondicionesPreexistentes() != null && !cambios.getCondicionesPreexistentes().isBlank()) {
            base.setCondicionesPreexistentes(cambios.getCondicionesPreexistentes());
        }
        if (cambios.getPesoActual() != null) {
            base.setPesoActual(cambios.getPesoActual());
        }
        if (cambios.getMicrochip() != null && !cambios.getMicrochip().isBlank()) {
            base.setMicrochip(cambios.getMicrochip().trim());
        }
        if (cambios.getFechaImplantacionMicrochip() != null) {
            base.setFechaImplantacionMicrochip(cambios.getFechaImplantacionMicrochip());
        }
        if (cambios.getAgresivo() != null) {
            base.setAgresivo(cambios.getAgresivo());
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

    // Excepciones de dominio (similares a las usadas en DuenoServiceImpl)
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
