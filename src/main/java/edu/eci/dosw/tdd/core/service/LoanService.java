package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNoAvaliableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final BookService bookService;
    private final UserService userService;
    private final List<Loan> loans = new ArrayList<>();

    public LoanService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    /**
     * Crea un préstamo para un usuario dado un libro.
     * Lanza BookNoAvaliableException si el libro no está disponible.
     */
    public Loan loanBook(String bookId, String userId) {
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + bookId));

        if (!book.isAvailable()) {
            throw new BookNoAvaliableException(bookId);
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        bookService.updateAvailability(bookId, false);

        Loan loan = new Loan(book, user, LocalDate.now(), null, LoanStatus.ACTIVE);
        loans.add(loan);
        return loan;
    }

    /**
     * Registra la devolución de un libro.
     */
    public Loan returnBook(String bookId, String userId) {
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        Loan loan = loans.stream()
                .filter(l -> l.getBook().getId().equals(bookId)
                        && l.getUser().getId().equals(userId)
                        && l.getStatus() == LoanStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró un préstamo activo para el libro " + bookId + " y usuario " + userId));

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        bookService.updateAvailability(bookId, true);
        return loan;
    }

    /**
     * Retorna todos los préstamos registrados.
     */
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }
}