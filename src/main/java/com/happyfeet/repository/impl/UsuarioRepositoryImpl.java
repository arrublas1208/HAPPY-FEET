package com.happyfeet.repository.impl;

import com.happyfeet.model.entities.Usuario;
import com.happyfeet.repository.DataAccessException;
import com.happyfeet.repository.UsuarioRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC de UsuarioRepository.
 * Persiste los usuarios en la tabla 'usuarios' de MySQL.
 */
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final DatabaseConnection db;

    public UsuarioRepositoryImpl() {
        this.db = DatabaseConnection.getInstance();
    }

    @Override
    public void save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser nulo");
        }

        String sql = "INSERT INTO usuarios (username, email, password_hash, nombre_completo, rol, " +
                     "veterinario_id, activo, fecha_ultimo_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getNombreCompleto());
            ps.setString(5, usuario.getRol() != null ? usuario.getRol() : "recepcion");

            if (usuario.getVeterinarioId() != null) {
                ps.setInt(6, usuario.getVeterinarioId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setBoolean(7, usuario.isActivo());

            if (usuario.getFechaUltimoLogin() != null) {
                ps.setTimestamp(8, new Timestamp(usuario.getFechaUltimoLogin().getTime()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error guardando usuario", e);
        }
    }

    @Override
    public Usuario findById(int id) {
        String sql = "SELECT id, username, email, password_hash, nombre_completo, rol, veterinario_id, " +
                     "activo, fecha_ultimo_login, fecha_creacion, fecha_actualizacion FROM usuarios WHERE id = ?";

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
            throw new DataAccessException("Error buscando usuario por ID: " + id, e);
        }
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT id, username, email, password_hash, nombre_completo, rol, veterinario_id, " +
                     "activo, fecha_ultimo_login, fecha_creacion, fecha_actualizacion FROM usuarios " +
                     "ORDER BY nombre_completo";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapRow(rs));
            }

            return usuarios;

        } catch (SQLException e) {
            throw new DataAccessException("Error listando usuarios", e);
        }
    }

    @Override
    public void update(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            throw new IllegalArgumentException("Usuario invalido para actualizar");
        }

        String sql = "UPDATE usuarios SET username = ?, email = ?, password_hash = ?, nombre_completo = ?, " +
                     "rol = ?, veterinario_id = ?, activo = ?, fecha_ultimo_login = ? WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getNombreCompleto());
            ps.setString(5, usuario.getRol());

            if (usuario.getVeterinarioId() != null) {
                ps.setInt(6, usuario.getVeterinarioId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setBoolean(7, usuario.isActivo());

            if (usuario.getFechaUltimoLogin() != null) {
                ps.setTimestamp(8, new Timestamp(usuario.getFechaUltimoLogin().getTime()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setInt(9, usuario.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Usuario con ID " + usuario.getId() + " no encontrado para actualizar");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando usuario ID: " + usuario.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error eliminando usuario ID: " + id, e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Usuario.
     */
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setRol(rs.getString("rol"));

        int veterinarioId = rs.getInt("veterinario_id");
        if (!rs.wasNull()) {
            usuario.setVeterinarioId(veterinarioId);
        }

        usuario.setActivo(rs.getBoolean("activo"));

        Timestamp fechaUltimoLogin = rs.getTimestamp("fecha_ultimo_login");
        if (fechaUltimoLogin != null) {
            usuario.setFechaUltimoLogin(new java.util.Date(fechaUltimoLogin.getTime()));
        }

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            usuario.setFechaCreacion(new java.util.Date(fechaCreacion.getTime()));
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            usuario.setFechaActualizacion(new java.util.Date(fechaActualizacion.getTime()));
        }

        return usuario;
    }
}