package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.enums.CitaEstado;
import com.happyfeet.repository.CitaRepository;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CitaRepositoryImpl implements CitaRepository {
    private final DatabaseConnection db;

    public CitaRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Cita save(Cita c) {
        String sql = "INSERT INTO citas (codigo, mascota_id, servicio_id, veterinario_id, fecha_hora, motivo_consulta, estado_id, urgencia) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // Generar valores por defecto para campos que no están en la entidad
        String codigo = generarCodigo();
        int servicioId = 1; // por defecto (Consulta general), debe existir en data.sql
        String urgencia = "Baja"; // por defecto
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setString(i++, codigo);
            ps.setInt(i++, c.getIdMascota() != null ? c.getIdMascota().intValue() : 0);
            ps.setInt(i++, servicioId);
            ps.setInt(i++, c.getIdVeterinario() != null ? c.getIdVeterinario().intValue() : 0);
            ps.setTimestamp(i++, Timestamp.valueOf(c.getInicio()));
            ps.setString(i++, c.getMotivo());
            ps.setInt(i++, c.getEstado() != null ? c.getEstado().getId() : CitaEstado.getEstadoInicial().getId());
            ps.setString(i++, urgencia);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getLong(1));
            }
            return c;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando cita", e);
        }
    }

    @Override
    public Cita update(Cita c) {
        if (c.getId() == null) throw new IllegalArgumentException("id requerido para actualizar");
        String sql = "UPDATE citas SET mascota_id=?, veterinario_id=?, fecha_hora=?, motivo_consulta=?, estado_id=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, c.getIdMascota() != null ? c.getIdMascota().intValue() : 0);
            ps.setInt(i++, c.getIdVeterinario() != null ? c.getIdVeterinario().intValue() : 0);
            ps.setTimestamp(i++, Timestamp.valueOf(c.getInicio()));
            ps.setString(i++, c.getMotivo());
            ps.setInt(i++, c.getEstado() != null ? c.getEstado().getId() : CitaEstado.getEstadoInicial().getId());
            ps.setLong(i, c.getId());
            ps.executeUpdate();
            return c;
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando cita", e);
        }
    }

    @Override
    public Optional<Cita> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM citas WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando cita por id", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM citas WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando cita", e);
        }
    }

    @Override
    public List<Cita> findByVeterinarioAndRange(Long idVet, LocalDateTime desde, LocalDateTime hasta) {
        if (idVet == null || desde == null || hasta == null) return List.of();
        String sql = "SELECT * FROM citas WHERE veterinario_id=? AND fecha_hora BETWEEN ? AND ? ORDER BY fecha_hora";
        List<Cita> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVet);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando citas por rango", e);
        }
    }

    @Override
    public List<Cita> findByDate(LocalDate fecha) {
        if (fecha == null) return List.of();
        String sql = "SELECT * FROM citas WHERE DATE(fecha_hora) = ? ORDER BY fecha_hora";
        List<Cita> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando citas por fecha", e);
        }
    }

    @Override
    public List<Cita> findByEstado(CitaEstado estado) {
        if (estado == null) return List.of();
        String sql = "SELECT * FROM citas WHERE estado_id = ? ORDER BY fecha_hora";
        List<Cita> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, estado.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando citas por estado", e);
        }
    }

    @Override
    public boolean existsOverlap(Long idVet, LocalDateTime inicio, LocalDateTime fin) {
        if (idVet == null || inicio == null || fin == null) return false;
        // Dado que el esquema sólo almacena fecha_hora (inicio), consideramos solape si hay una cita que empieza dentro del rango
        String sql = "SELECT 1 FROM citas WHERE veterinario_id=? AND fecha_hora BETWEEN ? AND ? LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVet);
            ps.setTimestamp(2, Timestamp.valueOf(inicio));
            ps.setTimestamp(3, Timestamp.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error verificando solape de citas", e);
        }
    }

    // --------- Helpers ---------
    private Cita mapRow(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setId(rs.getLong("id"));
        c.setIdMascota((long) rs.getInt("mascota_id"));
        c.setIdVeterinario((long) rs.getInt("veterinario_id"));
        Timestamp ts = rs.getTimestamp("fecha_hora");
        c.setInicio(ts != null ? ts.toLocalDateTime() : null);
        c.setFin(null); // el esquema actual no tiene columna de fin
        c.setMotivo(rs.getString("motivo_consulta"));
        int estadoId = rs.getInt("estado_id");
        CitaEstado estado = CitaEstado.porId(estadoId).orElse(CitaEstado.getEstadoInicial());
        c.setEstado(estado);
        return c;
    }

    private String generarCodigo() {
        return "CIT-" + System.currentTimeMillis();
    }
}
