package com.tiendita.pos.service;

import com.tiendita.pos.model.Session;
import com.tiendita.pos.model.User;
import com.tiendita.pos.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * Servicio encargado de la seguridad, autenticación y gestión de usuarios.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Autentica un usuario en el sistema.
     * Si las credenciales son válidas, inicia la sesión en memoria.
     * 
     * @return true si el inicio de sesión fue exitoso, false en caso contrario.
     */
    public boolean login(String username, String password) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null || !user.isActive()) {
                return false;
            }

            // Verificar contraseña usando BCrypt
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                Session.getInstance().startSession(user);
                System.out.println("Sesión iniciada exitosamente para: " + username);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error en login del servicio: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registra un nuevo usuario en la base de datos con contraseña cifrada.
     */
    public void registerUser(User user, String plaintextPassword) throws Exception {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("El nombre de usuario '" + user.getUsername() + "' ya existe.");
        }
        if (plaintextPassword == null || plaintextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        
        // Cifrar contraseña con sal de BCrypt
        String hashed = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
        user.setPasswordHash(hashed);
        
        userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente. Si se provee una contraseña en texto plano, la cifra primero.
     */
    public void updateUser(User user, String newPlaintextPassword) throws Exception {
        if (newPlaintextPassword != null && !newPlaintextPassword.trim().isEmpty()) {
            String hashed = BCrypt.hashpw(newPlaintextPassword, BCrypt.gensalt());
            user.setPasswordHash(hashed);
        }
        userRepository.update(user);
    }

    /**
     * Cierra la sesión activa actual.
     */
    public void logout() {
        Session.getInstance().closeSession();
        System.out.println("Sesión cerrada correctamente.");
    }

    public List<User> getAllUsers() throws Exception {
        return userRepository.findAll();
    }
}
