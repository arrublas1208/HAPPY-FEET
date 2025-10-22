package com.happyfeet.model.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Dueno {
    private Integer id;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String direccion;
    private String telefono;
    private String email;
    private String contactoEmergencia;
    private LocalDate fechaNacimiento;
    private String tipoSangre;
    private String alergia;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    private Dueno() {} // constructor privado

    public String getTelefono() {
        return telefono;

    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public static class Builder {
        private Dueno dueno;

        private Builder() {
            dueno = new Dueno();
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withId(Integer id) {
            dueno.id = id;
            return this;
        }

        public Builder withNombreCompleto(String nombreCompleto) {
            dueno.nombreCompleto = nombreCompleto;
            return this;
        }

        public Builder withDocumentoIdentidad(String documentoIdentidad) {
            dueno.documentoIdentidad = documentoIdentidad;
            return this;
        }

        public Builder withDireccion(String direccion) {
            dueno.direccion = direccion;
            return this;
        }

        public Builder withTelefono(String telefono) {
            dueno.telefono = telefono;
            return this;
        }

        public Builder withEmail(String email) {
            dueno.email = email;
            return this;
        }

        public Builder withContactoEmergencia(String contactoEmergencia) {
            dueno.contactoEmergencia = contactoEmergencia;
            return this;
        }

        public Builder withFechaNacimiento(LocalDate fechaNacimiento) {
            dueno.fechaNacimiento = fechaNacimiento;
            return this;
        }

        public Builder withTipoSangre(String tipoSangre) {
            dueno.tipoSangre = tipoSangre;
            return this;
        }

        public Builder withAlergia(String alergia) {
            dueno.alergia = alergia;
            return this;
        }

        public Builder withFechaRegistro(LocalDateTime fechaRegistro) {
            dueno.fechaRegistro = fechaRegistro;
            return this;
        }

        public Builder withFechaActualizacion(LocalDateTime fechaActualizacion) {
            dueno.fechaActualizacion = fechaActualizacion;
            return this;
        }

        public Dueno build() {
            Objects.requireNonNull(dueno.nombreCompleto, "Nombre completo es requerido");
            Objects.requireNonNull(dueno.documentoIdentidad, "Documento Identidad es requerido");
            Objects.requireNonNull(dueno.email, "Email es requerido");

            if (dueno.fechaRegistro == null) {
                dueno.fechaRegistro = LocalDateTime.now();
            }
            dueno.fechaActualizacion = LocalDateTime.now();

            return dueno;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "Nombre completo no puede ser nulo");
    }

    // Métodos de compatibilidad con servicios que usan nombre y apellido por separado
    public String getNombre() {
        String[] partes = dividirNombreCompleto();
        return partes[0];
    }

    public void setNombre(String nombre) {
        Objects.requireNonNull(nombre, "nombre no puede ser nulo");
        String[] partes = dividirNombreCompleto();
        String apellido = partes[1];
        this.nombreCompleto = (nombre + " " + apellido).trim();
    }

    public String getApellido() {
        String[] partes = dividirNombreCompleto();
        return partes[1];
    }

    public void setApellido(String apellido) {
        Objects.requireNonNull(apellido, "apellido no puede ser nulo");
        String[] partes = dividirNombreCompleto();
        String nombre = partes[0];
        this.nombreCompleto = (nombre + " " + apellido).trim();
    }

    private String[] dividirNombreCompleto() {
        String nc = this.nombreCompleto == null ? "" : this.nombreCompleto.trim();
        if (nc.isEmpty()) return new String[]{"", ""};
        int idx = nc.indexOf(' ');
        if (idx < 0) return new String[]{nc, ""};
        String nombre = nc.substring(0, idx).trim();
        String apellido = nc.substring(idx + 1).trim();
        return new String[]{nombre, apellido};
    }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = Objects.requireNonNull(documentoIdentidad, "Documento de identidad no puede ser nulo");
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        Objects.requireNonNull(email, "Email no puede ser nulo");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Formato de email no valido");
        }
        this.email = email;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) {
        this.direccion = Objects.requireNonNull(direccion, "Direccion es requerido");
    }

    public String getContactoEmergencia() { return contactoEmergencia; }
    public void setContactoEmergencia(String contactoEmergencia) {
        this.contactoEmergencia = Objects.requireNonNull(contactoEmergencia, "Contacto de emergencia es requerido");
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipoSangre() { return tipoSangre; }
    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = Objects.requireNonNull(tipoSangre, "Tipo de sangre es requerido");
    }

    public String getAlergia() { return alergia; }
    public void setAlergia(String alergia) {
        this.alergia = Objects.requireNonNull(alergia, "Alergia es requerido");
    }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = Objects.requireNonNull(fechaRegistro, "Fecha de registro es requerido");
    }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = Objects.requireNonNull(fechaActualizacion, "Fecha de actualizacion es requerido");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dueno dueno = (Dueno) o;
        return Objects.equals(documentoIdentidad, dueno.documentoIdentidad) &&
                Objects.equals(email, dueno.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentoIdentidad, email);
    }

    @Override
    public String toString() {
        return String.format("""
            |********************************
            |   Dueño:          %s
            |   Documento:      %s
            |   Email:          %s
            |   Teléfono:       %s
            """, nombreCompleto, documentoIdentidad, email, telefono);
    }
}
