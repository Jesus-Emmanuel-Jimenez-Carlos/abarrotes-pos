package com.tiendita.pos.repository;

import com.tiendita.pos.model.User;
import java.util.List;

/**
 * Contrato de acceso a datos para la gestión de usuarios.
 */
public interface UserRepository {
    User findByUsername(String username) throws Exception;
    void save(User user) throws Exception;
    void update(User user) throws Exception;
    List<User> findAll() throws Exception;
}
