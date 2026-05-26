package com.tiendita.pos.repository;

import com.tiendita.pos.model.Merma;
import java.util.List;

/**
 * Contrato de acceso a datos para registrar y consultar mermas de inventario.
 */
public interface MermaRepository {
    void save(Merma merma) throws Exception;
    List<Merma> findAll() throws Exception;
    List<Merma> findByProductBarcode(String barcode) throws Exception;
}
