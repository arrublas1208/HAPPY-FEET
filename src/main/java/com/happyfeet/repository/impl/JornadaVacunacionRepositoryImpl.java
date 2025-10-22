package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.JornadaVacunacionRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JornadaVacunacionRepositoryImpl implements JornadaVacunacionRepository {
    private final DatabaseConnection db;

    public JornadaVacunacionRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public JornadaVacunacion save(JornadaVacunacion jornada) {
        if (jornada.getId() == null) {
            return insert(jornada);
        } else {
            return update(jornada);
        }
    }

    private JornadaVacunacion insert(JornadaVacunacion j) {
        String sql = "INSERT INTO jornadas_vacunacion (nombre, fecha, ubicacion, " +
                     "vacunas_disponibles, precio_especial, capacidad_maxima, activa) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setString(i++, j.getNombre());
            ps.setDate(i++, Date.valueOf(j.getFecha()));
            ps.setString(i++, j.getUbicacion());
            ps.setString(i++, j.getVacunasDisponibles());
            ps.setBigDecimal(i++, j.getPrecioEspecial());
            ps.setInt(i++, j.getCapacidadMaxima());
            ps.setBoolean(i, j.getActiva());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    j.setId(keys.getInt(1));
                }
            }
            return j;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando jornada", e);
        }
    }

    private JornadaVacunacion update(JornadaVacunacion j) {
        String sql = "UPDATE jornadas_vacunacion SET nombre=?, fecha=?, ubicacion=?, " +
                     "vacunas_disponibles=?, precio_especial=?, capacidad_maxima=?, activa=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, j.getNombre());
            ps.setDate(i++, Date.valueOf(j.getFecha()));
            ps.setString(i++, j.getUbicacion());
            ps.setString(i++, j.getVacunasDisponibles());
            ps.setBigDecimal(i++, j.getPrecioEspecial());
            ps.setInt(i++, j.getCapacidadMaxima());
            ps.setBoolean(i++, j.getActiva());
            ps.setInt(i, j.getId());
            ps.executeUpdate();
            return j;
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando jornada", e);
        }
    }

    @Override
    public Optional<JornadaVacunacion> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM jornadas_vacunacion WHERE id = ?";
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
            throw new DataAccessException("Error buscando jornada", e);
        }
    }

    @Override
    public List<JornadaVacunacion> findAll() {
        List<JornadaVacunacion> jornadas = new ArrayList<>();
        String sql = "SELECT * FROM jornadas_vacunacion ORDER BY fecha DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jornadas.add(mapRow(rs));
            }
            return jornadas;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando jornadas", e);
        }
    }

    @Override
    public List<JornadaVacunacion> findActivas() {
        List<JornadaVacunacion> jornadas = new ArrayList<>();
        String sql = "SELECT * FROM jornadas_vacunacion WHERE activa = 1 ORDER BY fecha";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jornadas.add(mapRow(rs));
            }
            return jornadas;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando jornadas activas", e);
        }
    }

    @Override
    public List<JornadaVacunacion> findByFechaRange(LocalDate inicio, LocalDate fin) {
        List<JornadaVacunacion> jornadas = new ArrayList<>();
        String sql = "SELECT * FROM jornadas_vacunacion WHERE fecha BETWEEN ? AND ? ORDER BY fecha";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jornadas.add(mapRow(rs));
                }
            }
            return jornadas;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando jornadas por rango", e);
        }
    }

    @Override
    public List<JornadaVacunacion> findFuturasActivas() {
        List<JornadaVacunacion> jornadas = new ArrayList<>();
        String sql = "SELECT * FROM jornadas_vacunacion WHERE activa = 1 AND fecha >= CURDATE() ORDER BY fecha";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jornadas.add(mapRow(rs));
            }
            return jornadas;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando jornadas futuras", e);
        }
    }

    @Override
    public int countRegistrosByJornada(Integer jornadaId) {
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
    public boolean cerrarJornada(Integer id) {
        String sql = "UPDATE jornadas_vacunacion SET activa = 0 WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error cerrando jornada", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM jornadas_vacunacion WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando jornada", e);
        }
    }

    @Override
    public long countActivas() {
        String sql = "SELECT COUNT(*) FROM jornadas_vacunacion WHERE activa = 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error contando jornadas activas", e);
        }
    }

    private JornadaVacunacion mapRow(ResultSet rs) throws SQLException {
        JornadaVacunacion j = new JornadaVacunacion();
        j.setId(rs.getInt("id"));
        j.setNombre(rs.getString("nombre"));

        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            j.setFecha(fecha.toLocalDate());
        }

        j.setUbicacion(rs.getString("ubicacion"));
        j.setVacunasDisponibles(rs.getString("vacunas_disponibles"));
        j.setPrecioEspecial(rs.getBigDecimal("precio_especial"));
        j.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        j.setActiva(rs.getBoolean("activa"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            j.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            j.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return j;
    }
}