package com.happyfeet.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;


public class Raza {
    private Integer id;
    private final Integer especieId;  // Inmutable después de la creación
    private String nombre;
    private String caracteristicas;
    private LocalDateTime fechaCreacion;

    // Referencia a la entidad Especie (composición)
    private Especie especie;

    // Constructor privado para forzar uso del Builder
    private Raza(Integer especieId) {
        this.especieId = Objects.requireNonNull(especieId, "ID de especie es requerido");
        this.fechaCreacion = LocalDateTime.now();
    }


    public static class Builder {
        private final Integer especieId;
        private String nombre;
        private String caracteristicas;
        private Especie especie;


        public Builder(Integer especieId) {
            this.especieId = Objects.requireNonNull(especieId, "ID de especie es requerido");
            if (especieId <= 0) {
                throw new IllegalArgumentException("ID de especie debe ser positivo");
            }
        }


        public Builder(Especie especie) {
            Objects.requireNonNull(especie, "Especie no puede ser nula");
            Objects.requireNonNull(especie.getId(), "Especie debe tener ID válido");
            this.especieId = especie.getId();
            this.especie = especie;
        }


        public Builder withNombre(String nombre) {
            this.nombre = Objects.requireNonNull(nombre, "Nombre de raza no puede ser nulo");
            if (nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("Nombre de raza no puede estar vacío");
            }
            return this;
        }


        public Builder withCaracteristicas(String caracteristicas) {
            this.caracteristicas = caracteristicas;
            return this;
        }

        public Builder withEspecie(Especie especie) {
            this.especie = Objects.requireNonNull(especie, "Especie no puede ser nula");
            // Validar consistencia entre especieId y objeto especie
            if (!especieId.equals(especie.getId())) {
                throw new IllegalArgumentException(
                        "ID de especie del builder no coincide con el ID del objeto especie");
            }
            return this;
        }


        public Raza build() {
            // Validar campos requeridos
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalStateException("Nombre de raza es requerido");
            }

            Raza raza = new Raza(especieId);
            raza.nombre = this.nombre.trim();
            raza.caracteristicas = this.caracteristicas;
            raza.especie = this.especie;

            return raza;
        }
    }

    // ============ GETTERS Y SETTERS CON VALIDACIONES ============

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID debe ser positivo: " + id);
        }
        this.id = id;
    }


    public Integer getEspecieId() {
        return especieId;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        if (nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        // Validar integridad referencial
        Objects.requireNonNull(especie, "Especie no puede ser nula");
        if (!especieId.equals(especie.getId())) {
            throw new IllegalArgumentException(
                    "La especie asignada no coincide con el especieId de la raza");
        }
        this.especie = especie;
    }

    // ============ MÉTODOS DE NEGOCIO Y UTILITARIOS ============


    public boolean tieneCaracteristicas() {
        return caracteristicas != null && !caracteristicas.trim().isEmpty();
    }


    public String obtenerResumenCaracteristicas() {
        if (!tieneCaracteristicas()) {
            return "No hay características definidas para esta raza";
        }

        return caracteristicas.length() > 100
                ? caracteristicas.substring(0, 100) + "..."
                : caracteristicas;
    }


    public boolean esValida() {
        return nombre != null && !nombre.trim().isEmpty() &&
                especieId != null && especieId > 0;
    }


    public String getNombreEspecie() {
        return especie != null ? especie.getNombre() : "Especie no cargada";
    }


    public boolean perteneceAEspecie(Integer especieId) {
        return this.especieId.equals(especieId);
    }


    public boolean perteneceAEspecie(String nombreEspecie) {
        if (especie == null || nombreEspecie == null) return false;
        return especie.getNombre().equalsIgnoreCase(nombreEspecie.trim());
    }

    // ============ MÉTODOS SOBRESCRITOS ============

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Raza otraRaza = (Raza) obj;

        return Objects.equals(nombre, otraRaza.nombre) &&
                Objects.equals(especieId, otraRaza.especieId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(nombre, especieId);
    }

    @Override
    public String toString() {
        return String.format("""
            🐾 RAZA
            ID: %d
            Nombre: %s
            Especie: %s
            Características: %s
            Fecha Creación: %s
            """,
                id != null ? id : "N/A",
                nombre,
                getNombreEspecie(),
                obtenerResumenCaracteristicas(),
                fechaCreacion != null ? fechaCreacion.toLocalDate() : "N/A"
        );
    }

    // ============ MÉTODOS ESTÁTICOS Y FACTORY METHODS ============


    public static Raza of(Integer especieId, String nombre) {
        return new Builder(especieId)
                .withNombre(nombre)
                .build();
    }


    public static Raza of(Especie especie, String nombre) {
        return new Builder(especie)
                .withNombre(nombre)
                .build();
    }


    public static Raza of(Integer especieId, String nombre, String caracteristicas) {
        return new Builder(especieId)
                .withNombre(nombre)
                .withCaracteristicas(caracteristicas)
                .build();
    }


    public static Raza crearEjemploLabrador(Integer especieId) {
        return new Builder(especieId)
                .withNombre("Labrador Retriever")
                .withCaracteristicas("""
                Raza de perro conocida por su inteligencia, temperamento amigable y energía. 
                Excelente para familias, fácil de entrenar. Peso: 25-36 kg, Altura: 55-62 cm.
                Esperanza de vida: 10-12 años. Propenso a displasia de cadera.
                """)
                .build();
    }


    public static Raza crearEjemploSiames(Integer especieId) {
        return new Builder(especieId)
                .withNombre("Siamés")
                .withCaracteristicas("""
                Gato elegante y vocal, de cuerpo delgado y ojos azules almendrados. 
                Muy inteligente y afectuoso con su familia. Peso: 3-5 kg.
                Esperanza de vida: 12-15 años. Propenso a problemas respiratorios.
                """)
                .build();
    }
}
