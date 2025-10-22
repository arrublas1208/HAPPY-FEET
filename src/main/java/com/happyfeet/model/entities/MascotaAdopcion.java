package com.happyfeet.model.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una mascota disponible para adopción.
 */
public class MascotaAdopcion {
    private Integer id;
    private String nombre;
    private String especie;
    private String raza;
    private Integer edadMeses;
    private Sexo sexo;
    private String descripcion;
    private String necesidadesEspeciales;
    private LocalDate fechaIngreso;
    private Boolean adoptada;
    private String contactoResponsable;
    private String fotoUrl;
    private Integer duenoAdoptanteId;
    private LocalDate fechaAdopcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Referencia al dueño adoptante
    private Dueno duenoAdoptante;

    public enum Sexo {
        MACHO("Macho"),
        HEMBRA("Hembra");

        private final String nombre;

        Sexo(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        public static Sexo fromString(String sexo) {
            for (Sexo s : Sexo.values()) {
                if (s.nombre.equalsIgnoreCase(sexo)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Sexo no válido: " + sexo);
        }
    }

    public MascotaAdopcion() {
        this.fechaIngreso = LocalDate.now();
        this.adoptada = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Builder Pattern
    public static class Builder {
        private final MascotaAdopcion mascota;

        public Builder() {
            this.mascota = new MascotaAdopcion();
        }

        public Builder withNombre(String nombre) {
            mascota.nombre = Objects.requireNonNull(nombre, "Nombre es requerido");
            return this;
        }

        public Builder withEspecie(String especie) {
            mascota.especie = Objects.requireNonNull(especie, "Especie es requerida");
            return this;
        }

        public Builder withRaza(String raza) {
            mascota.raza = raza;
            return this;
        }

        public Builder withEdadMeses(Integer edadMeses) {
            mascota.edadMeses = Objects.requireNonNull(edadMeses, "Edad es requerida");
            if (edadMeses < 0) {
                throw new IllegalArgumentException("Edad no puede ser negativa");
            }
            return this;
        }

        public Builder withSexo(Sexo sexo) {
            mascota.sexo = Objects.requireNonNull(sexo, "Sexo es requerido");
            return this;
        }

        public Builder withDescripcion(String descripcion) {
            mascota.descripcion = descripcion;
            return this;
        }

        public Builder withNecesidadesEspeciales(String necesidades) {
            mascota.necesidadesEspeciales = necesidades;
            return this;
        }

        public Builder withContactoResponsable(String contacto) {
            mascota.contactoResponsable = contacto;
            return this;
        }

        public Builder withFotoUrl(String url) {
            mascota.fotoUrl = url;
            return this;
        }

        public MascotaAdopcion build() {
            if (mascota.nombre == null || mascota.nombre.trim().isEmpty()) {
                throw new IllegalStateException("Nombre es requerido");
            }
            if (mascota.especie == null || mascota.especie.trim().isEmpty()) {
                throw new IllegalStateException("Especie es requerida");
            }
            if (mascota.edadMeses == null) {
                throw new IllegalStateException("Edad es requerida");
            }
            if (mascota.sexo == null) {
                throw new IllegalStateException("Sexo es requerido");
            }
            return mascota;
        }
    }

    // Métodos de negocio
    public void marcarComoAdoptada(Integer duenoId) {
        this.adoptada = true;
        this.duenoAdoptanteId = duenoId;
        this.fechaAdopcion = LocalDate.now();
    }

    public boolean estaDisponible() {
        return !adoptada;
    }

    public String getEdadFormateada() {
        if (edadMeses == null) return "N/A";
        if (edadMeses < 12) {
            return edadMeses + " meses";
        } else {
            int años = edadMeses / 12;
            int meses = edadMeses % 12;
            return años + " año" + (años > 1 ? "s" : "") +
                   (meses > 0 ? " y " + meses + " mes" + (meses > 1 ? "es" : "") : "");
        }
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Integer getEdadMeses() {
        return edadMeses;
    }

    public void setEdadMeses(Integer edadMeses) {
        this.edadMeses = edadMeses;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNecesidadesEspeciales() {
        return necesidadesEspeciales;
    }

    public void setNecesidadesEspeciales(String necesidadesEspeciales) {
        this.necesidadesEspeciales = necesidadesEspeciales;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Boolean getAdoptada() {
        return adoptada;
    }

    public void setAdoptada(Boolean adoptada) {
        this.adoptada = adoptada;
    }

    public String getContactoResponsable() {
        return contactoResponsable;
    }

    public void setContactoResponsable(String contactoResponsable) {
        this.contactoResponsable = contactoResponsable;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Integer getDuenoAdoptanteId() {
        return duenoAdoptanteId;
    }

    public void setDuenoAdoptanteId(Integer duenoAdoptanteId) {
        this.duenoAdoptanteId = duenoAdoptanteId;
    }

    public LocalDate getFechaAdopcion() {
        return fechaAdopcion;
    }

    public void setFechaAdopcion(LocalDate fechaAdopcion) {
        this.fechaAdopcion = fechaAdopcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Dueno getDuenoAdoptante() {
        return duenoAdoptante;
    }

    public void setDuenoAdoptante(Dueno duenoAdoptante) {
        this.duenoAdoptante = duenoAdoptante;
        if (duenoAdoptante != null) {
            this.duenoAdoptanteId = duenoAdoptante.getId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MascotaAdopcion that = (MascotaAdopcion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s (%s) - %s",
                nombre, especie, raza != null ? raza : "Mestizo",
                getEdadFormateada(),
                adoptada ? "ADOPTADA" : "DISPONIBLE");
    }

    public static Builder builder() {
        return new Builder();
    }
}