package edu.eci.dosw.tdd.core.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String username;
    private String password;
    private Role role;
}