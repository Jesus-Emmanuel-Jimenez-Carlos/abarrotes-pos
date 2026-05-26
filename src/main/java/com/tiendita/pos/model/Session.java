package com.tiendita.pos.model;

import java.time.LocalDateTime;

/**
 * Singleton que gestiona la sesión de usuario activa en memoria del sistema.
 */
public class Session {
    private static volatile Session instance;
    private User currentUser;
    private LocalDateTime loginTime;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            synchronized (Session.class) {
                if (instance == null) {
                    instance = new Session();
                }
            }
        }
        return instance;
    }

    public synchronized void startSession(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
    }

    public synchronized void closeSession() {
        this.currentUser = null;
        this.loginTime = null;
    }

    public synchronized boolean isActive() {
        return currentUser != null;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized LocalDateTime getLoginTime() {
        return loginTime;
    }

    public synchronized boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMINISTRADOR;
    }
}
