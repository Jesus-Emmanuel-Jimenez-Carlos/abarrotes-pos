package com.tiendita.pos.service;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.model.Role;
import com.tiendita.pos.model.Session;
import com.tiendita.pos.model.User;
import com.tiendita.pos.repository.UserRepository;
import com.tiendita.pos.repository.impl.SQLiteUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private static UserService userService;

    @BeforeAll
    public static void setUp() {
        // Inicializar BD local de prueba
        DatabaseConfig.initializeDatabase();
        UserRepository userRepo = new SQLiteUserRepository();
        userService = new UserService(userRepo);
    }

    @Test
    public void testAuthenticationSuccessAndFailure() {
        try {
            // Registrar un nuevo usuario de pruebas
            String username = "testcajero_" + System.currentTimeMillis();
            String plainPassword = "superSecurePassword999";
            User user = new User(username, "", Role.CAJERO, true);

            userService.registerUser(user, plainPassword);

            // Validar login fallido (usuario o contraseña errónea)
            assertFalse(userService.login(username, "incorrectPassword"));
            assertFalse(userService.login("nonexistent_user", plainPassword));

            // Validar login exitoso
            assertTrue(userService.login(username, plainPassword));

            // Validar sesión activa en memoria
            assertTrue(Session.getInstance().isActive());
            assertEquals(username, Session.getInstance().getCurrentUser().getUsername());
            assertEquals(Role.CAJERO, Session.getInstance().getCurrentUser().getRole());

            // Cerrar sesión y verificar
            userService.logout();
            assertFalse(Session.getInstance().isActive());

        } catch (Exception e) {
            fail("El test falló debido a una excepción: " + e.getMessage());
        }
    }
}
