package com.tiendita.pos.service;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.*;
import com.tiendita.pos.repository.MermaRepository;
import com.tiendita.pos.repository.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio encargado de la lógica de negocio del inventario y las mermas.
 */
public class ProductService {
    private final ProductRepository productRepository;
    private final MermaRepository mermaRepository;

    public ProductService(ProductRepository productRepository, MermaRepository mermaRepository) {
        this.productRepository = productRepository;
        this.mermaRepository = mermaRepository;
    }

    public void addProduct(Product product) throws Exception {
        // Validaciones de negocio
        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de barras es requerido.");
        }
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es requerida.");
        }
        if (product.getBuyPrice() < 0) {
            throw new IllegalArgumentException("El precio de compra no puede ser negativo.");
        }
        if (product.getSellPrice() < product.getBuyPrice()) {
            throw new IllegalArgumentException("El precio de venta no debe ser menor al de compra.");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
        }

        // Verificar existencia previa
        if (productRepository.findByBarcode(product.getBarcode()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con el código de barras: " + product.getBarcode());
        }

        productRepository.save(product);
    }

    public void updateProduct(Product product) throws Exception {
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es requerida.");
        }
        if (product.getBuyPrice() < 0) {
            throw new IllegalArgumentException("El precio de compra no puede ser negativo.");
        }
        if (product.getSellPrice() < product.getBuyPrice()) {
            throw new IllegalArgumentException("El precio de venta no debe ser menor al de compra (margen negativo).");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        productRepository.update(product);
    }

    public void deleteProduct(String barcode) throws Exception {
        productRepository.delete(barcode);
    }

    public Product getProductByBarcode(String barcode) throws Exception {
        return productRepository.findByBarcode(barcode);
    }

    public List<Product> getAllProducts() throws Exception {
        return productRepository.findAll();
    }

    public List<Product> getLowStockProducts() throws Exception {
        return productRepository.findLowStock();
    }

    public List<Product> searchProducts(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchByDescription(query);
    }

    /**
     * Registra una merma de forma transaccional: descuenta el stock del producto
     * e inserta el registro de merma en un único lote atómico.
     */
    public synchronized void registerMerma(String barcode, double quantity, MermaReason reason) throws Exception {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad de merma debe ser mayor a cero.");
        }

        Product product = productRepository.findByBarcode(barcode);
        if (product == null) {
            throw new IllegalArgumentException("No existe el producto con código: " + barcode);
        }

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException(String.format(
                "Stock insuficiente para reportar merma. Stock actual: %.2f, Solicitado: %.2f",
                product.getStock(), quantity
            ));
        }

        double newStock = product.getStock() - quantity;
        User activeUser = Session.getInstance().getCurrentUser();
        String username = (activeUser != null) ? activeUser.getUsername() : "Sistema";

        String updateStockSql = "UPDATE products SET stock = ? WHERE barcode = ?";
        String insertMermaSql = "INSERT INTO mermas (product_barcode, quantity, reason, registered_at, username) VALUES (?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Ejecutar transaccionalmente sobre una única conexión compartida
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUpdate = conn.prepareStatement(updateStockSql);
                 PreparedStatement psInsert = conn.prepareStatement(insertMermaSql)) {
                
                // 1. Actualizar Stock
                psUpdate.setDouble(1, newStock);
                psUpdate.setString(2, barcode);
                psUpdate.executeUpdate();

                // 2. Insertar Registro de Merma
                psInsert.setString(1, barcode);
                psInsert.setDouble(2, quantity);
                psInsert.setString(3, reason.name());
                psInsert.setString(4, LocalDateTime.now().format(formatter));
                psInsert.setString(5, username);
                psInsert.executeUpdate();

                // Confirmar transacción
                conn.commit();
                System.out.println("Merma registrada transaccionalmente. Producto: " + product.getDescription());
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Merma> getAllMermas() throws Exception {
        return mermaRepository.findAll();
    }
}
