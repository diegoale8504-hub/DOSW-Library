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
import java.util.ArrayList;
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
    private Loan pendingLoan;
    private Loan acceptedLoan;

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

        // Loan en PENDING con history mutable (como lo devuelve el mapper real)
        pendingLoan = Loan.builder()
                .id("loan-1")
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .history(new ArrayList<>())
                .build();

        // Loan en ACCEPTED con history mutable
        acceptedLoan = Loan.builder()
                .id("loan-2")
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .status(LoanStatus.ACCEPTED)
                .history(new ArrayList<>())
                .build();
    }

    // ===== requestLoan =====

    @Test
    void requestLoan_deberiaCrearPrestamo_cuandoLibroDisponible() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenReturn(pendingLoan);

        Loan result = loanService.requestLoan("book-1", "user-1");

        assertNotNull(result);
        assertEquals(LoanStatus.PENDING, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
        // NO llama a decreaseAvailableCopies — eso ocurre en acceptLoan
        verify(bookService, never()).decreaseAvailableCopies(any());
    }

    @Test
    void requestLoan_deberiaLanzarExcepcion_cuandoLibroNoDisponible() {
        book.setAvailableCopies(0);
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));

        assertThrows(BookNoAvaliableException.class,
                () -> loanService.requestLoan("book-1", "user-1"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void requestLoan_deberiaLanzarExcepcion_cuandoLibroNoExiste() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.requestLoan("book-1", "user-1"));
    }

    @Test
    void requestLoan_deberiaLanzarExcepcion_cuandoUsuarioNoExiste() {
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.requestLoan("book-1", "user-1"));
    }

    @Test
    void requestLoan_deberiaLanzarExcepcion_cuandoBookIdEsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.requestLoan("", "user-1"));
    }

    @Test
    void requestLoan_deberiaLanzarExcepcion_cuandoUserIdEsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.requestLoan("book-1", ""));
    }

    // ===== acceptLoan =====

    @Test
    void acceptLoan_deberiaCambiarEstadoAAccepted_cuandoPrestamoEsPending() {
        when(loanRepository.findById("loan-1")).thenReturn(Optional.of(pendingLoan));
        doNothing().when(bookService).decreaseAvailableCopies("book-1");
        when(loanRepository.save(any(Loan.class))).thenReturn(pendingLoan);

        Loan result = loanService.acceptLoan("loan-1");

        assertNotNull(result);
        verify(bookService).decreaseAvailableCopies("book-1");
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void acceptLoan_deberiaLanzarExcepcion_cuandoPrestamoNoExiste() {
        when(loanRepository.findById("loan-x")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.acceptLoan("loan-x"));
    }

    @Test
    void acceptLoan_deberiaLanzarExcepcion_cuandoPrestamoNoEsPending() {
        when(loanRepository.findById("loan-2")).thenReturn(Optional.of(acceptedLoan));

        assertThrows(IllegalStateException.class,
                () -> loanService.acceptLoan("loan-2"));
        verify(bookService, never()).decreaseAvailableCopies(any());
    }

    @Test
    void acceptLoan_deberiaLanzarExcepcion_cuandoLoanIdEsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.acceptLoan(""));
    }

    // ===== returnBook =====

    @Test
    void returnBook_deberiaCambiarEstadoAReturned_cuandoHayPrestamoAceptado() {
        when(loanRepository.findAcceptedLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.of(acceptedLoan));
        doNothing().when(loanRepository).markAsReturned("book-1", "user-1");
        doNothing().when(bookService).increaseAvailableCopies("book-1");
        when(loanRepository.save(any(Loan.class))).thenReturn(acceptedLoan);

        Loan result = loanService.returnBook("book-1", "user-1");

        assertNotNull(result);
        verify(bookService).increaseAvailableCopies("book-1");
        verify(loanRepository).markAsReturned("book-1", "user-1");
    }

    @Test
    void returnBook_deberiaLanzarExcepcion_cuandoNoHayPrestamoAceptado() {
        when(loanRepository.findAcceptedLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBook("book-1", "user-1"));
    }

    @Test
    void returnBook_deberiaLanzarExcepcion_cuandoBookIdEsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBook("", "user-1"));
    }

    @Test
    void returnBook_deberiaLanzarExcepcion_cuandoUserIdEsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBook("book-1", ""));
    }

    // ===== getAllLoans / getPendingLoans / getLoansByUser =====

    @Test
    void getAllLoans_deberiaRetornarLista() {
        when(loanRepository.findAll()).thenReturn(List.of(pendingLoan));

        List<Loan> result = loanService.getAllLoans();

        assertEquals(1, result.size());
        assertEquals(LoanStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void getAllLoans_deberiaRetornarListaVacia_cuandoNoHayPrestamos() {
        when(loanRepository.findAll()).thenReturn(List.of());

        List<Loan> result = loanService.getAllLoans();

        assertTrue(result.isEmpty());
    }

    @Test
    void getPendingLoans_deberiaRetornarSoloPending() {
        when(loanRepository.findByStatus(LoanStatus.PENDING))
                .thenReturn(List.of(pendingLoan));

        List<Loan> result = loanService.getPendingLoans();

        assertEquals(1, result.size());
        assertEquals(LoanStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void getLoansByUser_deberiaRetornarPrestamosDelUsuario() {
        when(loanRepository.findByUserId("user-1")).thenReturn(List.of(pendingLoan));

        List<Loan> result = loanService.getLoansByUser("user-1");

        assertEquals(1, result.size());
    }

    // ===== requestLoanByUsername =====

    @Test
    void requestLoanByUsername_deberiaCrearPrestamo_cuandoUsernameExiste() {
        when(userService.getUserByUsername("diego123")).thenReturn(Optional.of(user));
        when(bookService.getBookById("book-1")).thenReturn(Optional.of(book));
        when(userService.getUserById("user-1")).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenReturn(pendingLoan);

        Loan result = loanService.requestLoanByUsername("book-1", "diego123");

        assertNotNull(result);
        assertEquals(LoanStatus.PENDING, result.getStatus());
    }

    @Test
    void requestLoanByUsername_deberiaLanzarExcepcion_cuandoUsernameNoExiste() {
        when(userService.getUserByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.requestLoanByUsername("book-1", "noexiste"));
    }

    // ===== returnBookByUsername =====

    @Test
    void returnBookByUsername_deberiaRetornarLibro_cuandoUsernameExiste() {
        when(userService.getUserByUsername("diego123")).thenReturn(Optional.of(user));
        when(loanRepository.findAcceptedLoanByBookAndUser("book-1", "user-1"))
                .thenReturn(Optional.of(acceptedLoan));
        doNothing().when(loanRepository).markAsReturned("book-1", "user-1");
        doNothing().when(bookService).increaseAvailableCopies("book-1");
        when(loanRepository.save(any(Loan.class))).thenReturn(acceptedLoan);

        Loan result = loanService.returnBookByUsername("book-1", "diego123");

        assertNotNull(result);
        verify(bookService).increaseAvailableCopies("book-1");
    }

    @Test
    void returnBookByUsername_deberiaLanzarExcepcion_cuandoUsernameNoExiste() {
        when(userService.getUserByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBookByUsername("book-1", "noexiste"));
    }

    // ===== getLoansByUsername =====

    @Test
    void getLoansByUsername_deberiaRetornarLista_cuandoUsernameExiste() {
        when(userService.getUserByUsername("diego123")).thenReturn(Optional.of(user));
        when(loanRepository.findByUserId("user-1")).thenReturn(List.of(pendingLoan));

        List<Loan> result = loanService.getLoansByUsername("diego123");

        assertEquals(1, result.size());
    }

    @Test
    void getLoansByUsername_deberiaLanzarExcepcion_cuandoUsernameNoExiste() {
        when(userService.getUserByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.getLoansByUsername("noexiste"));
    }
}