package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoginRequest;
import edu.eci.dosw.tdd.controller.dto.LoginResponse;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.getUserByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Credenciales inválidas");

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name(), user.getUsername()));
    }

    /**
     * Cualquier persona puede registrarse como USER.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDTO dto) {
        dto.setRole("USER");
        userService.registerUser(UserMapper.toModel(dto));
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    /**
     * Un LIBRARIAN puede crear su propio usuario (sin autenticación previa).
     * No puede crear otros usuarios; eso lo gestiona el ADMIN directamente en Mongo.
     */
    @PostMapping("/register-librarian")
    public ResponseEntity<String> registerLibrarian(@Valid @RequestBody UserDTO dto) {
        dto.setRole("LIBRARIAN");
        userService.registerUser(UserMapper.toModel(dto));
        return ResponseEntity.ok("Bibliotecario registrado exitosamente");
    }
}