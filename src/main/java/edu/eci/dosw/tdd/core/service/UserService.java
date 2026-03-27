package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import edu.eci.dosw.tdd.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(User user) {
        ValidationUtil.validateNotNull(user, "User");
        ValidationUtil.validateNotEmpty(user.getName(), "Nombre");
        ValidationUtil.validateNotEmpty(user.getUsername(), "Username");
        ValidationUtil.validateNotEmpty(user.getPassword(), "Password");

        if (userRepository.existsByUsername(user.getUsername()))
            throw new IllegalArgumentException("El username ya está en uso");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.USER);

        return UserPersistenceMapper.toDomain(
                userRepository.save(UserPersistenceMapper.toEntity(user)));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(UserPersistenceMapper::toDomain);
    }

    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id))
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        userRepository.deleteById(id);
    }
}