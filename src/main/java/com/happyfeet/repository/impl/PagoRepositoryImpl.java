package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Pago;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.PagoRepository;
import com.happyfeet.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementaci\u00f3n JDBC de PagoRepository.
 * Persiste los pagos en la tabla 'pagos' de MySQL.
 */
public class PagoRepositoryImpl implements PagoRepository {

    private final DatabaseConnection db;

    public PagoRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public void save(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("Pago no puede ser nulo");
        }

        String sql = "INSERT INTO pagos (factura_id, monto, fecha_pago, metodo_pago, referencia_pago, estado, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getFacturaId());
            ps.setBigDecimal(2, pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO);

            if (pago.getFechaPago() != null) {
                ps.setTimestamp(3, new Timestamp(pago.getFechaPago().getTime()));
            } else {
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }

            ps.setString(4, pago.getMetodoPago() != null ? pago.getMetodoPago() : "Efectivo");
            ps.setString(5, pago.getReferenciaPago());
            ps.setString(6, pago.getEstado() != null ? pago.getEstado() : "Completado");
            ps.setString(7, pago.getObservaciones());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pago.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando pago", e);
        }
    }

    @Override
    public Pago findById(int id) {
        String sql = "SELECT id, factura_id, monto, fecha_pago, metodo_pago, referencia_pago, estado, observaciones, fecha_creacion " +
                     "FROM pagos WHERE id = ?";

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
            throw new DataAccessException("Error buscando pago por ID: " + id, e);
        }
    }

    @Override
    public List<Pago> findAll() {
        String sql = "SELECT id, factura_id, monto, fecha_pago, metodo_pago, referencia_pago, estado, observaciones, fecha_creacion " +
                     "FROM pagos ORDER BY fecha_pago DESC";

        List<Pago> pagos = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pagos.add(mapRow(rs));
            }

            return pagos;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando pagos", e);
        }
    }

    @Override
    public void update(Pago pago) {
        if (pago == null || pago.getId() <= 0) {
            throw new IllegalArgumentException("Pago inv\u00e1lido para actualizar");
        }

        String sql = "UPDATE pagos SET factura_id = ?, monto = ?, fecha_pago = ?, metodo_pago = ?, " +
                     "referencia_pago = ?, estado = ?, observaciones = ? WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pago.getFacturaId());
            ps.setBigDecimal(2, pago.getMonto());

            if (pago.getFechaPago() != null) {
                ps.setTimestamp(3, new Timestamp(pago.getFechaPago().getTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }

            ps.setString(4, pago.getMetodoPago());
            ps.setString(5, pago.getReferenciaPago());
            ps.setString(6, pago.getEstado());
            ps.setString(7, pago.getObservaciones());
            ps.setInt(8, pago.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Pago con ID " + pago.getId() + " no encontrado para actualizar");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando pago ID: " + pago.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM pagos WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando pago ID: " + id, e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Pago.
     */
    private Pago mapRow(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setId(rs.getInt("id"));
        pago.setFacturaId(rs.getInt("factura_id"));
        pago.setMonto(rs.getBigDecimal("monto"));

        Timestamp fechaPago = rs.getTimestamp("fecha_pago");
        if (fechaPago != null) {
            pago.setFechaPago(new java.util.Date(fechaPago.getTime()));
        }

        pago.setMetodoPago(rs.getString("metodo_pago"));
        pago.setReferenciaPago(rs.getString("referencia_pago"));
        pago.setEstado(rs.getString("estado"));
        pago.setObservaciones(rs.getString("observaciones"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            pago.setFechaCreacion(new java.util.Date(fechaCreacion.getTime()));
        }

        return pago;
    }
}