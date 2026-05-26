package com.tiendita.pos.model;

/**
 * Representa el detalle individual de un producto vendido dentro de una venta.
 */
public class SaleDetail {
    private Integer id;
    private Sale sale;
    private String productBarcode;
    private String productDescription;
    private double quantity;
    private double buyPrice;  // Histórico: costo al momento de venta
    private double sellPrice; // Histórico: precio de venta real

    public SaleDetail() {}

    public SaleDetail(Integer id, String productBarcode, String productDescription, double quantity, double buyPrice, double sellPrice) {
        this.id = id;
        this.productBarcode = productBarcode;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public SaleDetail(String productBarcode, String productDescription, double quantity, double buyPrice, double sellPrice) {
        this.productBarcode = productBarcode;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
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

    /**
     * Calcula el subtotal de esta línea de detalle.
     */
    public double getSubtotal() {
        return quantity * sellPrice;
    }

    /**
     * Calcula la ganancia o utilidad neta de esta línea (Venta - Costo).
     */
    public double getNetUtility() {
        return quantity * (sellPrice - buyPrice);
    }
}
