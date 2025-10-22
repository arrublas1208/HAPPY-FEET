package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.CompraClubFrecuente;
import com.happyfeet.repository.CompraClubFrecuenteRepository;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompraClubFrecuenteRepositoryImpl implements CompraClubFrecuenteRepository {
    private final DatabaseConnection db;

    public CompraClubFrecuenteRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public CompraClubFrecuente save(CompraClubFrecuente compra) {
        String sql = "INSERT INTO compras_club_frecuentes (dueno_id, monto, puntos_generados, fecha, descripcion) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setInt(i++, compra.getDuenoId());
            ps.setBigDecimal(i++, compra.getMonto());
            ps.setInt(i++, compra.getPuntosGenerados());
            ps.setTimestamp(i++, Timestamp.valueOf(compra.getFecha()));
            ps.setString(i, compra.getDescripcion());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    compra.setId(keys.getInt(1));
                }
            }
            return compra;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando compra", e);
        }
    }

    @Override
    public Optional<CompraClubFrecuente> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM compras_club_frecuentes WHERE id = ?";
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
            throw new DataAccessException("Error buscando compra", e);
        }
    }

    @Override
    public List<CompraClubFrecuente> findByDuenoId(Integer duenoId) {
        List<CompraClubFrecuente> compras = new ArrayList<>();
        String sql = "SELECT * FROM compras_club_frecuentes WHERE dueno_id = ? ORDER BY fecha";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    compras.add(mapRow(rs));
                }
            }
            return compras;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando compras por dueño", e);
        }
    }

    @Override
    public List<CompraClubFrecuente> findByDuenoIdOrderByFechaDesc(Integer duenoId) {
        List<CompraClubFrecuente> compras = new ArrayList<>();
        String sql = "SELECT * FROM compras_club_frecuentes WHERE dueno_id = ? ORDER BY fecha DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    compras.add(mapRow(rs));
                }
            }
            return compras;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando compras por dueño", e);
        }
    }

    @Override
    public List<CompraClubFrecuente> findAll() {
        List<CompraClubFrecuente> compras = new ArrayList<>();
        String sql = "SELECT * FROM compras_club_frecuentes ORDER BY fecha DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                compras.add(mapRow(rs));
            }
            return compras;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando compras", e);
        }
    }

    @Override
    public BigDecimal calcularTotalGastado(Integer duenoId) {
        String sql = "SELECT SUM(monto) as total FROM compras_club_frecuentes WHERE dueno_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DataAccessException("Error calculando total gastado", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM compras_club_frecuentes WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando compra", e);
        }
    }

    private CompraClubFrecuente mapRow(ResultSet rs) throws SQLException {
        CompraClubFrecuente c = new CompraClubFrecuente();
        c.setId(rs.getInt("id"));
        c.setDuenoId(rs.getInt("dueno_id"));
        c.setMonto(rs.getBigDecimal("monto"));
        c.setPuntosGenerados(rs.getInt("puntos_generados"));

        Timestamp fecha = rs.getTimestamp("fecha");
        if (fecha != null) {
            c.setFecha(fecha.toLocalDateTime());
        }

        c.setDescripcion(rs.getString("descripcion"));
        return c;
    }
}