package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.util.ValidationUtil;

public final class BookValidator {

    private BookValidator() {}

    public static void validateForCreate(BookDTO dto) {
        ValidationUtil.requireNonNull(dto, "bookDTO");
        ValidationUtil.requireNonBlank(dto.getId(), "book.id");
        ValidationUtil.requireNonBlank(dto.getTitle(), "book.title");
        ValidationUtil.requireNonBlank(dto.getAuthor(), "book.author");
    }

    public static void validateAvailabilityUpdate(Boolean available) {
        if (available == null) {
            throw new IllegalArgumentException("available cannot be null");
        }
    }
}