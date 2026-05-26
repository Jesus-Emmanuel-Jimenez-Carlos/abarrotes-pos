package com.tiendita.pos.model;

/**
 * Razones estándar por las cuales un producto es reportado como merma.
 */
public enum MermaReason {
    CADUCIDAD("Caducidad de Producto"),
    DANIO("Producto Dañado / Roto"),
    ROBO("Robo Hormiga"),
    OTRO("Otro Motivo");

    private final String label;

    MermaReason(String label) {
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
