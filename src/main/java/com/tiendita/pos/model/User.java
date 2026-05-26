package com.tiendita.pos.model;

/**
 * Representa un usuario en el sistema.
 */
public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private Role role;
    private boolean active;

    public User() {}

    public User(Integer id, String username, String passwordHash, Role role, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public User(String username, String passwordHash, Role role, boolean active) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return username + " (" + role.getDisplayName() + ")";
    }
}
