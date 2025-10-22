package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Recordatorio;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.RecordatorioRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC de RecordatorioRepository.
 * Persiste los recordatorios en la tabla 'recordatorios' de MySQL.
 */
public class RecordatorioRepositoryImpl implements RecordatorioRepository {

    private final DatabaseConnection db;

    public RecordatorioRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public void save(Recordatorio recordatorio) {
        if (recordatorio == null) {
            throw new IllegalArgumentException("Recordatorio no puede ser nulo");
        }

        String sql = "INSERT INTO recordatorios (tipo, mascota_id, dueno_id, cita_id, fecha_recordatorio, " +
                     "mensaje, enviado, fecha_envio, metodo_envio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, recordatorio.getTipo());

            if (recordatorio.getMascotaId() != null) {
                ps.setInt(2, recordatorio.getMascotaId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (recordatorio.getDuenoId() != null) {
                ps.setInt(3, recordatorio.getDuenoId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (recordatorio.getCitaId() != null) {
                ps.setInt(4, recordatorio.getCitaId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (recordatorio.getFechaRecordatorio() != null) {
                ps.setTimestamp(5, new Timestamp(recordatorio.getFechaRecordatorio().getTime()));
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }

            ps.setString(6, recordatorio.getMensaje());
            ps.setBoolean(7, recordatorio.isEnviado());

            if (recordatorio.getFechaEnvio() != null) {
                ps.setTimestamp(8, new Timestamp(recordatorio.getFechaEnvio().getTime()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setString(9, recordatorio.getMetodoEnvio() != null ? recordatorio.getMetodoEnvio() : "sistema");

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    recordatorio.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando recordatorio", e);
        }
    }

    @Override
    public Recordatorio findById(int id) {
        String sql = "SELECT id, tipo, mascota_id, dueno_id, cita_id, fecha_recordatorio, mensaje, " +
                     "enviado, fecha_envio, metodo_envio, fecha_creacion FROM recordatorios WHERE id = ?";

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
            throw new DataAccessException("Error buscando recordatorio por ID: " + id, e);
        }
    }

    @Override
    public List<Recordatorio> findAll() {
        String sql = "SELECT id, tipo, mascota_id, dueno_id, cita_id, fecha_recordatorio, mensaje, " +
                     "enviado, fecha_envio, metodo_envio, fecha_creacion FROM recordatorios " +
                     "ORDER BY fecha_recordatorio DESC";

        List<Recordatorio> recordatorios = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                recordatorios.add(mapRow(rs));
            }

            return recordatorios;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando recordatorios", e);
        }
    }

    @Override
    public void update(Recordatorio recordatorio) {
        if (recordatorio == null || recordatorio.getId() <= 0) {
            throw new IllegalArgumentException("Recordatorio invalido para actualizar");
        }

        String sql = "UPDATE recordatorios SET tipo = ?, mascota_id = ?, dueno_id = ?, cita_id = ?, " +
                     "fecha_recordatorio = ?, mensaje = ?, enviado = ?, fecha_envio = ?, metodo_envio = ? " +
                     "WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recordatorio.getTipo());

            if (recordatorio.getMascotaId() != null) {
                ps.setInt(2, recordatorio.getMascotaId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (recordatorio.getDuenoId() != null) {
                ps.setInt(3, recordatorio.getDuenoId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (recordatorio.getCitaId() != null) {
                ps.setInt(4, recordatorio.getCitaId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (recordatorio.getFechaRecordatorio() != null) {
                ps.setTimestamp(5, new Timestamp(recordatorio.getFechaRecordatorio().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setString(6, recordatorio.getMensaje());
            ps.setBoolean(7, recordatorio.isEnviado());

            if (recordatorio.getFechaEnvio() != null) {
                ps.setTimestamp(8, new Timestamp(recordatorio.getFechaEnvio().getTime()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setString(9, recordatorio.getMetodoEnvio());
            ps.setInt(10, recordatorio.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Recordatorio con ID " + recordatorio.getId() + " no encontrado para actualizar");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando recordatorio ID: " + recordatorio.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM recordatorios WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando recordatorio ID: " + id, e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Recordatorio.
     */
    private Recordatorio mapRow(ResultSet rs) throws SQLException {
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setId(rs.getInt("id"));
        recordatorio.setTipo(rs.getString("tipo"));

        int mascotaId = rs.getInt("mascota_id");
        if (!rs.wasNull()) {
            recordatorio.setMascotaId(mascotaId);
        }

        int duenoId = rs.getInt("dueno_id");
        if (!rs.wasNull()) {
            recordatorio.setDuenoId(duenoId);
        }

        int citaId = rs.getInt("cita_id");
        if (!rs.wasNull()) {
            recordatorio.setCitaId(citaId);
        }

        Timestamp fechaRecordatorio = rs.getTimestamp("fecha_recordatorio");
        if (fechaRecordatorio != null) {
            recordatorio.setFechaRecordatorio(new java.util.Date(fechaRecordatorio.getTime()));
        }

        recordatorio.setMensaje(rs.getString("mensaje"));
        recordatorio.setEnviado(rs.getBoolean("enviado"));

        Timestamp fechaEnvio = rs.getTimestamp("fecha_envio");
        if (fechaEnvio != null) {
            recordatorio.setFechaEnvio(new java.util.Date(fechaEnvio.getTime()));
        }

        recordatorio.setMetodoEnvio(rs.getString("metodo_envio"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            recordatorio.setFechaCreacion(new java.util.Date(fechaCreacion.getTime()));
        }

        return recordatorio;
    }
}