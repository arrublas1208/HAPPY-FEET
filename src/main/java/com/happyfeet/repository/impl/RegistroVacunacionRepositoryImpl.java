package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.RegistroVacunacion;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.RegistroVacunacionRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistroVacunacionRepositoryImpl implements RegistroVacunacionRepository {
    private final DatabaseConnection db;

    public RegistroVacunacionRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public RegistroVacunacion save(RegistroVacunacion registro) {
        String sql = "INSERT INTO registros_vacunacion_jornada (jornada_id, nombre_mascota, nombre_dueno, " +
                     "telefono, vacuna_aplicada, fecha_hora, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setInt(i++, registro.getJornadaId());
            ps.setString(i++, registro.getNombreMascota());
            ps.setString(i++, registro.getNombreDueno());
            ps.setString(i++, registro.getTelefono());
            ps.setString(i++, registro.getVacunaAplicada());
            ps.setTimestamp(i++, Timestamp.valueOf(registro.getFechaHora()));
            ps.setString(i, registro.getObservaciones());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    registro.setId(keys.getInt(1));
                }
            }
            return registro;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando registro de vacunación", e);
        }
    }

    @Override
    public Optional<RegistroVacunacion> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM registros_vacunacion_jornada WHERE id = ?";
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
            throw new DataAccessException("Error buscando registro", e);
        }
    }

    @Override
    public List<RegistroVacunacion> findByJornadaId(Integer jornadaId) {
        List<RegistroVacunacion> registros = new ArrayList<>();
        String sql = "SELECT * FROM registros_vacunacion_jornada WHERE jornada_id = ? ORDER BY fecha_hora DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jornadaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRow(rs));
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando registros por jornada", e);
        }
    }

    @Override
    public List<RegistroVacunacion> findAll() {
        List<RegistroVacunacion> registros = new ArrayList<>();
        String sql = "SELECT * FROM registros_vacunacion_jornada ORDER BY fecha_hora DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                registros.add(mapRow(rs));
            }
            return registros;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando registros", e);
        }
    }

    @Override
    public List<RegistroVacunacion> findByNombreMascota(String nombreMascota) {
        List<RegistroVacunacion> registros = new ArrayList<>();
        String sql = "SELECT * FROM registros_vacunacion_jornada WHERE nombre_mascota LIKE ? ORDER BY fecha_hora DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombreMascota + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRow(rs));
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando registros por mascota", e);
        }
    }

    @Override
    public List<RegistroVacunacion> findByNombreDueno(String nombreDueno) {
        List<RegistroVacunacion> registros = new ArrayList<>();
        String sql = "SELECT * FROM registros_vacunacion_jornada WHERE nombre_dueno LIKE ? ORDER BY fecha_hora DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombreDueno + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRow(rs));
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando registros por dueño", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM registros_vacunacion_jornada WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando registro", e);
        }
    }

    @Override
    public int countByJornadaId(Integer jornadaId) {
        String sql = "SELECT COUNT(*) FROM registros_vacunacion_jornada WHERE jornada_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jornadaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error contando registros", e);
        }
    }

    @Override
    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM registros_vacunacion_jornada";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error contando total de registros", e);
        }
    }

    private RegistroVacunacion mapRow(ResultSet rs) throws SQLException {
        RegistroVacunacion r = new RegistroVacunacion();
        r.setId(rs.getInt("id"));
        r.setJornadaId(rs.getInt("jornada_id"));
        r.setNombreMascota(rs.getString("nombre_mascota"));
        r.setNombreDueno(rs.getString("nombre_dueno"));
        r.setTelefono(rs.getString("telefono"));
        r.setVacunaAplicada(rs.getString("vacuna_aplicada"));

        Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        if (fechaHora != null) {
            r.setFechaHora(fechaHora.toLocalDateTime());
        }

        r.setObservaciones(rs.getString("observaciones"));
        return r;
    }
}