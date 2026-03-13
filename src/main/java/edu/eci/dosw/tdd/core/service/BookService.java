package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final List<Book> books = new ArrayList<>();

    /**
     * Agrega un libro al sistema.
     */
    public Book addBook(Book book) {
        ValidationUtil.validateNotNull(book, "Book");
        ValidationUtil.validateNotEmpty(book.getId(), "ID del libro");
        ValidationUtil.validateNotEmpty(book.getTitle(), "Título del libro");
        ValidationUtil.validateNotEmpty(book.getAuthor(), "Autor del libro");
        books.add(book);
        return book;
    }

    /**
     * Retorna todos los libros registrados.
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Busca un libro por su ID.
     */
    public Optional<Book> getBookById(String id) {
        ValidationUtil.validateNotEmpty(id, "ID del libro");
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    /**
     * Actualiza la disponibilidad de un libro.
     */
    public Book updateAvailability(String id, boolean available) {
        ValidationUtil.validateNotEmpty(id, "ID del libro");
        Book book = getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + id));
        book.setAvailable(available);
        return book;
    }
}