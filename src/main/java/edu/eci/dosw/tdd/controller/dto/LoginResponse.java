package edu.eci.dosw.tdd.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private String username;
}