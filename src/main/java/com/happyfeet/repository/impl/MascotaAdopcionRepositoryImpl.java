package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.MascotaAdopcion;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.MascotaAdopcionRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MascotaAdopcionRepositoryImpl implements MascotaAdopcionRepository {
    private final DatabaseConnection db;

    public MascotaAdopcionRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public MascotaAdopcion save(MascotaAdopcion mascota) {
        if (mascota.getId() == null) {
            return insert(mascota);
        } else {
            return update(mascota);
        }
    }

    private MascotaAdopcion insert(MascotaAdopcion mascota) {
        String sql = "INSERT INTO mascotas_adopcion (nombre, especie, raza, edad_meses, sexo, descripcion, " +
                     "necesidades_especiales, fecha_ingreso, adoptada, contacto_responsable, foto_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setString(i++, mascota.getNombre());
            ps.setString(i++, mascota.getEspecie());
            ps.setString(i++, mascota.getRaza());
            ps.setInt(i++, mascota.getEdadMeses());
            ps.setString(i++, mascota.getSexo().name());
            ps.setString(i++, mascota.getDescripcion());
            ps.setString(i++, mascota.getNecesidadesEspeciales());
            ps.setDate(i++, Date.valueOf(mascota.getFechaIngreso()));
            ps.setBoolean(i++, mascota.getAdoptada());
            ps.setString(i++, mascota.getContactoResponsable());
            ps.setString(i, mascota.getFotoUrl());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    mascota.setId(keys.getInt(1));
                }
            }
            return mascota;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando mascota en adopción", e);
        }
    }

    private MascotaAdopcion update(MascotaAdopcion mascota) {
        String sql = "UPDATE mascotas_adopcion SET nombre=?, especie=?, raza=?, edad_meses=?, sexo=?, " +
                     "descripcion=?, necesidades_especiales=?, fecha_ingreso=?, adoptada=?, " +
                     "contacto_responsable=?, foto_url=?, dueno_adoptante_id=?, fecha_adopcion=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, mascota.getNombre());
            ps.setString(i++, mascota.getEspecie());
            ps.setString(i++, mascota.getRaza());
            ps.setInt(i++, mascota.getEdadMeses());
            ps.setString(i++, mascota.getSexo().name());
            ps.setString(i++, mascota.getDescripcion());
            ps.setString(i++, mascota.getNecesidadesEspeciales());
            ps.setDate(i++, Date.valueOf(mascota.getFechaIngreso()));
            ps.setBoolean(i++, mascota.getAdoptada());
            ps.setString(i++, mascota.getContactoResponsable());
            ps.setString(i++, mascota.getFotoUrl());

            if (mascota.getDuenoAdoptanteId() != null) {
                ps.setInt(i++, mascota.getDuenoAdoptanteId());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }

            if (mascota.getFechaAdopcion() != null) {
                ps.setDate(i++, Date.valueOf(mascota.getFechaAdopcion()));
            } else {
                ps.setNull(i++, Types.DATE);
            }

            ps.setInt(i, mascota.getId());
            ps.executeUpdate();
            return mascota;
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando mascota en adopción", e);
        }
    }

    @Override
    public Optional<MascotaAdopcion> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM mascotas_adopcion WHERE id = ?";
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
            throw new DataAccessException("Error buscando mascota en adopción por id", e);
        }
    }

    @Override
    public List<MascotaAdopcion> findAll() {
        List<MascotaAdopcion> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas_adopcion ORDER BY fecha_ingreso DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mascotas.add(mapRow(rs));
            }
            return mascotas;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando mascotas en adopción", e);
        }
    }

    @Override
    public List<MascotaAdopcion> findDisponibles() {
        List<MascotaAdopcion> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas_adopcion WHERE adoptada = 0 ORDER BY fecha_ingreso";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mascotas.add(mapRow(rs));
            }
            return mascotas;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando mascotas disponibles", e);
        }
    }

    @Override
    public List<MascotaAdopcion> findAdoptadas() {
        List<MascotaAdopcion> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas_adopcion WHERE adoptada = 1 ORDER BY fecha_adopcion DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mascotas.add(mapRow(rs));
            }
            return mascotas;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando mascotas adoptadas", e);
        }
    }

    @Override
    public List<MascotaAdopcion> findByEspecie(String especie) {
        List<MascotaAdopcion> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas_adopcion WHERE especie LIKE ? ORDER BY fecha_ingreso";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + especie + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapRow(rs));
                }
            }
            return mascotas;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando mascotas por especie", e);
        }
    }

    @Override
    public List<MascotaAdopcion> findByNombre(String nombre) {
        List<MascotaAdopcion> mascotas = new ArrayList<>();
        String sql = "SELECT * FROM mascotas_adopcion WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapRow(rs));
                }
            }
            return mascotas;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando mascotas por nombre", e);
        }
    }

    @Override
    public boolean marcarComoAdoptada(Integer id, Integer duenoId) {
        String sql = "UPDATE mascotas_adopcion SET adoptada = 1, dueno_adoptante_id = ?, " +
                     "fecha_adopcion = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error marcando mascota como adoptada", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM mascotas_adopcion WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando mascota en adopción", e);
        }
    }

    @Override
    public long countDisponibles() {
        String sql = "SELECT COUNT(*) FROM mascotas_adopcion WHERE adoptada = 0";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error contando mascotas disponibles", e);
        }
    }

    @Override
    public long countAdoptadas() {
        String sql = "SELECT COUNT(*) FROM mascotas_adopcion WHERE adoptada = 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error contando mascotas adoptadas", e);
        }
    }

    private MascotaAdopcion mapRow(ResultSet rs) throws SQLException {
        MascotaAdopcion mascota = new MascotaAdopcion();
        mascota.setId(rs.getInt("id"));
        mascota.setNombre(rs.getString("nombre"));
        mascota.setEspecie(rs.getString("especie"));
        mascota.setRaza(rs.getString("raza"));
        mascota.setEdadMeses(rs.getInt("edad_meses"));
        mascota.setSexo(MascotaAdopcion.Sexo.fromString(rs.getString("sexo")));
        mascota.setDescripcion(rs.getString("descripcion"));
        mascota.setNecesidadesEspeciales(rs.getString("necesidades_especiales"));

        Date fechaIngreso = rs.getDate("fecha_ingreso");
        if (fechaIngreso != null) {
            mascota.setFechaIngreso(fechaIngreso.toLocalDate());
        }

        mascota.setAdoptada(rs.getBoolean("adoptada"));
        mascota.setContactoResponsable(rs.getString("contacto_responsable"));
        mascota.setFotoUrl(rs.getString("foto_url"));

        int duenoId = rs.getInt("dueno_adoptante_id");
        if (!rs.wasNull()) {
            mascota.setDuenoAdoptanteId(duenoId);
        }

        Date fechaAdopcion = rs.getDate("fecha_adopcion");
        if (fechaAdopcion != null) {
            mascota.setFechaAdopcion(fechaAdopcion.toLocalDate());
        }

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            mascota.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            mascota.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return mascota;
    }
}