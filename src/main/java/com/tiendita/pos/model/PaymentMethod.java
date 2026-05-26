package com.tiendita.pos.model;

/**
 * Métodos de pago aceptados en el punto de venta.
 */
public enum PaymentMethod {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta de Débito/Crédito");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
