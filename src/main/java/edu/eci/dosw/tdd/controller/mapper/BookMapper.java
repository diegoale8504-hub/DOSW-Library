package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;

public final class BookMapper {

    private BookMapper() {}

    public static Book toModel(BookDTO dto) {
        if (dto == null) return null;
        return Book.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .available(dto.isAvailable())
                .build();
    }

    public static BookDTO toDTO(Book book) {
        if (book == null) return null;
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .available(book.isAvailable())
                .build();
    }
}