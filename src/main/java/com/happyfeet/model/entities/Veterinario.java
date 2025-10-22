package com.happyfeet.model.entities;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


public class Veterinario {
    private Integer id;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String especialidad;
    private String telefono;
    private String email;
    private String direccion;
    private LocalDate fechaContratacion;
    private BigDecimal salario;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;


    private Veterinario() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }


    public static class Builder {
        private final Veterinario veterinario;

        public Builder() {
            this.veterinario = new Veterinario();
        }


        public Builder withNombreCompleto(String nombreCompleto) {
            veterinario.nombreCompleto = Objects.requireNonNull(nombreCompleto,
                    "Nombre completo no puede ser nulo");
            if (nombreCompleto.trim().isEmpty()) {
                throw new IllegalArgumentException("Nombre completo no puede estar vacío");
            }
            return this;
        }


        public Builder withDocumentoIdentidad(String documentoIdentidad) {
            veterinario.documentoIdentidad = Objects.requireNonNull(documentoIdentidad,
                    "Documento de identidad no puede ser nulo");
            if (documentoIdentidad.trim().isEmpty()) {
                throw new IllegalArgumentException("Documento de identidad no puede estar vacío");
            }
            return this;
        }


        public Builder withEmail(String email) {
            veterinario.email = Objects.requireNonNull(email, "Email no puede ser nulo");
            if (!email.contains("@")) {
                throw new IllegalArgumentException("Email debe contener @: " + email);
            }
            return this;
        }


        public Builder withEspecialidad(String especialidad) {
            veterinario.especialidad = especialidad;
            return this;
        }


        public Builder withTelefono(String telefono) {
            veterinario.telefono = telefono;
            return this;
        }


        public Builder withFechaContratacion(LocalDate fechaContratacion) {
            if (fechaContratacion != null && fechaContratacion.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Fecha de contratación no puede ser futura");
            }
            veterinario.fechaContratacion = fechaContratacion;
            return this;
        }


        public Builder withSalario(BigDecimal salario) {
            if (salario != null && salario.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Salario no puede ser negativo");
            }
            veterinario.salario = salario;
            return this;
        }


        public Veterinario build() {
            // Validar campos requeridos
            if (veterinario.nombreCompleto == null || veterinario.nombreCompleto.trim().isEmpty()) {
                throw new IllegalStateException("Nombre completo es requerido");
            }
            if (veterinario.documentoIdentidad == null || veterinario.documentoIdentidad.trim().isEmpty()) {
                throw new IllegalStateException("Documento de identidad es requerido");
            }
            if (veterinario.email == null || veterinario.email.trim().isEmpty()) {
                throw new IllegalStateException("Email es requerido");
            }


            if (!isEmailValido(veterinario.email)) {
                throw new IllegalArgumentException("Formato de email inválido: " + veterinario.email);
            }

            return veterinario;
        }


        private boolean isEmailValido(String email) {
            return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto,
                "Nombre completo no puede ser nulo");
        if (nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre completo no puede estar vacío");
        }
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = Objects.requireNonNull(documentoIdentidad,
                "Documento de identidad no puede ser nulo");
        if (documentoIdentidad.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento de identidad no puede estar vacío");
        }
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email no puede ser nulo");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email debe contener @: " + email);
        }
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        if (fechaContratacion != null && fechaContratacion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de contratación no puede ser futura");
        }
        this.fechaContratacion = fechaContratacion;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        if (salario != null && salario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salario no puede ser negativo");
        }
        this.salario = salario;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo != null ? activo : true;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // ============ MÉTODOS DE NEGOCIO ============


    public int calcularAntiguedad() {
        if (fechaContratacion == null) {
            return 0;
        }
        return LocalDate.now().getYear() - fechaContratacion.getYear();
    }


    public boolean estaActivo() {
        return Boolean.TRUE.equals(activo);
    }


    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }


    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ============ MÉTODOS SOBRESCRITOS ============


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Veterinario that = (Veterinario) obj;

        return Objects.equals(documentoIdentidad, that.documentoIdentidad) ||
                Objects.equals(email, that.email);
    }


    @Override
    public int hashCode() {
        return Objects.hash(documentoIdentidad, email);
    }


    @Override
    public String toString() {
        return String.format("""
            🩺 VETERINARIO
            ID: %d
            Nombre: %s
            Documento: %s
            Especialidad: %s
            Email: %s
            Teléfono: %s
            Estado: %s
            Antigüedad: %d años
            """,
                id != null ? id : "N/A",
                nombreCompleto,
                documentoIdentidad,
                especialidad != null ? especialidad : "No especificada",
                email,
                telefono != null ? telefono : "No registrado",
                estaActivo() ? "✅ Activo" : "❌ Inactivo",
                calcularAntiguedad()
        );
    }


    public static Builder builder() {
        return new Builder();
    }


    public static Veterinario crearEjemplo() {
        return new Builder()
                .withNombreCompleto("Dr. Carlos Rodríguez")
                .withDocumentoIdentidad("11223344")
                .withEmail("carlos.rodriguez@happyfeet.com")
                .withEspecialidad("Cirugía")
                .withTelefono("555-1234")
                .withFechaContratacion(LocalDate.of(2020, 3, 15))
                .withSalario(new BigDecimal("50000.00"))
                .build();
    }
}
