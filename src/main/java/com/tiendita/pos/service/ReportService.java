package com.tiendita.pos.service;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.Sale;
import com.tiendita.pos.model.SaleDetail;
import com.tiendita.pos.repository.SaleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la generación de reportes gerenciales, análisis de utilidades y valuación de inventario.
 */
public class ReportService {
    private final SaleRepository saleRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * Calcula la suma total de las ventas en un rango de fechas.
     */
    public double getTotalSalesAmount(LocalDateTime start, LocalDateTime end) throws Exception {
        String sql = "SELECT SUM(total) FROM sales WHERE sale_date >= ? AND sale_date <= ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start.format(formatter));
            ps.setString(2, end.format(formatter));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    /**
     * Calcula la Utilidad Neta (Ganancia Real) en un rango de fechas.
     * Utilidad = Sumatoria(cantidad * (precio_venta_historico - costo_compra_historico))
     */
    public double getNetUtility(LocalDateTime start, LocalDateTime end) throws Exception {
        String sql = "SELECT SUM(sd.quantity * (sd.sell_price - sd.buy_price)) " +
                     "FROM sale_details sd " +
                     "JOIN sales s ON sd.sale_id = s.id " +
                     "WHERE s.sale_date >= ? AND s.sale_date <= ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start.format(formatter));
            ps.setString(2, end.format(formatter));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    /**
     * Obtiene el listado de los 5 productos más vendidos.
     */
    public List<ProductSalesSummary> getTopSellingProducts() throws Exception {
        List<ProductSalesSummary> list = new ArrayList<>();
        String sql = "SELECT sd.product_barcode, sd.product_description, SUM(sd.quantity) AS total_qty, SUM(sd.quantity * sd.sell_price) AS total_revenue " +
                     "FROM sale_details sd " +
                     "GROUP BY sd.product_barcode, sd.product_description " +
                     "ORDER BY total_qty DESC " +
                     "LIMIT 5";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ProductSalesSummary(
                    rs.getString("product_barcode"),
                    rs.getString("product_description"),
                    rs.getDouble("total_qty"),
                    rs.getDouble("total_revenue")
                ));
            }
        }
        return list;
    }

    /**
     * Calcula el valor de inversión total en inventario (stock actual * precio compra).
     */
    public double getInventoryTotalValue() throws Exception {
        String sql = "SELECT SUM(stock * buy_price) FROM products";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    /**
     * Obtiene el número total de productos con nivel de stock bajo (stock <= min_stock).
     */
    public int getLowStockAlertCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM products WHERE stock <= min_stock";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Obtiene una lista de ventas recientes filtradas por fecha límite.
     */
    public List<Sale> topRecentSales(LocalDateTime start, LocalDateTime end, int limit) throws Exception {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT id, sale_date, total, payment_method, username " +
                     "FROM sales " +
                     "WHERE sale_date >= ? AND sale_date <= ? " +
                     "ORDER BY sale_date DESC " +
                     "LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start.format(formatter));
            ps.setString(2, end.format(formatter));
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Sale(
                        rs.getInt("id"),
                        LocalDateTime.parse(rs.getString("sale_date"), formatter),
                        rs.getDouble("total"),
                        com.tiendita.pos.model.PaymentMethod.valueOf(rs.getString("payment_method")),
                        rs.getString("username")
                    ));
                }
            }
        }
        return list;
    }

    public List<Sale> getSalesByRange(LocalDateTime start, LocalDateTime end) throws Exception {
        return saleRepository.findByDateRange(start, end);
    }

    public List<SaleDetail> getSaleDetails(int saleId) throws Exception {
        return saleRepository.findDetailsBySaleId(saleId);
    }

    /**
     * Clase auxiliar (DTO/VO) para encapsular las estadísticas de venta por producto.
     */
    public static class ProductSalesSummary {
        private final String barcode;
        private final String description;
        private final double totalQuantity;
        private final double totalRevenue;

        public ProductSalesSummary(String barcode, String description, double totalQuantity, double totalRevenue) {
            this.barcode = barcode;
            this.description = description;
            this.totalQuantity = totalQuantity;
            this.totalRevenue = totalRevenue;
        }

        public String getBarcode() {
            return barcode;
        }

        public String getDescription() {
            return description;
        }

        public double getTotalQuantity() {
            return totalQuantity;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }
    }
}
