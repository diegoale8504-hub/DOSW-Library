package edu.eci.dosw.tdd.persistence.relacional.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relacional.entity.BookEntity;

public final class BookPersistenceMapper {

    private BookPersistenceMapper() {}

    public static Book toDomain(BookEntity e) {
        if (e == null) return null;
        return Book.builder()
                .id(e.getId())
                .title(e.getTitle())
                .author(e.getAuthor())
                .totalCopies(e.getTotalCopies())
                .availableCopies(e.getAvailableCopies())
                .build();
    }

    public static BookEntity toEntity(Book b) {
        if (b == null) return null;
        return BookEntity.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .build();
    }
}