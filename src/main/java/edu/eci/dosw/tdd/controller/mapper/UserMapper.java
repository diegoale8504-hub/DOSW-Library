package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toModel(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(dto.getRole() != null ? Role.valueOf(dto.getRole()) : Role.USER)
                .build();
    }

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }
}