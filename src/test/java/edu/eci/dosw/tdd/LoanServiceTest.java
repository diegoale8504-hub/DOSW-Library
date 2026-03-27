package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNoAvaliableException;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanStatusEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.entity.RoleEntity;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Book book;
    private User user;
    private LoanEntity loanEntity;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id("book-1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalCopies(5)
                .availableCopies(3)
                .build();

        user = User.builder()
                .id("user-1")
                .name("Diego")
                .username("diego123")
                .password("hashed")
                .role(Role.USER)
                .build();

        BookEntity bookEntity = BookEntity.builder()
                .id("book-1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .totalCopies(5)
                .availableCopies(3)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id("user-1")
                .name("Diego")
                .username("diego123")
                .password("hashed")
                .role(RoleEntity.USER)
                .build();

        loanEntity = LoanEntity.builder()
                .id("loan-1")
                .book(bookEntity)
                .user(userEntity)
                .loanDate(LocalDate.now())
                .status(LoanStatusEntity.ACTIVE)
                .build();
    }

    @Test
    void loanBook_shouldCreateLoan_whenBookAvailable() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.of(user));
        doNothing().when(bookService).decreaseAvailableCopies("book-1");
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);

        Loan result = loanService.loanBook("book-1", "user-1");

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(bookService).decreaseAvailableCopies("book-1");
        verify(loanRepository).save(any(LoanEntity.class));
    }

    @Test
    void loanBook_shouldThrow_whenBookNotAvailable() {
        book.setAvailableCopies(0);
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));

        assertThrows(BookNoAvaliableException.class,
                () -> loanService.loanBook("book-1", "user-1"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void loanBook_shouldThrow_whenBookNotFound() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.loanBook("book-1", "user-1"));
    }

    @Test
    void loanBook_shouldThrow_whenUserNotFound() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.loanBook("book-1", "user-1"));
    }

    @Test
    void returnBook_shouldMarkAsReturned_whenActiveLoanExists() {
        when(loanRepository.findByBookIdAndUserIdAndStatus(
                "book-1", "user-1", LoanStatusEntity.ACTIVE))
                .thenReturn(Optional.of(loanEntity));
        when(loanRepository.save(any())).thenReturn(loanEntity);
        doNothing().when(bookService).increaseAvailableCopies("book-1");

        Loan result = loanService.returnBook("book-1", "user-1");

        assertNotNull(result);
        verify(bookService).increaseAvailableCopies("book-1");
        verify(loanRepository).save(any(LoanEntity.class));
    }

    @Test
    void returnBook_shouldThrow_whenNoActiveLoan() {
        when(loanRepository.findByBookIdAndUserIdAndStatus(
                "book-1", "user-1", LoanStatusEntity.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBook("book-1", "user-1"));
    }

    @Test
    void getAllLoans_shouldReturnList() {
        when(loanRepository.findAll()).thenReturn(List.of(loanEntity));

        List<Loan> result = loanService.getAllLoans();

        assertEquals(1, result.size());
        assertEquals(LoanStatus.ACTIVE, result.get(0).getStatus());
    }
}