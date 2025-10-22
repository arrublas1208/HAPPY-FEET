package com.happyfeet.model.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Mascota {
    private Integer id;
    private Integer duenoId;
    private String nombre;
    private Integer razaId;
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private String color;
    private String seniasParticulares;
    private String urlFoto;
    private String alergias;
    private String condicionesPreexistentes;
    private Double pesoActual;
    private String historialVacunas;
    private String procedimientosPrevios;
    private String microchip;
    private LocalDate fechaImplantacionMicrochip;
    private Boolean agresivo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    private Mascota() {} // Constructor privado

    public enum Sexo {
        MACHO, HEMBRA
    }

    public static class Builder {
        private Mascota mascota;

        private Builder() {
            mascota = new Mascota();
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withId(Integer id) {
            mascota.id = id;
            return this;
        }

        public Builder withDuenoId(Integer duenoId) {
            mascota.duenoId = duenoId;
            return this;
        }

        public Builder withNombre(String nombre) {
            mascota.nombre = nombre;
            return this;
        }

        public Builder withRazaId(Integer razaId) {
            mascota.razaId = razaId;
            return this;
        }

        public Builder withFechaNacimiento(LocalDate fechaNacimiento) {
            mascota.fechaNacimiento = fechaNacimiento;
            return this;
        }

        public Builder withSexo(Sexo sexo) {
            mascota.sexo = sexo;
            return this;
        }

        public Builder withColor(String color) {
            mascota.color = color;
            return this;
        }

        public Builder withSeniasParticulares(String seniasParticulares) {
            mascota.seniasParticulares = seniasParticulares;
            return this;
        }

        public Builder withUrlFoto(String urlFoto) {
            mascota.urlFoto = urlFoto;
            return this;
        }

        public Builder withAlergias(String alergias) {
            mascota.alergias = alergias;
            return this;
        }

        public Builder withCondicionesPreexistentes(String condicionesPreexistentes) {
            mascota.condicionesPreexistentes = condicionesPreexistentes;
            return this;
        }

        public Builder withPesoActual(Double pesoActual) {
            mascota.pesoActual = pesoActual;
            return this;
        }

        public Builder withHistorialVacunas(String historialVacunas) {
            mascota.historialVacunas = historialVacunas;
            return this;
        }

        public Builder withProcedimientosPrevios(String procedimientosPrevios) {
            mascota.procedimientosPrevios = procedimientosPrevios;
            return this;
        }

        public Builder withMicrochip(String microchip) {
            mascota.microchip = microchip;
            return this;
        }

        public Builder withFechaImplantacionMicrochip(LocalDate fechaImplantacionMicrochip) {
            mascota.fechaImplantacionMicrochip = fechaImplantacionMicrochip;
            return this;
        }

        public Builder withAgresivo(Boolean agresivo) {
            mascota.agresivo = agresivo;
            return this;
        }

        public Builder withFechaRegistro(LocalDateTime fechaRegistro) {
            mascota.fechaRegistro = fechaRegistro;
            return this;
        }

        public Builder withFechaActualizacion(LocalDateTime fechaActualizacion) {
            mascota.fechaActualizacion = fechaActualizacion;
            return this;
        }

        public Mascota build() {
            Objects.requireNonNull(mascota.nombre, "Nombre de mascota es requerido");
            Objects.requireNonNull(mascota.duenoId, "ID del dueño es requerido");
            Objects.requireNonNull(mascota.sexo, "Sexo de la mascota es requerido");

            if (mascota.fechaRegistro == null) {
                mascota.fechaRegistro = LocalDateTime.now();
            }
            mascota.fechaActualizacion = LocalDateTime.now();

            return mascota;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDuenoId() { return duenoId; }
    public void setDuenoId(Integer duenoId) {
        this.duenoId = Objects.requireNonNull(duenoId, "DuenoId es requerido");
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
    }

    public Integer getRazaId() { return razaId; }
    public void setRazaId(Integer razaId) {
        this.razaId = Objects.requireNonNull(razaId, "RazaId es requerido");
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) {
        this.sexo = Objects.requireNonNull(sexo, "Sexo no puede ser nulo");
    }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSeniasParticulares() { return seniasParticulares; }
    public void setSeniasParticulares(String seniasParticulares) { this.seniasParticulares = seniasParticulares; }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getCondicionesPreexistentes() { return condicionesPreexistentes; }
    public void setCondicionesPreexistentes(String condicionesPreexistentes) { this.condicionesPreexistentes = condicionesPreexistentes; }

    public Double getPesoActual() { return pesoActual; }
    public void setPesoActual(Double pesoActual) { this.pesoActual = pesoActual; }

    public String getHistorialVacunas() { return historialVacunas; }
    public void setHistorialVacunas(String historialVacunas) { this.historialVacunas = historialVacunas; }

    public String getProcedimientosPrevios() { return procedimientosPrevios; }
    public void setProcedimientosPrevios(String procedimientosPrevios) { this.procedimientosPrevios = procedimientosPrevios; }

    public String getMicrochip() { return microchip; }
    public void setMicrochip(String microchip) { this.microchip = microchip; }

    public LocalDate getFechaImplantacionMicrochip() { return fechaImplantacionMicrochip; }
    public void setFechaImplantacionMicrochip(LocalDate fechaImplantacionMicrochip) {
        this.fechaImplantacionMicrochip = fechaImplantacionMicrochip;
    }

    public Boolean getAgresivo() { return agresivo; }
    public void setAgresivo(Boolean agresivo) { this.agresivo = agresivo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = Objects.requireNonNull(fechaRegistro, "Fecha de registro es requerida");
    }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = Objects.requireNonNull(fechaActualizacion, "Fecha de actualización es requerida");
    }

    @Override
    public String toString() {
        return String.format("""
            Mascota: %s
            Sexo: %s
            Peso: %.2f kg
            Microchip: %s
            """, nombre, sexo, pesoActual != null ? pesoActual : 0.0,
                microchip != null ? microchip : "No registrado");
    }
}
