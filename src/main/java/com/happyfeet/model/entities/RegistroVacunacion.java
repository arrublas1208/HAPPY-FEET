package com.happyfeet.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un registro de vacunación en una jornada.
 */
public class RegistroVacunacion {
    private Integer id;
    private Integer jornadaId;
    private String nombreMascota;
    private String nombreDueno;
    private String telefono;
    private String vacunaAplicada;
    private LocalDateTime fechaHora;
    private String observaciones;

    // Referencia a la jornada
    private JornadaVacunacion jornada;

    public RegistroVacunacion() {
        this.fechaHora = LocalDateTime.now();
    }

    public RegistroVacunacion(String nombreMascota, String nombreDueno, String telefono, String vacunaAplicada) {
        this();
        this.nombreMascota = nombreMascota;
        this.nombreDueno = nombreDueno;
        this.telefono = telefono;
        this.vacunaAplicada = vacunaAplicada;
    }

    // Builder Pattern
    public static class Builder {
        private final RegistroVacunacion registro;

        public Builder() {
            this.registro = new RegistroVacunacion();
        }

        public Builder withJornadaId(Integer jornadaId) {
            registro.jornadaId = Objects.requireNonNull(jornadaId, "ID de jornada es requerido");
            return this;
        }

        public Builder withNombreMascota(String nombre) {
            registro.nombreMascota = Objects.requireNonNull(nombre, "Nombre de mascota es requerido");
            return this;
        }

        public Builder withNombreDueno(String nombre) {
            registro.nombreDueno = Objects.requireNonNull(nombre, "Nombre de dueño es requerido");
            return this;
        }

        public Builder withTelefono(String telefono) {
            registro.telefono = telefono;
            return this;
        }

        public Builder withVacunaAplicada(String vacuna) {
            registro.vacunaAplicada = Objects.requireNonNull(vacuna, "Vacuna aplicada es requerida");
            return this;
        }

        public Builder withObservaciones(String observaciones) {
            registro.observaciones = observaciones;
            return this;
        }

        public Builder withFechaHora(LocalDateTime fechaHora) {
            registro.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
            return this;
        }

        public RegistroVacunacion build() {
            if (registro.jornadaId == null) {
                throw new IllegalStateException("ID de jornada es requerido");
            }
            if (registro.nombreMascota == null || registro.nombreMascota.trim().isEmpty()) {
                throw new IllegalStateException("Nombre de mascota es requerido");
            }
            if (registro.nombreDueno == null || registro.nombreDueno.trim().isEmpty()) {
                throw new IllegalStateException("Nombre de dueño es requerido");
            }
            if (registro.vacunaAplicada == null || registro.vacunaAplicada.trim().isEmpty()) {
                throw new IllegalStateException("Vacuna aplicada es requerida");
            }
            return registro;
        }
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJornadaId() {
        return jornadaId;
    }

    public void setJornadaId(Integer jornadaId) {
        this.jornadaId = jornadaId;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombreDueno() {
        return nombreDueno;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getVacunaAplicada() {
        return vacunaAplicada;
    }

    public void setVacunaAplicada(String vacunaAplicada) {
        this.vacunaAplicada = vacunaAplicada;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public JornadaVacunacion getJornada() {
        return jornada;
    }

    public void setJornada(JornadaVacunacion jornada) {
        this.jornada = jornada;
        if (jornada != null) {
            this.jornadaId = jornada.getId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroVacunacion that = (RegistroVacunacion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Vacunación: %s - %s - %s (%s)",
                nombreMascota, vacunaAplicada, nombreDueno,
                fechaHora != null ? fechaHora.toLocalDate().toString() : "N/A");
    }

    public static Builder builder() {
        return new Builder();
    }
}