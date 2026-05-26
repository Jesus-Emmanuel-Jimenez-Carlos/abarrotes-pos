package com.tiendita.pos.repository;

import com.tiendita.pos.model.Product;
import java.util.List;

/**
 * Contrato de acceso a datos para la gestión de productos del inventario.
 */
public interface ProductRepository {
    Product findByBarcode(String barcode) throws Exception;
    void save(Product product) throws Exception;
    void update(Product product) throws Exception;
    void delete(String barcode) throws Exception;
    List<Product> findAll() throws Exception;
    List<Product> findLowStock() throws Exception;
    List<Product> searchByDescription(String query) throws Exception;
}
