package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.MovimientoPuntos;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.MovimientoPuntosRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovimientoPuntosRepositoryImpl implements MovimientoPuntosRepository {
    private final DatabaseConnection db;

    public MovimientoPuntosRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public MovimientoPuntos save(MovimientoPuntos movimiento) {
        String sql = "INSERT INTO movimientos_puntos (dueno_id, puntos, concepto, tipo, fecha) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setInt(i++, movimiento.getDuenoId());
            ps.setInt(i++, movimiento.getPuntos());
            ps.setString(i++, movimiento.getConcepto());
            ps.setString(i++, movimiento.getTipo().name());
            ps.setTimestamp(i, Timestamp.valueOf(movimiento.getFecha()));

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    movimiento.setId(keys.getInt(1));
                }
            }
            return movimiento;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando movimiento de puntos", e);
        }
    }

    @Override
    public Optional<MovimientoPuntos> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM movimientos_puntos WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando movimiento", e);
        }
    }

    @Override
    public List<MovimientoPuntos> findByDuenoId(Integer duenoId) {
        List<MovimientoPuntos> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimientos_puntos WHERE dueno_id = ? ORDER BY fecha";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapRow(rs));
                }
            }
            return movimientos;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando movimientos por dueño", e);
        }
    }

    @Override
    public List<MovimientoPuntos> findByDuenoIdOrderByFechaDesc(Integer duenoId) {
        List<MovimientoPuntos> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimientos_puntos WHERE dueno_id = ? ORDER BY fecha DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapRow(rs));
                }
            }
            return movimientos;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando movimientos por dueño", e);
        }
    }

    @Override
    public List<MovimientoPuntos> findAll() {
        List<MovimientoPuntos> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimientos_puntos ORDER BY fecha DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movimientos.add(mapRow(rs));
            }
            return movimientos;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando movimientos", e);
        }
    }

    @Override
    public int calcularTotalPuntos(Integer duenoId) {
        String sql = "SELECT SUM(CASE WHEN tipo = 'GANADOS' THEN puntos ELSE -puntos END) as total " +
                     "FROM movimientos_puntos WHERE dueno_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    return rs.wasNull() ? 0 : total;
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error calculando total de puntos", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM movimientos_puntos WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando movimiento", e);
        }
    }

    private MovimientoPuntos mapRow(ResultSet rs) throws SQLException {
        MovimientoPuntos m = new MovimientoPuntos();
        m.setId(rs.getInt("id"));
        m.setDuenoId(rs.getInt("dueno_id"));
        m.setPuntos(rs.getInt("puntos"));
        m.setConcepto(rs.getString("concepto"));
        m.setTipo(MovimientoPuntos.TipoMovimiento.fromString(rs.getString("tipo")));

        Timestamp fecha = rs.getTimestamp("fecha");
        if (fecha != null) {
            m.setFecha(fecha.toLocalDateTime());
        }

        return m;
    }
}