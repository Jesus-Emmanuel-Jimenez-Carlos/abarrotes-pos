package com.tiendita.pos.service;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.*;
import com.tiendita.pos.repository.ProductRepository;
import com.tiendita.pos.repository.SaleRepository;
import com.tiendita.pos.repository.impl.SQLiteProductRepository;
import com.tiendita.pos.repository.impl.SQLiteSaleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class POSServiceTest {
    private static ProductService productService;
    private static POSService posService;
    private static ProductRepository productRepo;

    @BeforeAll
    public static void setUp() {
        DatabaseConfig.initializeDatabase();
        productRepo = new SQLiteProductRepository();
        SaleRepository saleRepo = new SQLiteSaleRepository();
        
        productService = new ProductService(productRepo, new com.tiendita.pos.repository.impl.SQLiteMermaRepository());
        posService = new POSService(saleRepo, productRepo);
    }

    @Test
    public void testPOSCheckoutAndStockDeduction() {
        try {
            // 1. Crear producto con stock inicial conocido
            String barcode = "testcode_" + System.currentTimeMillis();
            Product p = new Product(barcode, "Refresco Fresa 600ml", 5.0, 15.0, 10.0, 2.0, UnitType.PZA);
            productService.addProduct(p);

            // 2. Crear una venta de 3 unidades
            Sale sale = new Sale();
            sale.setPaymentMethod(PaymentMethod.EFECTIVO);
            
            SaleDetail detail = new SaleDetail(barcode, "Refresco Fresa 600ml", 3.0, 5.0, 15.0);
            sale.addDetail(detail);

            // 3. Procesar venta
            posService.processSale(sale);

            // 4. Verificar que el stock se redujo a 7.0 (10.0 - 3.0)
            Product updatedProduct = productService.getProductByBarcode(barcode);
            assertEquals(7.0, updatedProduct.getStock(), "El stock debió reducirse a 7.");

            // 5. Intentar una venta que exceda el stock (venta de 8 unidades cuando solo quedan 7)
            Sale excessiveSale = new Sale();
            excessiveSale.setPaymentMethod(PaymentMethod.TARJETA);
            
            SaleDetail excessiveDetail = new SaleDetail(barcode, "Refresco Fresa 600ml", 8.0, 5.0, 15.0);
            excessiveSale.addDetail(excessiveDetail);

            // Debe lanzar excepción e impedir cobro
            assertThrows(IllegalArgumentException.class, () -> {
                posService.processSale(excessiveSale);
            });

            // Verificar que tras el rollback el stock sigue siendo 7.0 (y no cambió)
            Product postRollbackProduct = productService.getProductByBarcode(barcode);
            assertEquals(7.0, postRollbackProduct.getStock(), "El stock debe mantenerse en 7 tras un rollback transaccional.");

        } catch (Exception e) {
            fail("Excepción en pruebas del POS: " + e.getMessage());
        }
    }
}
