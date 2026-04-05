package edu.eci.dosw.tdd.relacional;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.port.BookRepositoryPort;
import edu.eci.dosw.tdd.core.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepositoryPort bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id("1").title("Clean Code").author("Robert C. Martin")
                .totalCopies(5).availableCopies(5).build();
    }

    @Test
    void addBook_shouldSaveAndReturnBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book result = bookService.addBook(book);
        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBook_shouldThrowWhenTotalCopiesIsZero() {
        book.setTotalCopies(0);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_shouldThrowWhenAvailableCopiesIsNegative() {
        book.setAvailableCopies(-1);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getAllBooks_shouldReturnList() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        List<Book> result = bookService.getAllBooks();
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        Optional<Book> result = bookService.getBookById("1");
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void getBookById_shouldReturnEmpty_whenNotExists() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());
        assertTrue(bookService.getBookById("999").isEmpty());
    }

    @Test
    void decreaseAvailableCopies_shouldCallPort() {
        doNothing().when(bookRepository).updateAvailableCopies("1", -1);
        bookService.decreaseAvailableCopies("1");
        verify(bookRepository).updateAvailableCopies("1", -1);
    }

    @Test
    void decreaseAvailableCopies_shouldThrow_whenNoneAvailable() {
        doThrow(new IllegalStateException("No hay ejemplares disponibles"))
                .when(bookRepository).updateAvailableCopies("1", -1);
        assertThrows(IllegalStateException.class,
                () -> bookService.decreaseAvailableCopies("1"));
    }

    @Test
    void increaseAvailableCopies_shouldCallPort() {
        doNothing().when(bookRepository).updateAvailableCopies("1", 1);
        bookService.increaseAvailableCopies("1");
        verify(bookRepository).updateAvailableCopies("1", 1);
    }

    @Test
    void increaseAvailableCopies_shouldThrow_whenAlreadyAtMax() {
        doThrow(new IllegalStateException("Ejemplares disponibles ya están al máximo"))
                .when(bookRepository).updateAvailableCopies("1", 1);
        assertThrows(IllegalStateException.class,
                () -> bookService.increaseAvailableCopies("1"));
    }

    @Test
    void addBook_shouldSetDefaultValues_whenNullFields() {
        book.setAddedToCatalogDate(null);
        book.setAvailabilityStatus(null);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book result = bookService.addBook(book);
        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_shouldUpdateAndReturn() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book result = bookService.updateBook("1", book);
        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_shouldThrow_whenNotFound() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("999", book));
    }

    @Test
    void updateBook_shouldThrow_whenTotalCopiesZero() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        book.setTotalCopies(0);
        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("1", book));
    }

    @Test
    void deleteBook_shouldDelete_whenExists() {
        when(bookRepository.existsById("1")).thenReturn(true);
        doNothing().when(bookRepository).deleteById("1");
        bookService.deleteBook("1");
        verify(bookRepository).deleteById("1");
    }

    @Test
    void deleteBook_shouldThrow_whenNotFound() {
        when(bookRepository.existsById("999")).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook("999"));
    }

    @Test
    void getAvailableBooks_shouldReturnList() {
        when(bookRepository.findAvailable()).thenReturn(List.of(book));
        List<Book> result = bookService.getAvailableBooks();
        assertEquals(1, result.size());
    }

}