package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.VeterinarioRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VeterinarioRepositoryImpl implements VeterinarioRepository {
    private final DatabaseConnection db;

    public VeterinarioRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Veterinario save(Veterinario veterinario) {
        String sql = "INSERT INTO veterinarios (nombre_completo, documento_identidad, especialidad, telefono, email, direccion, fecha_contratacion, salario, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            ps.setString(i++, veterinario.getNombreCompleto());
            ps.setString(i++, veterinario.getDocumentoIdentidad());
            ps.setString(i++, veterinario.getEspecialidad());
            ps.setString(i++, veterinario.getTelefono());
            ps.setString(i++, veterinario.getEmail());
            ps.setString(i++, veterinario.getDireccion());

            if (veterinario.getFechaContratacion() != null) {
                ps.setDate(i++, Date.valueOf(veterinario.getFechaContratacion()));
            } else {
                ps.setNull(i++, Types.DATE);
            }

            ps.setBigDecimal(i++, veterinario.getSalario());
            ps.setBoolean(i++, veterinario.getActivo() != null ? veterinario.getActivo() : true);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    veterinario.setId(keys.getInt(1));
                }
            }
            return veterinario;

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando veterinario", e);
        }
    }

    @Override
    public Optional<Veterinario> findById(Integer id) {
        if (id == null) return Optional.empty();

        String sql = "SELECT * FROM veterinarios WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando veterinario por id", e);
        }
    }

    @Override
    public List<Veterinario> findAll() {
        String sql = "SELECT * FROM veterinarios ORDER BY nombre_completo";
        List<Veterinario> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando veterinarios", e);
        }
    }

    @Override
    public Veterinario update(Veterinario veterinario) {
        if (veterinario.getId() == null) {
            throw new IllegalArgumentException("ID requerido para actualizar");
        }

        String sql = "UPDATE veterinarios SET nombre_completo=?, documento_identidad=?, especialidad=?, telefono=?, email=?, direccion=?, fecha_contratacion=?, salario=?, activo=? WHERE id=?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, veterinario.getNombreCompleto());
            ps.setString(i++, veterinario.getDocumentoIdentidad());
            ps.setString(i++, veterinario.getEspecialidad());
            ps.setString(i++, veterinario.getTelefono());
            ps.setString(i++, veterinario.getEmail());
            ps.setString(i++, veterinario.getDireccion());

            if (veterinario.getFechaContratacion() != null) {
                ps.setDate(i++, Date.valueOf(veterinario.getFechaContratacion()));
            } else {
                ps.setNull(i++, Types.DATE);
            }

            ps.setBigDecimal(i++, veterinario.getSalario());
            ps.setBoolean(i++, veterinario.getActivo());
            ps.setInt(i++, veterinario.getId());

            ps.executeUpdate();
            return veterinario;

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando veterinario", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) return false;

        String sql = "DELETE FROM veterinarios WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando veterinario", e);
        }
    }

    @Override
    public List<Veterinario> findByEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) return List.of();

        String sql = "SELECT * FROM veterinarios WHERE LOWER(especialidad) LIKE ? ORDER BY nombre_completo";
        List<Veterinario> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + especialidad.trim().toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando veterinarios por especialidad", e);
        }
    }

    @Override
    public List<Veterinario> findActivos() {
        String sql = "SELECT * FROM veterinarios WHERE activo = true ORDER BY nombre_completo";
        List<Veterinario> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando veterinarios activos", e);
        }
    }

    @Override
    public Optional<Veterinario> findByDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) return Optional.empty();

        String sql = "SELECT * FROM veterinarios WHERE documento_identidad = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando veterinario por documento", e);
        }
    }

    @Override
    public Optional<Veterinario> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return Optional.empty();

        String sql = "SELECT * FROM veterinarios WHERE email = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando veterinario por email", e);
        }
    }

    private Veterinario mapRow(ResultSet rs) throws SQLException {
        Veterinario veterinario = Veterinario.builder()
                .withNombreCompleto(rs.getString("nombre_completo"))
                .withDocumentoIdentidad(rs.getString("documento_identidad"))
                .withEmail(rs.getString("email"))
                .withEspecialidad(rs.getString("especialidad"))
                .withTelefono(rs.getString("telefono"))
                .build();

        veterinario.setId(rs.getInt("id"));
        veterinario.setDireccion(rs.getString("direccion"));

        Date fechaContratacion = rs.getDate("fecha_contratacion");
        if (fechaContratacion != null) {
            veterinario.setFechaContratacion(fechaContratacion.toLocalDate());
        }

        veterinario.setSalario(rs.getBigDecimal("salario"));
        veterinario.setActivo(rs.getBoolean("activo"));

        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            veterinario.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            veterinario.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return veterinario;
    }
}