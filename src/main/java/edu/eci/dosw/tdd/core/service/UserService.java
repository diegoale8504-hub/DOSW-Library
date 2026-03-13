package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public User registerUser(User user) {
        ValidationUtil.validateNotNull(user, "User");
        ValidationUtil.validateNotEmpty(user.getId(), "ID del usuario");
        ValidationUtil.validateNotEmpty(user.getName(), "Nombre del usuario");
        users.add(user);
        return user;
    }

    /**
     * Retorna todos los usuarios registrados.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Busca un usuario por su ID.
     */
    public Optional<User> getUserById(String id) {
        ValidationUtil.validateNotEmpty(id, "ID del usuario");
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }
}