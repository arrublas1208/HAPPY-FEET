package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Sesion;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.SesionRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC de SesionRepository.
 * Persiste las sesiones en la tabla 'sesiones' de MySQL.
 */
public class SesionRepositoryImpl implements SesionRepository {

    private final DatabaseConnection db;

    public SesionRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public void save(Sesion sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("Sesion no puede ser nula");
        }

        String sql = "INSERT INTO sesiones (usuario_id, token_sesion, fecha_inicio, fecha_expiracion, " +
                     "direccion_ip, user_agent, activa) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, sesion.getUsuarioId());
            ps.setString(2, sesion.getTokenSesion());

            if (sesion.getFechaInicio() != null) {
                ps.setTimestamp(3, new Timestamp(sesion.getFechaInicio().getTime()));
            } else {
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }

            if (sesion.getFechaExpiracion() != null) {
                ps.setTimestamp(4, new Timestamp(sesion.getFechaExpiracion().getTime()));
            } else {
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }

            ps.setString(5, sesion.getDireccionIp());
            ps.setString(6, sesion.getUserAgent());
            ps.setBoolean(7, sesion.isActiva());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    sesion.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando sesion", e);
        }
    }

    @Override
    public Sesion findById(int id) {
        String sql = "SELECT id, usuario_id, token_sesion, fecha_inicio, fecha_expiracion, " +
                     "direccion_ip, user_agent, activa FROM sesiones WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando sesion por ID: " + id, e);
        }
    }

    @Override
    public List<Sesion> findAll() {
        String sql = "SELECT id, usuario_id, token_sesion, fecha_inicio, fecha_expiracion, " +
                     "direccion_ip, user_agent, activa FROM sesiones ORDER BY fecha_inicio DESC";

        List<Sesion> sesiones = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sesiones.add(mapRow(rs));
            }

            return sesiones;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando sesiones", e);
        }
    }

    @Override
    public void update(Sesion sesion) {
        if (sesion == null || sesion.getId() <= 0) {
            throw new IllegalArgumentException("Sesion invalida para actualizar");
        }

        String sql = "UPDATE sesiones SET usuario_id = ?, token_sesion = ?, fecha_inicio = ?, " +
                     "fecha_expiracion = ?, direccion_ip = ?, user_agent = ?, activa = ? WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sesion.getUsuarioId());
            ps.setString(2, sesion.getTokenSesion());

            if (sesion.getFechaInicio() != null) {
                ps.setTimestamp(3, new Timestamp(sesion.getFechaInicio().getTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }

            if (sesion.getFechaExpiracion() != null) {
                ps.setTimestamp(4, new Timestamp(sesion.getFechaExpiracion().getTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.setString(5, sesion.getDireccionIp());
            ps.setString(6, sesion.getUserAgent());
            ps.setBoolean(7, sesion.isActiva());
            ps.setInt(8, sesion.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Sesion con ID " + sesion.getId() + " no encontrada para actualizar");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando sesion ID: " + sesion.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM sesiones WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando sesion ID: " + id, e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Sesion.
     */
    private Sesion mapRow(ResultSet rs) throws SQLException {
        Sesion sesion = new Sesion();
        sesion.setId(rs.getInt("id"));
        sesion.setUsuarioId(rs.getInt("usuario_id"));
        sesion.setTokenSesion(rs.getString("token_sesion"));

        Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");
        if (fechaInicio != null) {
            sesion.setFechaInicio(new java.util.Date(fechaInicio.getTime()));
        }

        Timestamp fechaExpiracion = rs.getTimestamp("fecha_expiracion");
        if (fechaExpiracion != null) {
            sesion.setFechaExpiracion(new java.util.Date(fechaExpiracion.getTime()));
        }

        sesion.setDireccionIp(rs.getString("direccion_ip"));
        sesion.setUserAgent(rs.getString("user_agent"));
        sesion.setActiva(rs.getBoolean("activa"));

        return sesion;
    }
}