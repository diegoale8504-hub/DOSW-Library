package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String role; // "USER" o "LIBRARIAN"
}