package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Factura;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.util.DatabaseConnection;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementación JDBC de FacturaRepository, alineada a database/schema.sql
 * Usa la tabla 'facturas' para cabeceras. Los items no se persisten en esta mínima integración.
 */
public class FacturaRepositoryImpl implements FacturaRepository {

    private final DatabaseConnection db;

    public FacturaRepositoryImpl(DatabaseConnection db) {
        this.db = Objects.requireNonNull(db);
    }

    @Override
    public Factura save(Factura factura) {
        if (factura.getId() == null) {
            return insert(factura);
        } else {
            return update(factura);
        }
    }

    private Factura insert(Factura f) {
        String sql = "INSERT INTO facturas (numero_factura, dueno_id, fecha_emision, fecha_vencimiento, subtotal, impuestos, descuento, total, estado, forma_pago, observaciones)\n" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asegurar datos básicos
            if (f.getFechaEmision() == null) f.setFechaEmision(LocalDateTime.now());
            String numero = f.getNumeroFactura();
            if (numero == null || numero.isBlank()) {
                numero = "FACT-" + System.currentTimeMillis();
            }
            // Cumplir límite de schema.sql (varchar(20))
            if (numero.length() > 20) {
                numero = numero.substring(0, 20);
            }
            f.setNumeroFactura(numero);

            ps.setString(1, numero);
            ps.setInt(2, Optional.ofNullable(f.getDuenoId()).orElse(0));
            ps.setTimestamp(3, Timestamp.valueOf(f.getFechaEmision()));
            if (f.getFechaVencimiento() != null) {
                ps.setDate(4, Date.valueOf(f.getFechaVencimiento()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setBigDecimal(5, Optional.ofNullable(f.getSubtotal()).orElse(BigDecimal.ZERO));
            ps.setBigDecimal(6, Optional.ofNullable(f.getImpuestos()).orElse(BigDecimal.ZERO));
            ps.setBigDecimal(7, Optional.ofNullable(f.getDescuento()).orElse(BigDecimal.ZERO));
            ps.setBigDecimal(8, Optional.ofNullable(f.getTotal()).orElse(BigDecimal.ZERO));
            ps.setString(9, mapEstadoToDb(f.getEstado()));
            ps.setString(10, mapFormaPagoToDb(f.getFormaPago()));
            ps.setString(11, Optional.ofNullable(f.getObservaciones()).orElse(null));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setId(rs.getInt(1));
                }
            }
            return f;
        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error insertando factura: " + e.getMessage());
            throw new DataAccessException("Error insertando factura", e);
        }
    }

    private Factura update(Factura f) {
        String sql = "UPDATE facturas SET numero_factura=?, dueno_id=?, fecha_emision=?, fecha_vencimiento=?, subtotal=?, impuestos=?, descuento=?, total=?, estado=?, forma_pago=?, observaciones=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (f.getFechaEmision() == null) f.setFechaEmision(LocalDateTime.now());

            ps.setString(1, f.getNumeroFactura());
            ps.setInt(2, Optional.ofNullable(f.getDuenoId()).orElse(0));
            ps.setTimestamp(3, Timestamp.valueOf(f.getFechaEmision()));
            if (f.getFechaVencimiento() != null) {
                ps.setDate(4, Date.valueOf(f.getFechaVencimiento()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setBigDecimal(5, Optional.ofNullable(f.getSubtotal()).orElse(BigDecimal.ZERO));
            ps.setBigDecimal(6, Optional.ofNullable(f.getImpuestos()).orElse(BigDecimal.ZERO));
            ps.setBigDecimal(7, Optional.ofNullable(f.getDescuento()).orElse(BigDecimal.ZERO));
            ps.setBigDecimal(8, Optional.ofNullable(f.getTotal()).orElse(BigDecimal.ZERO));
            ps.setString(9, mapEstadoToDb(f.getEstado()));
            ps.setString(10, mapFormaPagoToDb(f.getFormaPago()));
            ps.setString(11, Optional.ofNullable(f.getObservaciones()).orElse(null));
            ps.setInt(12, f.getId());

            ps.executeUpdate();
            return f;
        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error actualizando factura id=" + f.getId() + " : " + e.getMessage());
            throw new DataAccessException("Error actualizando factura id=" + f.getId(), e);
        }
    }

    @Override
    public Optional<Factura> findById(Integer id) {
        String sql = "SELECT id, numero_factura, dueno_id, fecha_emision, fecha_vencimiento, subtotal, impuestos, descuento, total, estado, forma_pago, observaciones FROM facturas WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error buscando factura por id=" + id + " : " + e.getMessage());
            throw new DataAccessException("Error buscando factura por id=" + id, e);
        }
    }

