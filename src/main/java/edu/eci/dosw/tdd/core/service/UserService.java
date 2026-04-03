package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.MembershipType;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.port.UserRepositoryPort;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

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
        if (user.getMembershipType() == null) user.setMembershipType(MembershipType.STANDARD);
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDate.now());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsByUsername(
                userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id))
                        .getUsername()))
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        userRepository.deleteById(id);
    }
}