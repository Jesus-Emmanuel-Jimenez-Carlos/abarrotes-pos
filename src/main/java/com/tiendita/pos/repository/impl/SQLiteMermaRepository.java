package com.tiendita.pos.repository.impl;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.Merma;
import com.tiendita.pos.model.MermaReason;
import com.tiendita.pos.repository.MermaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite/JDBC para el control de pérdidas y mermas.
 */
public class SQLiteMermaRepository implements MermaRepository {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void save(Merma merma) throws Exception {
        String sql = "INSERT INTO mermas (product_barcode, quantity, reason, registered_at, username) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merma.getProductBarcode());
            ps.setDouble(2, merma.getQuantity());
            ps.setString(3, merma.getReason().name());
            ps.setString(4, merma.getRegisteredAt().format(formatter));
            ps.setString(5, merma.getUsername());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    merma.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Merma> findAll() throws Exception {
        List<Merma> list = new ArrayList<>();
        String sql = "SELECT m.id, m.product_barcode, p.description, m.quantity, m.reason, m.registered_at, m.username " +
                     "FROM mermas m " +
                     "JOIN products p ON m.product_barcode = p.barcode " +
                     "ORDER BY m.registered_at DESC";
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
    public List<Merma> findByProductBarcode(String barcode) throws Exception {
        List<Merma> list = new ArrayList<>();
        String sql = "SELECT m.id, m.product_barcode, p.description, m.quantity, m.reason, m.registered_at, m.username " +
                     "FROM mermas m " +
                     "JOIN products p ON m.product_barcode = p.barcode " +
                     "WHERE m.product_barcode = ? " +
                     "ORDER BY m.registered_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Merma mapRow(ResultSet rs) throws Exception {
        Merma m = new Merma(
            rs.getInt("id"),
            rs.getString("product_barcode"),
            rs.getDouble("quantity"),
            MermaReason.valueOf(rs.getString("reason")),
            LocalDateTime.parse(rs.getString("registered_at"), formatter),
            rs.getString("username")
        );
        m.setProductDescription(rs.getString("description"));
        return m;
    }
}
