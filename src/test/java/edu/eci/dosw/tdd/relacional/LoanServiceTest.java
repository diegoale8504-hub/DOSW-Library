package edu.eci.dosw.tdd.relacional;

import edu.eci.dosw.tdd.core.exception.BookNoAvaliableException;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.core.port.LoanRepositoryPort;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
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

    @Mock private BookService bookService;
    @Mock private UserService userService;
    @Mock private LoanRepositoryPort loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Book book;
    private User user;
    private Loan loan;

    @BeforeEach
    void setUp() {
        book = Book.builder().id("book-1").title("Clean Code")
                .author("Robert C. Martin").totalCopies(5).availableCopies(3).build();

        user = User.builder().id("user-1").name("Diego")
                .username("diego123").password("hashed").role(Role.USER).build();

        loan = Loan.builder().id("loan-1").book(book).user(user)
                .loanDate(LocalDate.now()).status(LoanStatus.ACTIVE).build();
    }

    // ===== Tests originales =====

    @Test
    void loanBook_shouldCreateLoan_whenBookAvailable() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.of(user));
        doNothing().when(bookService).decreaseAvailableCopies("book-1");
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan result = loanService.loanBook("book-1", "user-1");

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(bookService).decreaseAvailableCopies("book-1");
        verify(loanRepository).save(any(Loan.class));
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
        when(loanRepository.findActiveLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).markAsReturned("book-1", "user-1");
        doNothing().when(bookService).increaseAvailableCopies("book-1");
        when(loanRepository.findAll()).thenReturn(List.of(
                Loan.builder().id("loan-1").status(LoanStatus.RETURNED).build()));

        Loan result = loanService.returnBook("book-1", "user-1");

        assertNotNull(result);
        verify(bookService).increaseAvailableCopies("book-1");
        verify(loanRepository).markAsReturned("book-1", "user-1");
    }

    @Test
    void returnBook_shouldThrow_whenNoActiveLoan() {
        when(loanRepository.findActiveLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBook("book-1", "user-1"));
    }

    @Test
    void getAllLoans_shouldReturnList() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        List<Loan> result = loanService.getAllLoans();
        assertEquals(1, result.size());
        assertEquals(LoanStatus.ACTIVE, result.get(0).getStatus());
    }

    // ===== Tests Reto #6 =====

    @Test
    void dadoQueHayUnaReserva_cuandoConsulto_entoncesExitoValidandoId() {
        when(loanRepository.findById("loan-1")).thenReturn(Optional.of(loan));
        Optional<Loan> result = loanRepository.findById("loan-1");
        assertTrue(result.isPresent());
        assertEquals("loan-1", result.get().getId());
    }

    @Test
    void dadoQueNoHayReservas_cuandoConsulto_entoncesNoRetornaNada() {
        when(loanRepository.findAll()).thenReturn(List.of());
        List<Loan> result = loanService.getAllLoans();
        assertTrue(result.isEmpty());
    }

    @Test
    void dadoQueNoHayReservas_cuandoCreo_entoncesCreacionExitosa() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.of(user));
        doNothing().when(bookService).decreaseAvailableCopies("book-1");
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan result = loanService.loanBook("book-1", "user-1");

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
    }

    @Test
    void dadoQueHayUnaReserva_cuandoElimino_entoncesEliminacionExitosa() {
        when(loanRepository.findActiveLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).markAsReturned("book-1", "user-1");
        doNothing().when(bookService).increaseAvailableCopies("book-1");
        when(loanRepository.findAll()).thenReturn(List.of(
                Loan.builder().id("loan-1").status(LoanStatus.RETURNED).build()));

        Loan result = loanService.returnBook("book-1", "user-1");
        assertNotNull(result);
    }

    @Test
    void dadoQueHayUnaReserva_cuandoEliminoYConsulto_entoncesNoRetornaNada() {
        when(loanRepository.findAll()).thenReturn(List.of());
        List<Loan> result = loanService.getAllLoans();
        assertTrue(result.isEmpty());
    }

    @Test
    void getLoansByUser_shouldReturnList() {
        when(loanRepository.findByUserId("user-1")).thenReturn(List.of(loan));
        List<Loan> result = loanService.getLoansByUser("user-1");
        assertEquals(1, result.size());
    }

    @Test
    void loanBookByUsername_shouldCreateLoan() {
        when(userService.getUserByUsername("diego123")).thenReturn(Optional.of(user));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.of(user));
        doNothing().when(bookService).decreaseAvailableCopies("book-1");
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        Loan result = loanService.loanBookByUsername("book-1", "diego123");
        assertNotNull(result);
    }

    @Test
    void loanBookByUsername_shouldThrow_whenUserNotFound() {
        when(userService.getUserByUsername("noexiste")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> loanService.loanBookByUsername("book-1", "noexiste"));
    }

    @Test
    void returnBookByUsername_shouldReturn() {
        when(userService.getUserByUsername("diego123")).thenReturn(Optional.of(user));
        when(loanRepository.findActiveLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).markAsReturned("book-1", "user-1");
        doNothing().when(bookService).increaseAvailableCopies("book-1");
        when(loanRepository.findAll()).thenReturn(List.of(
                Loan.builder().id("loan-1").status(LoanStatus.RETURNED).build()));
        Loan result = loanService.returnBookByUsername("book-1", "diego123");
        assertNotNull(result);
    }

    @Test
    void returnBookByUsername_shouldThrow_whenUserNotFound() {
        when(userService.getUserByUsername("noexiste")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBookByUsername("book-1", "noexiste"));
    }

    @Test
    void getLoansByUsername_shouldReturnList() {
        when(userService.getUserByUsername("diego123")).thenReturn(Optional.of(user));
        when(loanRepository.findByUserId("user-1")).thenReturn(List.of(loan));
        List<Loan> result = loanService.getLoansByUsername("diego123");
        assertEquals(1, result.size());
    }

    @Test
    void getLoansByUsername_shouldThrow_whenUserNotFound() {
        when(userService.getUserByUsername("noexiste")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> loanService.getLoansByUsername("noexiste"));
    }
}