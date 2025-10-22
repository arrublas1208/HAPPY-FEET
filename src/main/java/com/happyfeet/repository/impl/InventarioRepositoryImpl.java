package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Inventario;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.InventarioRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventarioRepositoryImpl implements InventarioRepository {
    private final DatabaseConnection db;

    public InventarioRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public List<Inventario> findByNombreProducto(String nombreProducto) {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE nombre_producto = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreProducto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error consultando inventario por nombre de producto", e);
        }
    }

    @Override
    public Optional<Inventario> findByCodigo(String codigo) {
        String sql = "SELECT * FROM inventario WHERE codigo = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error consultando inventario por código", e);
        }
    }

    @Override
    public Optional<Inventario> findById(Integer id) {
        String sql = "SELECT * FROM inventario WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error consultando inventario por id", e);
        }
    }

    @Override
    public List<Inventario> findAll() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando inventario", e);
        }
    }

    @Override
    public Inventario save(Inventario inventario) {
        // Alinear con schema.sql: 'codigo' es NOT NULL y UNIQUE; generarlo si no viene definido
        String codigo = inventario.getCodigo();
        if (codigo == null || codigo.isBlank()) {
            codigo = "PRD-" + System.currentTimeMillis();
            inventario.setCodigo(codigo);
        }
        String sql = "INSERT INTO inventario (codigo, nombre_producto, producto_tipo_id, cantidad_stock, precio_venta, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, codigo);
            ps.setString(2, inventario.getNombreProducto());
            // A falta de campo en la entidad, usar producto_tipo_id = 1 por defecto (debe existir en producto_tipos)
            ps.setInt(3, 1);
            ps.setInt(4, inventario.getCantidadStock());
            ps.setBigDecimal(5, inventario.getPrecioVenta());
            if (inventario.getFechaVencimiento() != null) {
                ps.setDate(6, Date.valueOf(inventario.getFechaVencimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                inventario.setId(rs.getInt(1));
            }
            return inventario;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando inventario", e);
        }
    }

    @Override
    public Inventario update(Inventario inventario) {
        String sql = "UPDATE inventario SET nombre_producto = ?, cantidad_stock = ?, precio_venta = ?, fecha_vencimiento = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inventario.getNombreProducto());
            ps.setInt(2, inventario.getCantidadStock());
            ps.setBigDecimal(3, inventario.getPrecioVenta());
            if (inventario.getFechaVencimiento() != null) {
                ps.setDate(4, Date.valueOf(inventario.getFechaVencimiento()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setInt(5, inventario.getId());
            ps.executeUpdate();
            return inventario;
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando inventario", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM inventario WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando inventario por id", e);
        }
    }

    // Métodos adicionales para alertas
    @Override
    public List<Inventario> findProductosBajoStockMinimo() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE cantidad_stock < stock_minimo";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando productos con stock bajo", e);
        }
    }

    @Override
    public List<Inventario> findProductosProximosAVencer() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE fecha_vencimiento IS NOT NULL AND fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando productos próximos a vencer", e);
        }
    }

    // Método auxiliar para mapear el ResultSet a la entidad Inventario
    private Inventario mapRow(ResultSet rs) throws SQLException {
        Inventario inv = new Inventario();
        inv.setId(rs.getInt("id"));
        inv.setNombreProducto(rs.getString("nombre_producto"));
        inv.setCantidadStock(rs.getInt("cantidad_stock"));
        inv.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        inv.setFechaVencimiento(rs.getDate("fecha_vencimiento") != null ? rs.getDate("fecha_vencimiento").toLocalDate() : null);

        // Mapear código si existe en la base de datos
        if (hasColumn(rs, "codigo")) {
            inv.setCodigo(rs.getString("codigo"));
        }

        // Si tienes campo stock_minimo, agrégalo aquí
        if (hasColumn(rs, "stock_minimo")) {
            inv.setStockMinimo(rs.getInt("stock_minimo"));
        }
        return inv;
    }

    // Verifica si el ResultSet tiene una columna específica
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}