package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNoAvaliableException;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import edu.eci.dosw.tdd.persistence.entity.LoanStatusEntity;
import edu.eci.dosw.tdd.persistence.mapper.LoanPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final BookService bookService;
    private final UserService userService;
    private final LoanRepository loanRepository;

    public LoanService(BookService bookService, UserService userService,
                       LoanRepository loanRepository) {
        this.bookService = bookService;
        this.userService = userService;
        this.loanRepository = loanRepository;
    }

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

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .status(LoanStatus.ACTIVE)
                .build();

        return LoanPersistenceMapper.toDomain(
                loanRepository.save(LoanPersistenceMapper.toEntity(loan)));
    }

    @Transactional
    public Loan returnBook(String bookId, String userId) {
        ValidationUtil.requireNonBlank(bookId, "ID del libro");
        ValidationUtil.requireNonBlank(userId, "ID del usuario");

        var loanEntity = loanRepository
                .findByBookIdAndUserIdAndStatus(bookId, userId, LoanStatusEntity.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay préstamo activo para ese libro y usuario"));

        loanEntity.setReturnDate(LocalDate.now());
        loanEntity.setStatus(LoanStatusEntity.RETURNED);
        loanRepository.save(loanEntity);

        bookService.increaseAvailableCopies(bookId);

        return LoanPersistenceMapper.toDomain(loanEntity);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(LoanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansByUser(String userId) {
        return loanRepository.findByUserId(userId).stream()
                .map(LoanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
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