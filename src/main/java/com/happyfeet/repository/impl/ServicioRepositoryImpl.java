package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Servicio;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.ServicioRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioRepositoryImpl implements ServicioRepository {
    private final DatabaseConnection db;

    public ServicioRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Servicio save(Servicio servicio) {
        String sql = "INSERT INTO servicios (codigo, nombre, descripcion, precio, duracion_estimada, activo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getDescripcion());
            ps.setBigDecimal(4, servicio.getPrecio());
            ps.setInt(5, servicio.getDuracionEstimada() != null ? servicio.getDuracionEstimada() : 60);
            ps.setBoolean(6, true); // activo por defecto

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    servicio.setId(keys.getInt(1));
                }
            }
            return servicio;

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando servicio", e);
        }
    }

    @Override
    public Optional<Servicio> findById(Integer id) {
        if (id == null) return Optional.empty();

        String sql = "SELECT * FROM servicios WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando servicio por id", e);
        }
    }

    @Override
    public List<Servicio> findAll() {
        String sql = "SELECT * FROM servicios ORDER BY nombre";
        List<Servicio> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando servicios", e);
        }
    }

    @Override
    public Servicio update(Servicio servicio) {
        if (servicio.getId() == null) {
            throw new IllegalArgumentException("ID requerido para actualizar");
        }

        String sql = "UPDATE servicios SET codigo=?, nombre=?, descripcion=?, precio=?, duracion_estimada=? WHERE id=?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getDescripcion());
            ps.setBigDecimal(4, servicio.getPrecio());
            ps.setInt(5, servicio.getDuracionEstimada());
            ps.setInt(6, servicio.getId());

            ps.executeUpdate();
            return servicio;

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando servicio", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) return false;

        String sql = "UPDATE servicios SET activo = false WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando servicio", e);
        }
    }

    @Override
    public List<Servicio> findByActivo(boolean activo) {
        String sql = "SELECT * FROM servicios WHERE activo = ? ORDER BY nombre";
        List<Servicio> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando servicios por activo", e);
        }
    }

    public Optional<Servicio> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return Optional.empty();

        String sql = "SELECT * FROM servicios WHERE codigo = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando servicio por código", e);
        }
    }

    @Override
    public Optional<Servicio> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return Optional.empty();

        String sql = "SELECT * FROM servicios WHERE LOWER(nombre) = ? ORDER BY nombre";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando servicio por nombre", e);
        }
    }

    @Override
    public List<Servicio> findByCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) return List.of();
        String sql = "SELECT * FROM servicios WHERE LOWER(categoria) = ? ORDER BY nombre";
        List<Servicio> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando servicios por categoria", e);
        }
    }

    private Servicio mapRow(ResultSet rs) throws SQLException {
        Servicio servicio = new Servicio();
        servicio.setId(rs.getInt("id"));
        servicio.setCodigo(rs.getString("codigo"));
        servicio.setNombre(rs.getString("nombre"));
        servicio.setDescripcion(rs.getString("descripcion"));
        servicio.setPrecio(rs.getBigDecimal("precio"));
        servicio.setDuracionEstimada(rs.getInt("duracion_estimada"));
        return servicio;
    }
}