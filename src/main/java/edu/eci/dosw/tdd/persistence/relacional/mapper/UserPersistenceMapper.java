package edu.eci.dosw.tdd.persistence.relacional.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.persistence.relacional.entity.RoleEntity;
import edu.eci.dosw.tdd.persistence.relacional.entity.UserEntity;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {}

    public static User toDomain(UserEntity e) {
        if (e == null) return null;
        return User.builder()
                .id(e.getId())
                .name(e.getName())
                .username(e.getUsername())
                .password(e.getPassword())
                .role(Role.valueOf(e.getRole().name()))
                .build();
    }

    public static UserEntity toEntity(User u) {
        if (u == null) return null;
        return UserEntity.builder()
                .id(u.getId())
                .name(u.getName())
                .username(u.getUsername())
                .password(u.getPassword())
                .role(RoleEntity.valueOf(u.getRole().name()))
                .build();
    }
}