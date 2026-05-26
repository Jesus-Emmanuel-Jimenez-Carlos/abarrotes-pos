package com.tiendita.pos.repository.impl;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.Provider;
import com.tiendita.pos.repository.ProviderRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite/JDBC para la administración de proveedores de mercancías.
 */
public class SQLiteProviderRepository implements ProviderRepository {

    @Override
    public void save(Provider provider) throws Exception {
        String sql = "INSERT INTO providers (name, contact_name, phone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, provider.getName());
            ps.setString(2, provider.getContactName());
            ps.setString(3, provider.getPhone());
            ps.setString(4, provider.getEmail());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    provider.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Provider provider) throws Exception {
        String sql = "UPDATE providers SET name = ?, contact_name = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, provider.getName());
            ps.setString(2, provider.getContactName());
            ps.setString(3, provider.getPhone());
            ps.setString(4, provider.getEmail());
            ps.setInt(5, provider.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM providers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Provider> findAll() throws Exception {
        List<Provider> list = new ArrayList<>();
        String sql = "SELECT id, name, contact_name, phone, email FROM providers ORDER BY name ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public Provider findById(int id) throws Exception {
        String sql = "SELECT id, name, contact_name, phone, email FROM providers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Provider mapRow(ResultSet rs) throws Exception {
        return new Provider(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("contact_name"),
            rs.getString("phone"),
            rs.getString("email")
        );
    }
}
