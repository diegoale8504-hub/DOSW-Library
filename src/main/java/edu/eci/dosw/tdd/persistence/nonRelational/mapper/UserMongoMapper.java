package edu.eci.dosw.tdd.persistence.nonRelational.mapper;

import edu.eci.dosw.tdd.core.model.MembershipType;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonRelational.document.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserMongoMapper {

    public UserDocument toDocument(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .membershipType(user.getMembershipType() != null
                        ? user.getMembershipType().name() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toDomain(UserDocument doc) {
        return User.builder()
                .id(doc.getId())
                .username(doc.getUsername())
                .password(doc.getPassword())
                .email(doc.getEmail())
                .name(doc.getName())
                .role(Role.valueOf(doc.getRole()))
                .membershipType(doc.getMembershipType() != null
                        ? MembershipType.valueOf(doc.getMembershipType()) : null)
                .createdAt(doc.getCreatedAt())
                .build();
    }
}