package com.tiendita.pos.service;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.*;
import com.tiendita.pos.repository.ProductRepository;
import com.tiendita.pos.repository.SaleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

/**
 * Servicio encargado de procesar transacciones del Punto de Venta (POS).
 */
public class POSService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public POSService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    /**
     * Procesa una venta en el punto de venta de forma transaccional y atómica.
     * Resta el inventario de cada producto vendido.
     * Si no hay stock suficiente para algún producto, lanza una excepción y revierte toda la operación.
     * 
     * @param sale El objeto de venta con sus detalles precargados (cantidades y precios).
     * @throws Exception Si ocurre un fallo en base de datos o si hay inventario insuficiente.
     */
    public synchronized void processSale(Sale sale) throws Exception {
        if (sale.getDetails() == null || sale.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No se puede registrar una venta sin productos en el carrito.");
        }

        User activeUser = Session.getInstance().getCurrentUser();
        sale.setUsername((activeUser != null) ? activeUser.getUsername() : "Cajero");
        sale.setSaleDate(LocalDateTime.now());

        // Calcular el total consolidado por seguridad
        double totalCalculated = 0.0;
        for (SaleDetail detail : sale.getDetails()) {
            totalCalculated += detail.getSubtotal();
        }
        sale.setTotal(totalCalculated);

        // Sentencia para descontar stock en lote
        String updateStockSql = "UPDATE products SET stock = stock - ? WHERE barcode = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Desactivar auto-commit para iniciar transacción
            conn.setAutoCommit(false);
            
            try {
                // 1. Guardar cabecera de la venta para obtener el ID autogenerado
                saleRepository.save(sale, conn);

                // 2. Iterar por cada detalle de venta
                try (PreparedStatement psUpdateStock = conn.prepareStatement(updateStockSql)) {
                    for (SaleDetail detail : sale.getDetails()) {
                        
                        // Consultar producto actual de la BD para validar stock en tiempo real
                        Product product = productRepository.findByBarcode(detail.getProductBarcode());
                        if (product == null) {
                            throw new IllegalArgumentException("El producto con código " + detail.getProductBarcode() + " ya no existe en el catálogo.");
                        }

                        // Verificar existencias
                        if (product.getStock() < detail.getQuantity()) {
                            throw new IllegalArgumentException(String.format(
                                "Stock insuficiente para '%s'. Actual: %.2f %s, Solicitado: %.2f %s",
                                product.getDescription(),
                                product.getStock(), product.getUnit().name(),
                                detail.getQuantity(), product.getUnit().name()
                            ));
                        }

                        // Asignar precios históricos del producto al detalle por seguridad contable
                        detail.setBuyPrice(product.getBuyPrice());
                        detail.setSellPrice(product.getSellPrice());
                        detail.setProductDescription(product.getDescription());
                        detail.setSale(sale);

                        // A) Guardar detalle de venta en BD
                        saleRepository.saveDetail(detail, conn);

                        // B) Configurar y ejecutar la actualización del stock
                        psUpdateStock.setDouble(1, detail.getQuantity());
                        psUpdateStock.setString(2, detail.getProductBarcode());
                        psUpdateStock.executeUpdate();
                    }
                }

                // 3. Confirmar transacción
                conn.commit();
                System.out.println("Venta #" + sale.getId() + " registrada de manera exitosa y atómica.");

            } catch (Exception e) {
                // Revertir toda la transacción ante cualquier error
                conn.rollback();
                System.err.println("Error procesando venta. Transacción revertida (Rollback). Razón: " + e.getMessage());
                throw e;
            }
        }
    }
}
