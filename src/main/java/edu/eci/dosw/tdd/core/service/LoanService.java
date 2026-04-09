package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNoAvaliableException;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.core.port.LoanRepositoryPort;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepositoryPort loanRepository;
    private final BookService bookService;
    private final UserService userService;

    /**
     * El USER solicita un préstamo → queda en estado PENDING.
     */
    @Transactional
    public Loan requestLoan(String bookId, String userId) {
        ValidationUtil.requireNonBlank(bookId, "ID del libro");
        ValidationUtil.requireNonBlank(userId, "ID del usuario");

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + bookId));

        if (book.getAvailableCopies() <= 0)
            throw new BookNoAvaliableException(bookId);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        List<LoanHistoryEntry> history = new ArrayList<>();
        history.add(LoanHistoryEntry.builder()
                .status(LoanStatus.PENDING.name())
                .executedAt(LocalDateTime.now())
                .build());

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .history(history)
                .build();

        return loanRepository.save(loan);
    }

    /**
     * El LIBRARIAN acepta el préstamo → PENDING → ACCEPTED.
     * Aquí se descuentan las copias disponibles.
     */
    @Transactional
    public Loan acceptLoan(String loanId) {
        ValidationUtil.requireNonBlank(loanId, "ID del préstamo");

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado: " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING)
            throw new IllegalStateException("Solo se pueden aceptar préstamos en estado PENDING");

        bookService.decreaseAvailableCopies(loan.getBook().getId());

        loan.setStatus(LoanStatus.ACCEPTED);
        loan.getHistory().add(LoanHistoryEntry.builder()
                .status(LoanStatus.ACCEPTED.name())
                .executedAt(LocalDateTime.now())
                .build());

        return loanRepository.save(loan);
    }

    /**
     * El LIBRARIAN registra la devolución → ACCEPTED → RETURNED.
     */
    @Transactional
    public Loan returnBook(String bookId, String userId) {
        ValidationUtil.requireNonBlank(bookId, "ID del libro");
        ValidationUtil.requireNonBlank(userId, "ID del usuario");

        Loan loan = loanRepository.findAcceptedLoanByBookAndUser(bookId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay préstamo aceptado activo para ese libro y usuario"));

        loanRepository.markAsReturned(bookId, userId);
        bookService.increaseAvailableCopies(bookId);

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        loan.getHistory().add(LoanHistoryEntry.builder()
                .status(LoanStatus.RETURNED.name())
                .executedAt(LocalDateTime.now())
                .build());

        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getPendingLoans() {
        return loanRepository.findByStatus(LoanStatus.PENDING);
    }

    public List<Loan> getLoansByUser(String userId) {
        return loanRepository.findByUserId(userId);
    }

    /**
     * LIBRARIAN filtra préstamos por userId, con status opcional.
     * Si status es null devuelve todos los préstamos de ese usuario.
     */
    public List<Loan> getLoansByUserIdFilter(String userId, LoanStatus status) {
        ValidationUtil.requireNonBlank(userId, "ID del usuario");
        if (status != null) {
            return loanRepository.findByUserIdAndStatus(userId, status);
        }
        return loanRepository.findByUserId(userId);
    }

    public Loan requestLoanByUsername(String bookId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return requestLoan(bookId, user.getId());
    }

    public Loan returnBookByUsername(String bookId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return returnBook(bookId, user.getId());
    }

    public List<Loan> getLoansByUsername(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return getLoansByUser(user.getId());
    }
}