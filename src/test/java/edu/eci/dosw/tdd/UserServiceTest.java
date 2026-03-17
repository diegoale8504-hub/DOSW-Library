package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerUserSuccess() {
        UserService service = new UserService();

        User user = User.builder()
                .id("U1")
                .name("Diego")
                .build();

        User saved = service.registerUser(user);

        assertNotNull(saved);
        assertEquals("U1", saved.getId());
        assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void registerUser_error_whenNullUser() {
        UserService service = new UserService();
        assertThrows(IllegalArgumentException.class, () -> service.registerUser(null));
    }

    @Test
    void getUserById_success_whenExists() {
        UserService service = new UserService();
        service.registerUser(User.builder().id("U2").name("Camilo").build());

        Optional<User> found = service.getUserById("U2");

        assertTrue(found.isPresent());
        assertEquals("Camilo", found.get().getName());
    }

    @Test
    void getUserById_error_whenBlankId() {
        UserService service = new UserService();
        assertThrows(IllegalArgumentException.class, () -> service.getUserById(" "));
    }
}