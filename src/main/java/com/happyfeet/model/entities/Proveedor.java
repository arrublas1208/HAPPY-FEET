package com.happyfeet.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad Proveedor para gestión de proveedores del inventario.
 * Requerida según especificaciones del módulo de Inventario.
 */
public class Proveedor {
    private Integer id;
    private String nombre;
    private String nombreContacto;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private String nit;
    private TipoProveedor tipo;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public enum TipoProveedor {
        MEDICAMENTOS("Medicamentos", "Proveedor de medicamentos veterinarios"),
        VACUNAS("Vacunas", "Proveedor especializado en vacunas"),
        MATERIAL_MEDICO("Material Médico", "Instrumental y material médico"),
        ALIMENTOS("Alimentos", "Alimentos y suplementos para mascotas"),
        GENERAL("General", "Proveedor de productos generales");

        private final String nombre;
        private final String descripcion;

        TipoProveedor(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
    }

    // Constructor privado para Builder
    private Proveedor() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Builder Pattern
    public static class Builder {
        private final Proveedor proveedor;

        public Builder() {
            this.proveedor = new Proveedor();
        }

        public Builder withNombre(String nombre) {
            proveedor.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser null");
            return this;
        }

        public Builder withNombreContacto(String nombreContacto) {
            proveedor.nombreContacto = nombreContacto;
            return this;
        }

        public Builder withTelefono(String telefono) {
            proveedor.telefono = telefono;
            return this;
        }

        public Builder withEmail(String email) {
            proveedor.email = email;
            return this;
        }

        public Builder withDireccion(String direccion) {
            proveedor.direccion = direccion;
            return this;
        }

        public Builder withCiudad(String ciudad) {
            proveedor.ciudad = ciudad;
            return this;
        }

        public Builder withNit(String nit) {
            proveedor.nit = nit;
            return this;
        }

        public Builder withTipo(TipoProveedor tipo) {
            proveedor.tipo = tipo;
            return this;
        }

        public Builder withActivo(boolean activo) {
            proveedor.activo = activo;
            return this;
        }

        public Proveedor build() {
            // Validaciones
            if (proveedor.nombre == null || proveedor.nombre.trim().isEmpty()) {
                throw new IllegalStateException("Nombre del proveedor es requerido");
            }
            if (proveedor.tipo == null) {
                proveedor.tipo = TipoProveedor.GENERAL;
            }
            return proveedor;
        }
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser null");
    }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public TipoProveedor getTipo() { return tipo; }
    public void setTipo(TipoProveedor tipo) { this.tipo = tipo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) {
        this.activo = activo;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @Override
    public String toString() {
        return String.format("Proveedor{id=%d, nombre='%s', tipo=%s, activo=%s}",
                id, nombre, tipo, activo);
    }

    public static Builder builder() {
        return new Builder();
    }
}