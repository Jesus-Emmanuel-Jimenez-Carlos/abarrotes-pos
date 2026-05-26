package com.tiendita.pos.repository.impl;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.PaymentMethod;
import com.tiendita.pos.model.Sale;
import com.tiendita.pos.model.SaleDetail;
import com.tiendita.pos.repository.SaleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite/JDBC para las transacciones de ventas y detalles históricos.
 */
public class SQLiteSaleRepository implements SaleRepository {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void save(Sale sale, Connection conn) throws Exception {
        String sql = "INSERT INTO sales (sale_date, total, payment_method, username) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sale.getSaleDate().format(formatter));
            ps.setDouble(2, sale.getTotal());
            ps.setString(3, sale.getPaymentMethod().name());
            ps.setString(4, sale.getUsername());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sale.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void saveDetail(SaleDetail detail, Connection conn) throws Exception {
        String sql = "INSERT INTO sale_details (sale_id, product_barcode, product_description, quantity, buy_price, sell_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getSale().getId());
            ps.setString(2, detail.getProductBarcode());
            ps.setString(3, detail.getProductDescription());
            ps.setDouble(4, detail.getQuantity());
            ps.setDouble(5, detail.getBuyPrice());
            ps.setDouble(6, detail.getSellPrice());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Sale> findAll() throws Exception {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT id, sale_date, total, payment_method, username FROM sales ORDER BY sale_date DESC";
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
    public List<Sale> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT id, sale_date, total, payment_method, username " +
                     "FROM sales " +
                     "WHERE sale_date >= ? AND sale_date <= ? " +
                     "ORDER BY sale_date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start.format(formatter));
            ps.setString(2, end.format(formatter));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<SaleDetail> findDetailsBySaleId(int saleId) throws Exception {
        List<SaleDetail> list = new ArrayList<>();
        String sql = "SELECT id, sale_id, product_barcode, product_description, quantity, buy_price, sell_price " +
                     "FROM sale_details " +
                     "WHERE sale_id = ? " +
                     "ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDetailRow(rs));
                }
            }
        }
        return list;
    }

    private Sale mapRow(ResultSet rs) throws Exception {
        return new Sale(
            rs.getInt("id"),
            LocalDateTime.parse(rs.getString("sale_date"), formatter),
            rs.getDouble("total"),
            PaymentMethod.valueOf(rs.getString("payment_method")),
            rs.getString("username")
        );
    }

    private SaleDetail mapDetailRow(ResultSet rs) throws Exception {
        return new SaleDetail(
            rs.getInt("id"),
            rs.getString("product_barcode"),
            rs.getString("product_description"),
            rs.getDouble("quantity"),
            rs.getDouble("buy_price"),
            rs.getDouble("sell_price")
        );
    }
}
