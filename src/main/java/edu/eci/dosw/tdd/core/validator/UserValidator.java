package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.util.ValidationUtil;

public final class UserValidator {

    private UserValidator() {}

    public static void validateForCreate(UserDTO dto) {
        ValidationUtil.requireNonNull(dto, "userDTO");
        ValidationUtil.requireNonBlank(dto.getId(), "user.id");
        ValidationUtil.requireNonBlank(dto.getName(), "user.name");
    }
}