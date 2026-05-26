package com.tiendita.pos.repository;

import com.tiendita.pos.model.Provider;
import java.util.List;

/**
 * Contrato de acceso a datos para la administración de proveedores.
 */
public interface ProviderRepository {
    void save(Provider provider) throws Exception;
    void update(Provider provider) throws Exception;
    void delete(int id) throws Exception;
    List<Provider> findAll() throws Exception;
    Provider findById(int id) throws Exception;
}
