package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.PuntosCliente;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.PuntosClienteRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC de PuntosClienteRepository.
 * Persiste los puntos de cliente en la tabla 'puntos_cliente' de MySQL.
 */
public class PuntosClienteRepositoryImpl implements PuntosClienteRepository {

    private final DatabaseConnection db;

    public PuntosClienteRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public void save(PuntosCliente puntosCliente) {
        if (puntosCliente == null) {
            throw new IllegalArgumentException("PuntosCliente no puede ser nulo");
        }

        String sql = "INSERT INTO puntos_cliente (dueno_id, puntos_acumulados, puntos_redimidos, nivel) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, puntosCliente.getDuenoId());
            ps.setInt(2, puntosCliente.getPuntosAcumulados());
            ps.setInt(3, puntosCliente.getPuntosRedimidos());
            ps.setString(4, puntosCliente.getNivel() != null ? puntosCliente.getNivel() : "Bronce");

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    puntosCliente.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando puntos cliente", e);
        }
    }

    @Override
    public void update(PuntosCliente puntosCliente) {
        if (puntosCliente == null || puntosCliente.getId() <= 0) {
            throw new IllegalArgumentException("PuntosCliente invalido para actualizar");
        }

        String sql = "UPDATE puntos_cliente SET dueno_id = ?, puntos_acumulados = ?, puntos_redimidos = ?, " +
                     "nivel = ?, fecha_ultima_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, puntosCliente.getDuenoId());
            ps.setInt(2, puntosCliente.getPuntosAcumulados());
            ps.setInt(3, puntosCliente.getPuntosRedimidos());
            ps.setString(4, puntosCliente.getNivel());
            ps.setInt(5, puntosCliente.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("PuntosCliente con ID " + puntosCliente.getId() + " no encontrado");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando puntos cliente ID: " + puntosCliente.getId(), e);
        }
    }

    @Override
    public PuntosCliente findById(int id) {
        String sql = "SELECT id, dueno_id, puntos_acumulados, puntos_redimidos, nivel, " +
                     "fecha_ultima_actualizacion, fecha_creacion FROM puntos_cliente WHERE id = ?";

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
            throw new DataAccessException("Error buscando puntos cliente por ID: " + id, e);
        }
    }

    @Override
    public PuntosCliente findByDuenoId(Integer duenoId) {
        if (duenoId == null || duenoId <= 0) {
            return null;
        }

        String sql = "SELECT id, dueno_id, puntos_acumulados, puntos_redimidos, nivel, " +
                     "fecha_ultima_actualizacion, fecha_creacion FROM puntos_cliente WHERE dueno_id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, duenoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando puntos cliente por dueno ID: " + duenoId, e);
        }
    }

    @Override
    public List<PuntosCliente> findAll() {
        String sql = "SELECT id, dueno_id, puntos_acumulados, puntos_redimidos, nivel, " +
                     "fecha_ultima_actualizacion, fecha_creacion FROM puntos_cliente " +
                     "ORDER BY puntos_acumulados DESC";

        List<PuntosCliente> lista = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando puntos cliente", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM puntos_cliente WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando puntos cliente ID: " + id, e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto PuntosCliente.
     */
    private PuntosCliente mapRow(ResultSet rs) throws SQLException {
        PuntosCliente pc = new PuntosCliente();
        pc.setId(rs.getInt("id"));
        pc.setDuenoId(rs.getInt("dueno_id"));
        pc.setPuntosAcumulados(rs.getInt("puntos_acumulados"));
        pc.setPuntosRedimidos(rs.getInt("puntos_redimidos"));
        pc.setNivel(rs.getString("nivel"));

        Timestamp fechaUltima = rs.getTimestamp("fecha_ultima_actualizacion");
        if (fechaUltima != null) {
            pc.setFechaUltimaActualizacion(new java.util.Date(fechaUltima.getTime()));
        }

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            pc.setFechaCreacion(new java.util.Date(fechaCreacion.getTime()));
        }

        return pc;
    }
}