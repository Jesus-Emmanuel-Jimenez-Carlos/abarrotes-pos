package com.tiendita.pos.model;

/**
 * Define las unidades de medida comerciales para los productos de la tienda.
 */
public enum UnitType {
    PZA("Pieza"),
    KG("Kilogramo"),
    L("Litro"),
    CJA("Caja");

    private final String label;

    UnitType(String label) {
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
