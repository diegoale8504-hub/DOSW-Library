package edu.eci.dosw.tdd.relacional;

import edu.eci.dosw.tdd.core.model.MembershipType;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.port.UserRepositoryPort;
import edu.eci.dosw.tdd.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("1").name("Diego").username("diego123")
                .password("pass123").role(Role.USER)
                .membershipType(MembershipType.STANDARD)
                .createdAt(LocalDate.now())
                .build();
    }

    // ===== registerUser =====

    @Test
    void registerUser_shouldSaveAndReturnUser() {
        when(userRepository.existsByUsername("diego123")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("Diego", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldSetDefaultRole_whenRoleIsNull() {
        user.setRole(null);
        when(userRepository.existsByUsername("diego123")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldSetDefaultMembership_whenMembershipIsNull() {
        user.setMembershipType(null);
        when(userRepository.existsByUsername("diego123")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
    }

    @Test
    void registerUser_shouldSetCreatedAt_whenCreatedAtIsNull() {
        user.setCreatedAt(null);
        when(userRepository.existsByUsername("diego123")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
    }

    @Test
    void registerUser_shouldThrow_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("diego123")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_shouldThrow_whenNameIsBlank() {
        user.setName("");
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUser_shouldThrow_whenUsernameIsBlank() {
        user.setUsername("");
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUser_shouldThrow_whenPasswordIsBlank() {
        user.setPassword("");
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));
    }

    // ===== getAllUsers =====

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("Diego", result.get(0).getName());
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<User> result = userService.getAllUsers();
        assertTrue(result.isEmpty());
    }

    // ===== getUserById =====

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById("1");
        assertTrue(result.isPresent());
        assertEquals("diego123", result.get().getUsername());
    }

    @Test
    void getUserById_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());
        assertTrue(userService.getUserById("999").isEmpty());
    }

    // ===== getUserByUsername =====

    @Test
    void getUserByUsername_shouldReturnUser_whenExists() {
        when(userRepository.findByUsername("diego123")).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserByUsername("diego123");
        assertTrue(result.isPresent());
        assertEquals("diego123", result.get().getUsername());
    }

    @Test
    void getUserByUsername_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());
        assertTrue(userService.getUserByUsername("noexiste").isEmpty());
    }

    // ===== deleteUser =====

    @Test
    void deleteUser_shouldDelete_whenExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("diego123")).thenReturn(true);
        doNothing().when(userRepository).deleteById("1");

        userService.deleteUser("1");

        verify(userRepository).deleteById("1");
    }

    @Test
    void deleteUser_shouldThrow_whenUserIdNotFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser("999"));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_shouldThrow_whenUsernameNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("diego123")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser("1"));
        verify(userRepository, never()).deleteById(any());
    }
}