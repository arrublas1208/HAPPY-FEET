package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Especie;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.EspecieRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EspecieRepositoryImpl implements EspecieRepository {
    private final DatabaseConnection db;

    public EspecieRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public Especie save(Especie especie) {
        String sql = "INSERT INTO especies (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, especie.getNombre());
            ps.setString(2, especie.getDescripcion());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    especie.setId(keys.getInt(1));
                }
            }
            return especie;

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando especie", e);
        }
    }

    @Override
    public Optional<Especie> findById(Integer id) {
        if (id == null) return Optional.empty();

        String sql = "SELECT * FROM especies WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando especie por id", e);
        }
    }

    @Override
    public List<Especie> findAll() {
        String sql = "SELECT * FROM especies ORDER BY nombre";
        List<Especie> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando especies", e);
        }
    }

    @Override
    public Especie update(Especie especie) {
        if (especie.getId() == null) {
            throw new IllegalArgumentException("ID requerido para actualizar");
        }

        String sql = "UPDATE especies SET nombre=?, descripcion=? WHERE id=?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, especie.getNombre());
            ps.setString(2, especie.getDescripcion());
            ps.setInt(3, especie.getId());

            ps.executeUpdate();
            return especie;

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando especie", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) return false;

        String sql = "DELETE FROM especies WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando especie", e);
        }
    }

    @Override
    public Optional<Especie> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return Optional.empty();

        String sql = "SELECT * FROM especies WHERE LOWER(nombre) = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DataAccessException("Error buscando especie por nombre", e);
        }
    }

    private Especie mapRow(ResultSet rs) throws SQLException {
        Especie especie = new Especie.Builder(rs.getString("nombre"))
                .withDescripcion(rs.getString("descripcion"))
                .build();

        especie.setId(rs.getInt("id"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            especie.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        return especie;
    }
}