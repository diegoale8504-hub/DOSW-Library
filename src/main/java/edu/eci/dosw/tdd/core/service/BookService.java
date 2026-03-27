package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book addBook(Book book) {
        ValidationUtil.validateNotNull(book, "Book");
        ValidationUtil.validateNotEmpty(book.getTitle(), "Título");
        ValidationUtil.validateNotEmpty(book.getAuthor(), "Autor");
        if (book.getTotalCopies() <= 0)
            throw new IllegalArgumentException("El total de ejemplares debe ser mayor a 0");
        if (book.getAvailableCopies() < 0)
            throw new IllegalArgumentException("Los ejemplares disponibles no pueden ser negativos");

        BookEntity saved = bookRepository.save(BookPersistenceMapper.toEntity(book));
        return BookPersistenceMapper.toDomain(saved);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream()
                .map(BookPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id).map(BookPersistenceMapper::toDomain);
    }

    @Transactional
    public Book updateBook(String id, Book updatedBook) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + id));
        entity.setTitle(updatedBook.getTitle());
        entity.setAuthor(updatedBook.getAuthor());
        if (updatedBook.getTotalCopies() <= 0)
            throw new IllegalArgumentException("El total de ejemplares debe ser mayor a 0");
        entity.setTotalCopies(updatedBook.getTotalCopies());
        entity.setAvailableCopies(updatedBook.getAvailableCopies());
        return BookPersistenceMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional
    public void decreaseAvailableCopies(String bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + bookId));
        if (entity.getAvailableCopies() <= 0)
            throw new IllegalStateException("No hay ejemplares disponibles");
        entity.setAvailableCopies(entity.getAvailableCopies() - 1);
        bookRepository.save(entity);
    }

    @Transactional
    public void increaseAvailableCopies(String bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + bookId));
        if (entity.getAvailableCopies() >= entity.getTotalCopies())
            throw new IllegalStateException("Los ejemplares disponibles ya están al máximo");
        entity.setAvailableCopies(entity.getAvailableCopies() + 1);
        bookRepository.save(entity);
    }

    @Transactional
    public void deleteBook(String id) {
        if (!bookRepository.existsById(id))
            throw new IllegalArgumentException("Libro no encontrado: " + id);
        bookRepository.deleteById(id);
    }
}