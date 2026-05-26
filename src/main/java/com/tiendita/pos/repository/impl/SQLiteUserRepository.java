package com.tiendita.pos.repository.impl;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.Role;
import com.tiendita.pos.model.User;
import com.tiendita.pos.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación en SQLite/JDBC para la gestión de usuarios.
 */
public class SQLiteUserRepository implements UserRepository {

    @Override
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT id, username, password_hash, role, active FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void save(User user) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, role, active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setInt(4, user.isActive() ? 1 : 0);
            ps.executeUpdate();
            
            // Recuperar ID autogenerado
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(User user) throws Exception {
        String sql = "UPDATE users SET password_hash = ?, role = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getPasswordHash());
            ps.setString(2, user.getRole().name());
            ps.setInt(3, user.isActive() ? 1 : 0);
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, active FROM users ORDER BY username ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private User mapRow(ResultSet rs) throws Exception {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            Role.valueOf(rs.getString("role")),
            rs.getInt("active") == 1
        );
    }
}
