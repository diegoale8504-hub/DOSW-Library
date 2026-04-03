package edu.eci.dosw.tdd.relacional;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.persistence.relacional.entity.RoleEntity;
import edu.eci.dosw.tdd.persistence.relacional.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relacional.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private User user;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id("1")
                .name("Diego")
                .username("diego123")
                .password("hashed_password")
                .role(RoleEntity.USER)
                .build();

        user = User.builder()
                .id("1")
                .name("Diego")
                .username("diego123")
                .password("pass123")
                .role(Role.USER)
                .build();
    }

    @Test
    void registerUser_shouldSaveAndReturnUser() {
        when(userRepository.existsByUsername("diego123")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed_password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("Diego", result.getName());
        assertEquals("diego123", result.getUsername());
        verify(userRepository).save(any(UserEntity.class));
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
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Diego", result.get(0).getName());
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(userEntity));

        Optional<User> result = userService.getUserById("1");

        assertTrue(result.isPresent());
        assertEquals("diego123", result.get().getUsername());
    }

    @Test
    void getUserById_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById("999");

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserByUsername_shouldReturnUser_whenExists() {
        when(userRepository.findByUsername("diego123"))
                .thenReturn(Optional.of(userEntity));

        Optional<User> result = userService.getUserByUsername("diego123");

        assertTrue(result.isPresent());
        assertEquals("diego123", result.get().getUsername());
    }
}