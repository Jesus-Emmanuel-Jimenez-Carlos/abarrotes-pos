package com.tiendita.pos.model;

import java.time.LocalDateTime;

/**
 * Representa un registro de merma o pérdida de inventario.
 */
public class Merma {
    private Integer id;
    private String productBarcode;
    private String productDescription; // Para facilitar visualización
    private double quantity;
    private MermaReason reason;
    private LocalDateTime registeredAt;
    private String username;

    public Merma() {}

    public Merma(Integer id, String productBarcode, double quantity, MermaReason reason, LocalDateTime registeredAt, String username) {
        this.id = id;
        this.productBarcode = productBarcode;
        this.quantity = quantity;
        this.reason = reason;
        this.registeredAt = registeredAt;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public MermaReason getReason() {
        return reason;
    }

    public void setReason(MermaReason reason) {
        this.reason = reason;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
