package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Mascota;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.MascotaRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MascotaRepositoryImpl implements MascotaRepository {
    private final DatabaseConnection db;

    public MascotaRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    // ------------ CRUD ------------
    @Override
    public Mascota save(Mascota m) {
        String sql = "INSERT INTO mascotas (dueno_id, nombre, raza_id, fecha_nacimiento, sexo, color, senias_particulares, url_foto, alergias, condiciones_preexistentes, peso_actual, microchip, fecha_implantacion_microchip, agresivo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setInt(i++, m.getDuenoId());
            ps.setString(i++, m.getNombre());
            ps.setInt(i++, m.getRazaId());
            if (m.getFechaNacimiento() != null) ps.setDate(i++, Date.valueOf(m.getFechaNacimiento())); else ps.setNull(i++, Types.DATE);
            ps.setString(i++, sexoToDb(m.getSexo()));
            ps.setString(i++, m.getColor());
            ps.setString(i++, m.getSeniasParticulares());
            ps.setString(i++, m.getUrlFoto());
            ps.setString(i++, m.getAlergias());
            ps.setString(i++, m.getCondicionesPreexistentes());
            if (m.getPesoActual() != null) ps.setDouble(i++, m.getPesoActual()); else ps.setNull(i++, Types.DECIMAL);
            ps.setString(i++, m.getMicrochip());
            if (m.getFechaImplantacionMicrochip() != null) ps.setDate(i++, Date.valueOf(m.getFechaImplantacionMicrochip())); else ps.setNull(i++, Types.DATE);
            if (m.getAgresivo() != null) ps.setBoolean(i++, m.getAgresivo()); else ps.setNull(i++, Types.BOOLEAN);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                }
            }
            return m;
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando mascota", e);
        }
    }

    @Override
    public Optional<Mascota> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM mascotas WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando mascota por id", e);
        }
    }

    @Override
    public List<Mascota> findAll() {
        String sql = "SELECT * FROM mascotas";
        List<Mascota> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando mascotas", e);
        }
    }

    @Override
    public Mascota update(Mascota m) {
        if (m.getId() == null) throw new IllegalArgumentException("id requerido para actualizar");
        String sql = "UPDATE mascotas SET dueno_id=?, nombre=?, raza_id=?, fecha_nacimiento=?, sexo=?, color=?, senias_particulares=?, url_foto=?, alergias=?, condiciones_preexistentes=?, peso_actual=?, microchip=?, fecha_implantacion_microchip=?, agresivo=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, m.getDuenoId());
            ps.setString(i++, m.getNombre());
            ps.setInt(i++, m.getRazaId());
            if (m.getFechaNacimiento() != null) ps.setDate(i++, Date.valueOf(m.getFechaNacimiento())); else ps.setNull(i++, Types.DATE);
            ps.setString(i++, sexoToDb(m.getSexo()));
            ps.setString(i++, m.getColor());
            ps.setString(i++, m.getSeniasParticulares());
            ps.setString(i++, m.getUrlFoto());
            ps.setString(i++, m.getAlergias());
            ps.setString(i++, m.getCondicionesPreexistentes());
            if (m.getPesoActual() != null) ps.setDouble(i++, m.getPesoActual()); else ps.setNull(i++, Types.DECIMAL);
            ps.setString(i++, m.getMicrochip());
            if (m.getFechaImplantacionMicrochip() != null) ps.setDate(i++, Date.valueOf(m.getFechaImplantacionMicrochip())); else ps.setNull(i++, Types.DATE);
            if (m.getAgresivo() != null) ps.setBoolean(i++, m.getAgresivo()); else ps.setNull(i++, Types.BOOLEAN);
            ps.setInt(i, m.getId());
            ps.executeUpdate();
            return m;
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando mascota", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) return false;
        String sql = "DELETE FROM mascotas WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando mascota", e);
        }
    }

    // ------------ Consultas específicas ------------
    @Override
    public List<Mascota> findByDuenoId(Integer duenoId) {
        if (duenoId == null) return List.of();
        String sql = "SELECT * FROM mascotas WHERE dueno_id=?";
        List<Mascota> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duenoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando mascota por duenoId", e);
        }
    }

    @Override
    public List<Mascota> findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return List.of();
        String sql = "SELECT * FROM mascotas WHERE LOWER(nombre) LIKE ?";
        List<Mascota> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre.trim().toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando mascota por nombre", e);
        }
    }

    @Override
    public Mascota findByMicrochip(String microchip) {
        if (microchip == null || microchip.isBlank()) return null;
        String sql = "SELECT * FROM mascotas WHERE microchip = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, microchip.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando mascota por microchip", e);
        }
    }

    // ------------ Helpers ------------
    private Mascota mapRow(ResultSet rs) throws SQLException {
        LocalDate fnac = rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null;
        LocalDate fchip = rs.getDate("fecha_implantacion_microchip") != null ? rs.getDate("fecha_implantacion_microchip").toLocalDate() : null;
        Timestamp fr = rs.getTimestamp("fecha_registro");
        Timestamp fa = rs.getTimestamp("fecha_actualizacion");
        LocalDateTime fechaReg = fr != null ? fr.toLocalDateTime() : null;
        LocalDateTime fechaAct = fa != null ? fa.toLocalDateTime() : null;

        return Mascota.Builder.create()
                .withId(rs.getInt("id"))
                .withDuenoId(rs.getInt("dueno_id"))
                .withNombre(rs.getString("nombre"))
                .withRazaId(rs.getInt("raza_id"))
                .withFechaNacimiento(fnac)
                .withSexo(dbToSexo(rs.getString("sexo")))
                .withColor(rs.getString("color"))
                .withSeniasParticulares(rs.getString("senias_particulares"))
                .withUrlFoto(rs.getString("url_foto"))
                .withAlergias(rs.getString("alergias"))
                .withCondicionesPreexistentes(rs.getString("condiciones_preexistentes"))
                .withPesoActual(getDoubleNullable(rs, "peso_actual"))
                .withMicrochip(rs.getString("microchip"))
                .withFechaImplantacionMicrochip(fchip)
                .withAgresivo(getBooleanNullable(rs, "agresivo"))
                .withFechaRegistro(fechaReg)
                .withFechaActualizacion(fechaAct)
                .build();
    }

    private static String sexoToDb(Mascota.Sexo s) {
        if (s == null) return null;
        return s == Mascota.Sexo.MACHO ? "Macho" : "Hembra";
    }

    private static Mascota.Sexo dbToSexo(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase();
        if (v.startsWith("m")) return Mascota.Sexo.MACHO;
        if (v.startsWith("h")) return Mascota.Sexo.HEMBRA;
        return null;
    }

    private static Double getDoubleNullable(ResultSet rs, String col) throws SQLException {
        double d = rs.getDouble(col);
        return rs.wasNull() ? null : d;
        }

    private static Boolean getBooleanNullable(ResultSet rs, String col) throws SQLException {
        boolean b = rs.getBoolean(col);
        return rs.wasNull() ? null : b;
    }
}
