package com.tiendita.pos.repository.impl;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.Product;
import com.tiendita.pos.model.UnitType;
import com.tiendita.pos.repository.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite/JDBC para la gestión de productos en inventario.
 */
public class SQLiteProductRepository implements ProductRepository {

    @Override
    public Product findByBarcode(String barcode) throws Exception {
        String sql = "SELECT barcode, description, buy_price, sell_price, stock, min_stock, unit FROM products WHERE barcode = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void save(Product product) throws Exception {
        String sql = "INSERT INTO products (barcode, description, buy_price, sell_price, stock, min_stock, unit) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getBarcode());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getBuyPrice());
            ps.setDouble(4, product.getSellPrice());
            ps.setDouble(5, product.getStock());
            ps.setDouble(6, product.getMinStock());
            ps.setString(7, product.getUnit().name());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Product product) throws Exception {
        String sql = "UPDATE products SET description = ?, buy_price = ?, sell_price = ?, stock = ?, min_stock = ?, unit = ? WHERE barcode = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getDescription());
            ps.setDouble(2, product.getBuyPrice());
            ps.setDouble(3, product.getSellPrice());
            ps.setDouble(4, product.getStock());
            ps.setDouble(5, product.getMinStock());
            ps.setString(6, product.getUnit().name());
            ps.setString(7, product.getBarcode());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String barcode) throws Exception {
        String sql = "DELETE FROM products WHERE barcode = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Product> findAll() throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT barcode, description, buy_price, sell_price, stock, min_stock, unit FROM products ORDER BY description ASC";
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
    public List<Product> findLowStock() throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT barcode, description, buy_price, sell_price, stock, min_stock, unit FROM products WHERE stock <= min_stock ORDER BY description ASC";
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
    public List<Product> searchByDescription(String query) throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT barcode, description, buy_price, sell_price, stock, min_stock, unit FROM products WHERE description LIKE ? OR barcode LIKE ? ORDER BY description ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Product mapRow(ResultSet rs) throws Exception {
        return new Product(
            rs.getString("barcode"),
            rs.getString("description"),
            rs.getDouble("buy_price"),
            rs.getDouble("sell_price"),
            rs.getDouble("stock"),
            rs.getDouble("min_stock"),
            UnitType.valueOf(rs.getString("unit"))
        );
    }
}
