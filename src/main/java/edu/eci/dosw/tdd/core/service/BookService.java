package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.port.BookRepositoryPort;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepositoryPort bookRepository;

    @Transactional
    public Book addBook(Book book) {
        ValidationUtil.validateNotNull(book, "Book");
        ValidationUtil.validateNotEmpty(book.getTitle(), "Título");
        ValidationUtil.validateNotEmpty(book.getAuthor(), "Autor");
        if (book.getTotalCopies() <= 0)
            throw new IllegalArgumentException("El total de ejemplares debe ser mayor a 0");
        if (book.getAvailableCopies() < 0)
            throw new IllegalArgumentException("Los ejemplares disponibles no pueden ser negativos");
        if (book.getAddedToCatalogDate() == null)
            book.setAddedToCatalogDate(LocalDate.now());
        if (book.getAvailabilityStatus() == null)
            book.setAvailabilityStatus("AVAILABLE");

        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailable();
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book updateBook(String id, Book updatedBook) {
        bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + id));
        if (updatedBook.getTotalCopies() <= 0)
            throw new IllegalArgumentException("El total de ejemplares debe ser mayor a 0");
        updatedBook.setId(id);
        return bookRepository.save(updatedBook);
    }

    @Transactional
    public void decreaseAvailableCopies(String bookId) {
        bookRepository.updateAvailableCopies(bookId, -1);
    }

    @Transactional
    public void increaseAvailableCopies(String bookId) {
        bookRepository.updateAvailableCopies(bookId, +1);
    }

    @Transactional
    public void deleteBook(String id) {
        if (!bookRepository.existsById(id))
            throw new IllegalArgumentException("Libro no encontrado: " + id);
        bookRepository.deleteById(id);
    }
}