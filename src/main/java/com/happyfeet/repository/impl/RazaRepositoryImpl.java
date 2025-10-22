package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Raza;
import com.happyfeet.repository.RazaRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RazaRepositoryImpl implements RazaRepository {

    private final DatabaseConnection databaseConnection;

    public RazaRepositoryImpl() {
        this.databaseConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Raza save(Raza raza) {
        String sql = "INSERT INTO razas (nombre, especie_id) VALUES (?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, raza.getNombre());
            ps.setInt(2, raza.getEspecieId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    raza.setId(rs.getInt(1));
                }
            }

            return raza;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar raza: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Raza> findById(Long id) {
        String sql = "SELECT id, nombre, especie_id FROM razas WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRaza(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar raza por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Raza> findAll() {
        String sql = "SELECT id, nombre, especie_id FROM razas ORDER BY nombre";
        List<Raza> razas = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                razas.add(mapResultSetToRaza(rs));
            }

            return razas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las razas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Raza> findByEspecieId(Long especieId) {
        String sql = "SELECT id, nombre, especie_id FROM razas WHERE especie_id = ? ORDER BY nombre";
        List<Raza> razas = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, especieId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    razas.add(mapResultSetToRaza(rs));
                }
            }

            return razas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar razas por especie: " + e.getMessage(), e);
        }
    }

    @Override
    public Raza update(Raza raza) {
        String sql = "UPDATE razas SET nombre = ?, especie_id = ? WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, raza.getNombre());
            ps.setInt(2, raza.getEspecieId());
            ps.setInt(3, raza.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("No se pudo actualizar la raza con ID: " + raza.getId());
            }

            return raza;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar raza: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM razas WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar raza: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM razas WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de raza: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Raza> findById(Integer id) {
        String sql = "SELECT id, nombre, especie_id FROM razas WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRaza(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar raza por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Raza> findByEspecieId(Integer especieId) {
        String sql = "SELECT id, nombre, especie_id FROM razas WHERE especie_id = ? ORDER BY nombre";
        List<Raza> razas = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, especieId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    razas.add(mapResultSetToRaza(rs));
                }
            }

            return razas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar razas por especie: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Raza> findByNombreAndEspecieId(String nombre, Integer especieId) {
        String sql = "SELECT id, nombre, especie_id FROM razas WHERE nombre = ? AND especie_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, especieId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRaza(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar raza por nombre y especie: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Raza> findByNombre(String nombre) {
        String sql = "SELECT id, nombre, especie_id FROM razas WHERE nombre LIKE ? ORDER BY nombre";
        List<Raza> razas = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    razas.add(mapResultSetToRaza(rs));
                }
            }

            return razas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar razas por nombre: " + e.getMessage(), e);
        }
    }

    private Raza mapResultSetToRaza(ResultSet rs) throws SQLException {
        Raza raza = Raza.of(rs.getInt("especie_id"), rs.getString("nombre"));
        raza.setId(rs.getInt("id"));
        return raza;
    }
}