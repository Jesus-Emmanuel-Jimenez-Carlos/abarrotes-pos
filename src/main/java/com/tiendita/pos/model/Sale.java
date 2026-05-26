package com.tiendita.pos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una venta consolidada en el sistema.
 */
public class Sale {
    private Integer id;
    private LocalDateTime saleDate;
    private double total;
    private PaymentMethod paymentMethod;
    private String username;
    private List<SaleDetail> details = new ArrayList<>();

    public Sale() {}

    public Sale(Integer id, LocalDateTime saleDate, double total, PaymentMethod paymentMethod, String username) {
        this.id = id;
        this.saleDate = saleDate;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }

    public void addDetail(SaleDetail detail) {
        this.details.add(detail);
        detail.setSale(this);
    }
}
