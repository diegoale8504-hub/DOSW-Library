package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
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
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private BookEntity bookEntity;
    private Book book;

    @BeforeEach
    void setUp() {
        bookEntity = BookEntity.builder()
                .id("1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalCopies(5)
                .availableCopies(5)
                .build();

        book = Book.builder()
                .id("1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalCopies(5)
                .availableCopies(5)
                .build();
    }

    @Test
    void addBook_shouldSaveAndReturnBook() {
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        Book result = bookService.addBook(book);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals(5, result.getTotalCopies());
        verify(bookRepository, times(1)).save(any(BookEntity.class));
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
        when(bookRepository.findAll()).thenReturn(List.of(bookEntity));

        List<Book> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(bookEntity));

        Optional<Book> result = bookService.getBookById("1");

        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void getBookById_shouldReturnEmpty_whenNotExists() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById("999");

        assertTrue(result.isEmpty());
    }

    @Test
    void decreaseAvailableCopies_shouldDecrease_whenAvailable() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(any())).thenReturn(bookEntity);

        bookService.decreaseAvailableCopies("1");

        verify(bookRepository).save(any(BookEntity.class));
    }

    @Test
    void decreaseAvailableCopies_shouldThrow_whenNoneAvailable() {
        bookEntity.setAvailableCopies(0);
        when(bookRepository.findById("1")).thenReturn(Optional.of(bookEntity));

        assertThrows(IllegalStateException.class,
                () -> bookService.decreaseAvailableCopies("1"));
    }

    @Test
    void increaseAvailableCopies_shouldIncrease_whenBelowMax() {
        bookEntity.setAvailableCopies(3);
        when(bookRepository.findById("1")).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(any())).thenReturn(bookEntity);

        bookService.increaseAvailableCopies("1");

        verify(bookRepository).save(any(BookEntity.class));
    }

    @Test
    void increaseAvailableCopies_shouldThrow_whenAlreadyAtMax() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(bookEntity));

        assertThrows(IllegalStateException.class,
                () -> bookService.increaseAvailableCopies("1"));
    }
}