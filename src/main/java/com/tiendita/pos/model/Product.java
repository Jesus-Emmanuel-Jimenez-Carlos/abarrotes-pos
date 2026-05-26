package com.tiendita.pos.model;

/**
 * Representa un producto en el inventario.
 */
public class Product {
    private String barcode;
    private String description;
    private double buyPrice;
    private double sellPrice;
    private double stock;
    private double minStock;
    private UnitType unit;

    public Product() {}

    public Product(String barcode, String description, double buyPrice, double sellPrice, double stock, double minStock, UnitType unit) {
        this.barcode = barcode;
        this.description = description;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.minStock = minStock;
        this.unit = unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getMinStock() {
        return minStock;
    }

    public void setMinStock(double minStock) {
        this.minStock = minStock;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    /**
     * Evalúa si el producto requiere reabastecimiento (el stock actual es menor o igual al mínimo).
     */
    public boolean isLowStock() {
        return stock <= minStock;
    }

    @Override
    public String toString() {
        return description + " [" + barcode + "]";
    }
}
