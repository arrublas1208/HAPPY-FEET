package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.DuenoRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DuenoRepositoryImpl implements DuenoRepository {
    private final DatabaseConnection db;

    public DuenoRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    // ------------ CRUD ------------
    @Override
    public Dueno save(Dueno d) {
        if (d.getId() == null) {
            String sql = "INSERT INTO duenos (nombre_completo, documento_identidad, direccion, telefono, email, contacto_emergencia, fecha_nacimiento, tipo_sangre, alergias) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                int i = 1;
                ps.setString(i++, d.getNombreCompleto());
                ps.setString(i++, d.getDocumentoIdentidad());
                ps.setString(i++, d.getDireccion());
                ps.setString(i++, d.getTelefono());
                ps.setString(i++, d.getEmail());
                ps.setString(i++, d.getContactoEmergencia());
                if (d.getFechaNacimiento() != null) ps.setDate(i++, Date.valueOf(d.getFechaNacimiento())); else ps.setNull(i++, Types.DATE);
                ps.setString(i++, d.getTipoSangre());
                ps.setString(i, d.getAlergia());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) d.setId(keys.getInt(1));
                }
                return d;
            } catch (SQLException e) {
                throw new DataAccessException("Error guardando dueño", e);
            }
        } else {
            return update(d);
        }
    }

    @Override
    public Optional<Dueno> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM duenos WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por id", e);
        }
    }

    @Override
    public List<Dueno> findAll() {
        List<Dueno> duenos = new ArrayList<>();
        String sql = "SELECT * FROM duenos";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) duenos.add(mapRow(rs));
            return duenos;
        } catch (Exception e) {
            throw new DataAccessException("Error listando dueños", e);
        }
    }

    @Override
    public Dueno update(Dueno d) {
        if (d.getId() == null) throw new IllegalArgumentException("id requerido para actualizar");
        String sql = "UPDATE duenos SET nombre_completo=?, documento_identidad=?, direccion=?, telefono=?, email=?, contacto_emergencia=?, fecha_nacimiento=?, tipo_sangre=?, alergias=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, d.getNombreCompleto());
            ps.setString(i++, d.getDocumentoIdentidad());
            ps.setString(i++, d.getDireccion());
            ps.setString(i++, d.getTelefono());
            ps.setString(i++, d.getEmail());
            ps.setString(i++, d.getContactoEmergencia());
            if (d.getFechaNacimiento() != null) ps.setDate(i++, Date.valueOf(d.getFechaNacimiento())); else ps.setNull(i++, Types.DATE);
            ps.setString(i++, d.getTipoSangre());
            ps.setString(i++, d.getAlergia());
            ps.setInt(i, d.getId());
            ps.executeUpdate();
            return d;
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando dueño", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) return false;
        String sql = "DELETE FROM duenos WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando dueño", e);
        }
    }

    // ------------ Consultas específicas ------------
    @Override
    public Dueno findByNombre(String nombre) {
        // Búsqueda simple por coincidencia parcial en nombre_completo
        if (nombre == null || nombre.isBlank()) return null;
        String sql = "SELECT * FROM duenos WHERE LOWER(nombre_completo) LIKE ? LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre.trim().toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por nombre", e);
        }
    }

    @Override
    public Dueno findByDocumentoIdentidad(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) return null;
        String sql = "SELECT * FROM duenos WHERE documento_identidad = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, documentoIdentidad.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por documento", e);
        }
    }

    @Override
    public Dueno findByTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) return null;
        String sql = "SELECT * FROM duenos WHERE telefono = ? LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, telefono.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por teléfono", e);
        }
    }

    @Override
    public Dueno findByEmail(String email) {
        if (email == null || email.isBlank()) return null;
        String sql = "SELECT * FROM duenos WHERE email = ? LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por email", e);
        }
    }

    @Override
    public Dueno findByContactoEmergencia(String contactoEmergencia) {
        if (contactoEmergencia == null || contactoEmergencia.isBlank()) return null;
        String sql = "SELECT * FROM duenos WHERE contacto_emergencia = ? LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contactoEmergencia.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por contacto de emergencia", e);
        }
    }

    @Override
    public Dueno findByTipoSangre(String tipoSangre) {
        if (tipoSangre == null || tipoSangre.isBlank()) return null;
        String sql = "SELECT * FROM duenos WHERE tipo_sangre = ? LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoSangre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando dueño por tipo de sangre", e);
        }
    }

    // ------------ Helpers ------------
    private Dueno mapRow(ResultSet rs) throws SQLException {
        LocalDate fechaNacimiento = null;
        var fechaNacimientoDate = rs.getDate("fecha_nacimiento");
        if (fechaNacimientoDate != null) fechaNacimiento = fechaNacimientoDate.toLocalDate();
        LocalDateTime fechaRegistro = toLocalDateTimeSafe(rs.getTimestamp("fecha_registro"));
        LocalDateTime fechaActualizacion = toLocalDateTimeSafe(rs.getTimestamp("fecha_actualizacion"));
        return Dueno.Builder.create()
                .withId(rs.getInt("id"))
                .withNombreCompleto(rs.getString("nombre_completo"))
                .withDocumentoIdentidad(rs.getString("documento_identidad"))
                .withDireccion(rs.getString("direccion"))
                .withTelefono(rs.getString("telefono"))
                .withEmail(rs.getString("email"))
                .withContactoEmergencia(rs.getString("contacto_emergencia"))
                .withFechaNacimiento(fechaNacimiento)
                .withTipoSangre(rs.getString("tipo_sangre"))
                .withAlergia(rs.getString("alergias"))
                .withFechaRegistro(fechaRegistro)
                .withFechaActualizacion(fechaActualizacion)
                .build();
    }

    private LocalDateTime toLocalDateTimeSafe(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}

