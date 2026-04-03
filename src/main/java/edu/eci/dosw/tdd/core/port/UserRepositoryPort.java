package edu.eci.dosw.tdd.core.port;

import edu.eci.dosw.tdd.core.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void deleteById(String id);
    boolean existsByUsername(String username);
}