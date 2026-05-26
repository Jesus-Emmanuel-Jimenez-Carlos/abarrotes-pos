package com.tiendita.pos.repository;

import com.tiendita.pos.model.Sale;
import com.tiendita.pos.model.SaleDetail;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de acceso a datos para las transacciones del punto de venta (Ventas).
 */
public interface SaleRepository {
    void save(Sale sale, Connection conn) throws Exception;
    void saveDetail(SaleDetail detail, Connection conn) throws Exception;
    List<Sale> findAll() throws Exception;
    List<Sale> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception;
    List<SaleDetail> findDetailsBySaleId(int saleId) throws Exception;
}
