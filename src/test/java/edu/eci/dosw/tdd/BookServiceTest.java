package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    @Test
    void addBook_success() {
        BookService service = new BookService();

        Book book = Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert Martin")
                .available(true)
                .build();

        Book saved = service.addBook(book);

        assertNotNull(saved);
        assertEquals("B1", saved.getId());
        assertEquals(1, service.getAllBooks().size());
    }

    @Test
    void addBook_error_whenNullBook() {
        BookService service = new BookService();
        assertThrows(IllegalArgumentException.class, () -> service.addBook(null));
    }

    @Test
    void getBookById_success_whenExists() {
        BookService service = new BookService();
        service.addBook(Book.builder().id("B2").title("TDD").author("Kent Beck").available(true).build());

        Optional<Book> found = service.getBookById("B2");

        assertTrue(found.isPresent());
        assertEquals("B2", found.get().getId());
    }

    @Test
    void updateAvailability_success() {
        BookService service = new BookService();
        service.addBook(Book.builder().id("B3").title("DDD").author("Eric Evans").available(true).build());

        Book updated = service.updateAvailability("B3", false);

        assertFalse(updated.isAvailable());
        assertFalse(service.getBookById("B3").orElseThrow().isAvailable());
    }

    @Test
    void updateAvailability_error_whenBookNotFound() {
        BookService service = new BookService();
        assertThrows(IllegalArgumentException.class, () -> service.updateAvailability("NO_EXISTE", false));
    }
}