    @Override
    public List<Factura> findAll() {
        List<Factura> list = new ArrayList<>();
        String sql = "SELECT id, numero_factura, dueno_id, fecha_emision, fecha_vencimiento, subtotal, impuestos, descuento, total, estado, forma_pago, observaciones FROM facturas ORDER BY fecha_emision DESC, id DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error listando facturas: " + e.getMessage());
            throw new DataAccessException("Error listando facturas", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM facturas WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error eliminando factura id=" + id + " : " + e.getMessage());
            throw new DataAccessException("Error eliminando factura por id=" + id, e);
        }
    }

    @Override
    public Map<String, Object> obtenerFacturacionPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT " +
                     "COUNT(*) AS cantidad, " +
                     "SUM(total) AS total, " +
                     "AVG(total) AS promedio " +
                     "FROM facturas " +
                     "WHERE fecha_emision BETWEEN ? AND ? " +
                     "AND estado != 'Cancelada'";

        Map<String, Object> resultado = new HashMap<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.put("cantidad", rs.getInt("cantidad"));
                    resultado.put("total", rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO);
                    resultado.put("promedio", rs.getBigDecimal("promedio") != null ? rs.getBigDecimal("promedio") : BigDecimal.ZERO);
                }
            }

            return resultado;

        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error obteniendo facturación por período: " + e.getMessage());
            throw new DataAccessException("Error obteniendo facturación por período", e);
        }
    }

    @Override
    public BigDecimal obtenerTotalFacturado(LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT COALESCE(SUM(total), 0) AS total " +
                     "FROM facturas " +
                     "WHERE fecha_emision BETWEEN ? AND ? " +
                     "AND estado != 'Cancelada'";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }

            return BigDecimal.ZERO;

        } catch (SQLException e) {
            System.err.println("[FacturaRepositoryImpl] Error obteniendo total facturado: " + e.getMessage());
            throw new DataAccessException("Error obteniendo total facturado", e);
        }
    }

    private Factura mapRow(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setId(rs.getInt("id"));
        f.setNumeroFactura(rs.getString("numero_factura"));
        f.setDuenoId(rs.getInt("dueno_id"));
        Timestamp ts = rs.getTimestamp("fecha_emision");
        if (ts != null) f.setFechaEmision(ts.toLocalDateTime());
        Date fv = rs.getDate("fecha_vencimiento");
        if (fv != null) f.setFechaVencimiento(fv.toLocalDate());

        // Los totales del dominio se calculan en tiempo de construcción normalmente, pero aquí asignamos los valores almacenados
        try {
            Field subtotalField = Factura.class.getDeclaredField("subtotal");
            subtotalField.setAccessible(true);
            subtotalField.set(f, rs.getBigDecimal("subtotal"));
            Field impuestosField = Factura.class.getDeclaredField("impuestos");
            impuestosField.setAccessible(true);
            impuestosField.set(f, rs.getBigDecimal("impuestos"));
            Field descuentoField = Factura.class.getDeclaredField("descuento");
            descuentoField.setAccessible(true);
            descuentoField.set(f, rs.getBigDecimal("descuento"));
            Field totalField = Factura.class.getDeclaredField("total");
            totalField.setAccessible(true);
            totalField.set(f, rs.getBigDecimal("total"));
        } catch (ReflectiveOperationException rex) {
            System.err.println("[FacturaRepositoryImpl] Warning: fallo al asignar totales por reflexión: " + rex.getMessage());
            rex.printStackTrace(System.err);
        }

        // Estado: asignación directa sin transiciones; solo si difiere
        try {
            Field estadoField = Factura.class.getDeclaredField("estado");
            estadoField.setAccessible(true);
            Factura.FacturaEstado estadoLeido = mapEstadoFromDb(rs.getString("estado"));
            Factura.FacturaEstado actual = null;
            try {
                actual = (Factura.FacturaEstado) estadoField.get(f);
            } catch (IllegalAccessException iae) {
                System.err.println("[FacturaRepositoryImpl] Warning: no se pudo leer campo 'estado' por reflexión: " + iae.getMessage());
            }
            if (actual == null || actual != estadoLeido) {
                try {
                    estadoField.set(f, estadoLeido);
                } catch (IllegalAccessException iae) {
                    System.err.println("[FacturaRepositoryImpl] Warning: no se pudo asignar 'estado' por reflexión: " + iae.getMessage());
                }
            }
        } catch (ReflectiveOperationException rex) {
            System.err.println("[FacturaRepositoryImpl] Warning: no se encontró el campo 'estado' en la entidad Factura: " + rex.getMessage());
            rex.printStackTrace(System.err);
        } catch (ClassCastException ccx) {
            System.err.println("[FacturaRepositoryImpl] Warning: fallo casteando 'estado' leido: " + ccx.getMessage());
            ccx.printStackTrace(System.err);
        }

        String fp = rs.getString("forma_pago");
        if (fp != null) f.setFormaPago(mapFormaPagoFromDb(fp));
        f.setObservaciones(rs.getString("observaciones"));
        return f;
    }

    private String mapEstadoToDb(Factura.FacturaEstado e) {
        if (e == null) return "Pendiente";
        return switch (e) {
            case PENDIENTE -> "Pendiente";
            case PAGADA -> "Pagada";
            case CANCELADA -> "Cancelada";
            case VENCIDA -> "Vencida";
        };
    }

    private Factura.FacturaEstado mapEstadoFromDb(String s) {
        if (s == null) return Factura.FacturaEstado.PENDIENTE;
        String norm = s.trim();
        if (norm.isEmpty()) return Factura.FacturaEstado.PENDIENTE;
        String lower = norm.toLowerCase(Locale.ROOT);
        switch (lower) {
            case "pagada":
                return Factura.FacturaEstado.PAGADA;
            case "cancelada":
                return Factura.FacturaEstado.CANCELADA;
            case "vencida":
                return Factura.FacturaEstado.VENCIDA;
            case "pendiente":
                return Factura.FacturaEstado.PENDIENTE;
            default:
                System.err.println("[FACTURA] Valor de estado desconocido en BD: '" + s + "'. Usando PENDIENTE por defecto.");
                return Factura.FacturaEstado.PENDIENTE;
        }
    }

    private String mapFormaPagoToDb(Factura.FormaPago fp) {
        if (fp == null) return null;
        return switch (fp) {
            case EFECTIVO -> "Efectivo";
            case TARJETA_DEBITO -> "Tarjeta Débito";
            case TARJETA_CREDITO -> "Tarjeta Crédito";
            case TRANSFERENCIA -> "Transferencia";
            default -> null; // PUNTOS u otros no están en schema.sql
        };
    }

    private Factura.FormaPago mapFormaPagoFromDb(String s) {
        if (s == null) return null;
        return switch (s) {
            case "Efectivo" -> Factura.FormaPago.EFECTIVO;
            case "Tarjeta Débito" -> Factura.FormaPago.TARJETA_DEBITO;
            case "Tarjeta Crédito" -> Factura.FormaPago.TARJETA_CREDITO;
            case "Transferencia" -> Factura.FormaPago.TRANSFERENCIA;
            default -> null;
        };
    }
}
