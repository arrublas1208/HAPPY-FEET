package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.CitaEstado;
import com.happyfeet.repository.CitaEstadoRepository;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del repositorio de CitaEstado.
 * Maneja los estados de citas desde la tabla cita_estados.
 */
public class CitaEstadoRepositoryImpl implements CitaEstadoRepository {
    private final DatabaseConnection db;

    public CitaEstadoRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public void save(CitaEstado estado) {
        String sql = "INSERT INTO cita_estados (nombre, descripcion, es_activo) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, estado.getNombre());
            ps.setString(2, estado.getDescripcion());
            ps.setBoolean(3, estado.isEsActivo());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    estado.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando cita estado", e);
        }
    }

    @Override
    public CitaEstado findById(int id) {
        String sql = "SELECT id, nombre, descripcion, es_activo, fecha_creacion FROM cita_estados WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCitaEstado(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando cita estado por ID", e);
        }
        return null;
    }

    @Override
    public List<CitaEstado> findAll() {
        String sql = "SELECT id, nombre, descripcion, es_activo, fecha_creacion FROM cita_estados ORDER BY id";
        List<CitaEstado> estados = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                estados.add(mapearCitaEstado(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listando cita estados", e);
        }
        return estados;
    }

    @Override
    public void update(CitaEstado estado) {
        String sql = "UPDATE cita_estados SET nombre = ?, descripcion = ?, es_activo = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado.getNombre());
            ps.setString(2, estado.getDescripcion());
            ps.setBoolean(3, estado.isEsActivo());
            ps.setInt(4, estado.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando cita estado", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM cita_estados WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando cita estado", e);
        }
    }

    /**
     * Busca estados activos solamente
     */
    public List<CitaEstado> findActivos() {
        String sql = "SELECT id, nombre, descripcion, es_activo, fecha_creacion " +
                     "FROM cita_estados WHERE es_activo = 1 ORDER BY id";
        List<CitaEstado> estados = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                estados.add(mapearCitaEstado(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listando estados activos", e);
        }
        return estados;
    }

    /**
     * Busca estado por nombre
     */
    public CitaEstado findByNombre(String nombre) {
        String sql = "SELECT id, nombre, descripcion, es_activo, fecha_creacion " +
                     "FROM cita_estados WHERE nombre = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCitaEstado(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando estado por nombre", e);
        }
        return null;
    }

    private CitaEstado mapearCitaEstado(ResultSet rs) throws SQLException {
        CitaEstado estado = new CitaEstado();
        estado.setId(rs.getInt("id"));
        estado.setNombre(rs.getString("nombre"));
        estado.setDescripcion(rs.getString("descripcion"));
        estado.setEsActivo(rs.getBoolean("es_activo"));
        estado.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return estado;
    }
}