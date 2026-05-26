package com.tiendita.pos.service;

import com.tiendita.pos.model.Provider;
import com.tiendita.pos.repository.ProviderRepository;

import java.util.List;

/**
 * Servicio encargado de gestionar los proveedores de la tienda.
 */
public class ProviderService {
    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public void addProvider(Provider provider) throws Exception {
        if (provider.getName() == null || provider.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es requerido.");
        }
        providerRepository.save(provider);
    }

    public void updateProvider(Provider provider) throws Exception {
        if (provider.getName() == null || provider.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es requerido.");
        }
        providerRepository.update(provider);
    }

    public void deleteProvider(int id) throws Exception {
        providerRepository.delete(id);
    }

    public List<Provider> getAllProviders() throws Exception {
        return providerRepository.findAll();
    }

    public Provider getById(int id) throws Exception {
        return providerRepository.findById(id);
    }
}
