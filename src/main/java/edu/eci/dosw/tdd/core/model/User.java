package edu.eci.dosw.tdd.core.model;

import lombok.*;
import java.time.LocalDate;

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

    private String email;
    private MembershipType membershipType;
    private LocalDate createdAt;
}