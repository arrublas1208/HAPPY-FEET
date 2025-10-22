package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.HistorialMedico;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map;

public class HistorialMedicoRepositoryImpl implements HistorialMedicoRepository {
    private final DatabaseConnection db;

    public HistorialMedicoRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public HistorialMedico save(HistorialMedico historial) {
        String sql = "INSERT INTO historial_medico (mascota_id, cita_id, evento_tipo_id, fecha_evento, veterinario_id, " +
                "temperatura, frecuencia_cardiaca, frecuencia_respiratoria, peso, sintomas, diagnostico, " +
                "tratamiento_prescrito, medicamentos_recetados, observaciones, recomendaciones, " +
                "fecha_proximo_control, requiere_seguimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            ps.setInt(i++, historial.getMascotaId());

            if (historial.getCitaId() != null) {
                ps.setLong(i++, historial.getCitaId());
            } else {
                ps.setNull(i++, Types.BIGINT);
            }

            ps.setInt(i++, historial.getEventoMedicoId());
            ps.setTimestamp(i++, Timestamp.valueOf(historial.getFechaEvento()));
            ps.setInt(i++, historial.getVeterinarioId());

            setBigDecimalNullable(ps, i++, historial.getTemperatura());
            setIntegerNullable(ps, i++, historial.getFrecuenciaCardiaca());
            setIntegerNullable(ps, i++, historial.getFrecuenciaRespiratoria());
            setBigDecimalNullable(ps, i++, historial.getPeso());

            ps.setString(i++, historial.getSintomas());
            ps.setString(i++, historial.getDiagnostico());
            ps.setString(i++, historial.getTratamientoPrescrito());
            ps.setString(i++, historial.getMedicamentosRecetados());
            ps.setString(i++, historial.getObservaciones());
            ps.setString(i++, historial.getRecomendaciones());

            if (historial.getFechaProximoControl() != null) {
                ps.setDate(i++, Date.valueOf(historial.getFechaProximoControl()));
            } else {
                ps.setNull(i++, Types.DATE);
            }

            ps.setBoolean(i++, historial.getRequiereSeguimiento() != null ? historial.getRequiereSeguimiento() : false);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    historial.setId(keys.getInt(1));
                }
            }

            return historial;

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando historial médico", e);
        }
    }

    @Override
    public HistorialMedico update(HistorialMedico historial) {
        if (historial.getId() == null) {
            throw new IllegalArgumentException("ID requerido para actualizar");
        }

        String sql = "UPDATE historial_medico SET mascota_id=?, cita_id=?, evento_tipo_id=?, fecha_evento=?, veterinario_id=?, " +
                "temperatura=?, frecuencia_cardiaca=?, frecuencia_respiratoria=?, peso=?, sintomas=?, diagnostico=?, " +
                "tratamiento_prescrito=?, medicamentos_recetados=?, observaciones=?, recomendaciones=?, " +
                "fecha_proximo_control=?, requiere_seguimiento=? WHERE id=?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            ps.setInt(i++, historial.getMascotaId());

            if (historial.getCitaId() != null) {
                ps.setLong(i++, historial.getCitaId());
            } else {
                ps.setNull(i++, Types.BIGINT);
            }

            ps.setInt(i++, historial.getEventoMedicoId());
            ps.setTimestamp(i++, Timestamp.valueOf(historial.getFechaEvento()));
            ps.setInt(i++, historial.getVeterinarioId());

            setBigDecimalNullable(ps, i++, historial.getTemperatura());
            setIntegerNullable(ps, i++, historial.getFrecuenciaCardiaca());
            setIntegerNullable(ps, i++, historial.getFrecuenciaRespiratoria());
            setBigDecimalNullable(ps, i++, historial.getPeso());

            ps.setString(i++, historial.getSintomas());
            ps.setString(i++, historial.getDiagnostico());
            ps.setString(i++, historial.getTratamientoPrescrito());
            ps.setString(i++, historial.getMedicamentosRecetados());
            ps.setString(i++, historial.getObservaciones());
            ps.setString(i++, historial.getRecomendaciones());

            if (historial.getFechaProximoControl() != null) {
                ps.setDate(i++, Date.valueOf(historial.getFechaProximoControl()));
            } else {
                ps.setNull(i++, Types.DATE);
            }

            ps.setBoolean(i++, historial.getRequiereSeguimiento() != null ? historial.getRequiereSeguimiento() : false);

            ps.setInt(i, historial.getId());

            ps.executeUpdate();
            return historial;

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando historial médico", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) return false;
        String sql = "DELETE FROM historial_medico WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando historial médico", e);
        }
    }

    @Override
    public Optional<HistorialMedico> findById(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM historial_medico WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando historial médico por id", e);
        }
    }

    @Override
    public List<HistorialMedico> findAll() {
        String sql = "SELECT * FROM historial_medico ORDER BY fecha_evento DESC";
        List<HistorialMedico> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error listando historiales médicos", e);
        }
    }

    @Override
    public List<HistorialMedico> findByMascotaId(Integer mascotaId) {
        if (mascotaId == null) return List.of();
        String sql = "SELECT * FROM historial_medico WHERE mascota_id = ? ORDER BY fecha_evento DESC";
        List<HistorialMedico> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mascotaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando historiales por mascota", e);
        }
    }

    @Override
    public List<HistorialMedico> findByFechaEventoBetween(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) return List.of();
        String sql = "SELECT * FROM historial_medico WHERE fecha_evento BETWEEN ? AND ? ORDER BY fecha_evento";
        List<HistorialMedico> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando historiales por rango de fechas", e);
        }
    }

    @Override
    public List<HistorialMedico> findByVeterinarioId(Integer veterinarioId) {
        if (veterinarioId == null) return List.of();
        String sql = "SELECT * FROM historial_medico WHERE veterinario_id = ? ORDER BY fecha_evento DESC";
        List<HistorialMedico> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, veterinarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando historiales por veterinario", e);
        }
    }

    @Override
    public Optional<HistorialMedico> findByMascotaIdAndFechaEvento(Integer mascotaId, LocalDateTime fechaEvento) {
        if (mascotaId == null || fechaEvento == null) return Optional.empty();
        String sql = "SELECT * FROM historial_medico WHERE mascota_id = ? AND fecha_evento = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mascotaId);
            ps.setTimestamp(2, Timestamp.valueOf(fechaEvento));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando historial por mascota y fecha", e);
        }
    }

    @Override
    public List<HistorialMedico> findByRequiereSeguimientoTrue() {
        String sql = "SELECT * FROM historial_medico WHERE requiere_seguimiento = true ORDER BY fecha_evento DESC";
        List<HistorialMedico> list = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando historiales que requieren seguimiento", e);
        }
    }

    @Override
    public List<Map<String, Object>> obtenerServiciosMasSolicitados(int limite) {
        String sql = "SELECT et.nombre AS tipo_evento_medico, COUNT(*) AS cantidad " +
                     "FROM historial_medico hm " +
                     "JOIN evento_tipos et ON hm.evento_tipo_id = et.id " +
                     "GROUP BY et.nombre " +
                     "ORDER BY cantidad DESC " +
                     "LIMIT ?";

        List<Map<String, Object>> resultados = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("tipo_evento_medico", rs.getString("tipo_evento_medico"));
                    fila.put("cantidad", rs.getLong("cantidad"));
                    resultados.add(fila);
                }
            }

            return resultados;

        } catch (SQLException e) {
            throw new DataAccessException("Error obteniendo servicios más solicitados", e);
        }
    }

    @Override
    public List<Map<String, Object>> obtenerDesempenoVeterinarios() {
        String sql = "SELECT veterinario_id, COUNT(*) AS total_consultas " +
                     "FROM historial_medico " +
                     "WHERE veterinario_id IS NOT NULL " +
                     "GROUP BY veterinario_id " +
                     "ORDER BY total_consultas DESC";

        List<Map<String, Object>> resultados = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("veterinario_id", rs.getInt("veterinario_id"));
                fila.put("total_consultas", rs.getLong("total_consultas"));
                resultados.add(fila);
            }

            return resultados;

        } catch (SQLException e) {
            throw new DataAccessException("Error obteniendo desempeño de veterinarios", e);
        }
    }

    private HistorialMedico mapRow(ResultSet rs) throws SQLException {
        HistorialMedico h = HistorialMedico.newForPersistence();
        h.setId(rs.getInt("id"));
        h.setMascotaId(rs.getInt("mascota_id"));
        h.setCitaId(getLongNullable(rs, "cita_id"));
        h.setEventoMedicoId(rs.getInt("evento_tipo_id"));
        Timestamp ts = rs.getTimestamp("fecha_evento");
        h.setFechaEvento(ts != null ? ts.toLocalDateTime() : null);
        h.setVeterinarioId(rs.getInt("veterinario_id"));
        h.setTemperatura(getBigDecimalNullable(rs, "temperatura"));
        h.setFrecuenciaCardiaca(getIntegerNullable(rs, "frecuencia_cardiaca"));
        h.setFrecuenciaRespiratoria(getIntegerNullable(rs, "frecuencia_respiratoria"));
        h.setPeso(getBigDecimalNullable(rs, "peso"));
        h.setSintomas(rs.getString("sintomas"));
        h.setDiagnostico(rs.getString("diagnostico"));
        h.setTratamientoPrescrito(rs.getString("tratamiento_prescrito"));
        h.setMedicamentosRecetados(rs.getString("medicamentos_recetados"));
        h.setObservaciones(rs.getString("observaciones"));
        h.setRecomendaciones(rs.getString("recomendaciones"));
        Date fpc = rs.getDate("fecha_proximo_control");
        h.setFechaProximoControl(fpc != null ? fpc.toLocalDate() : null);
        h.setRequiereSeguimiento(rs.getBoolean("requiere_seguimiento"));
        Timestamp fr = null;
        try { fr = rs.getTimestamp("fecha_registro"); } catch (SQLException ignored) {}
        h.setFechaRegistro(fr != null ? fr.toLocalDateTime() : null);
        return h;
    }

    // Helper methods para manejar valores nullable
    private void setBigDecimalNullable(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        if (value != null) {
            ps.setBigDecimal(index, value);
        } else {
            ps.setNull(index, Types.DECIMAL);
        }
    }

    private void setIntegerNullable(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(index, value);
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }

    private Long getLongNullable(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private Integer getIntegerNullable(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private BigDecimal getBigDecimalNullable(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return rs.wasNull() ? null : value;
    }
}
