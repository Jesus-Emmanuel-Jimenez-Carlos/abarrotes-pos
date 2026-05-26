package com.tiendita.pos.model;

/**
 * Define los roles de seguridad y acceso para el sistema POS.
 */
public enum Role {
    ADMINISTRADOR("Administrador/Dueño"),
    CAJERO("Cajero/Empleado");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
