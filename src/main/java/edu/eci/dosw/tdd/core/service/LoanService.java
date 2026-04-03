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

    @Transactional
    public Loan loanBook(String bookId, String userId) {
        ValidationUtil.requireNonBlank(bookId, "ID del libro");
        ValidationUtil.requireNonBlank(userId, "ID del usuario");

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + bookId));

        if (book.getAvailableCopies() <= 0)
            throw new BookNoAvaliableException(bookId);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        bookService.decreaseAvailableCopies(bookId);

        List<LoanHistoryEntry> history = new ArrayList<>();
        history.add(LoanHistoryEntry.builder()
                .status(LoanStatus.ACTIVE.name())
                .executedAt(LocalDateTime.now())
                .build());

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .status(LoanStatus.ACTIVE)
                .history(history)
                .build();

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(String bookId, String userId) {
        ValidationUtil.requireNonBlank(bookId, "ID del libro");
        ValidationUtil.requireNonBlank(userId, "ID del usuario");

        loanRepository.findActiveLoanByBookAndUser(bookId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay préstamo activo para ese libro y usuario"));

        loanRepository.markAsReturned(bookId, userId);
        bookService.increaseAvailableCopies(bookId);

        return loanRepository.findActiveLoanByBookAndUser(bookId, userId)
                .orElse(loanRepository.findAll().stream()
                        .filter(l -> l.getStatus() == LoanStatus.RETURNED)
                        .findFirst().orElseThrow());
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByUser(String userId) {
        return loanRepository.findByUserId(userId);
    }

    public Loan loanBookByUsername(String bookId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return loanBook(bookId, user.getId());
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