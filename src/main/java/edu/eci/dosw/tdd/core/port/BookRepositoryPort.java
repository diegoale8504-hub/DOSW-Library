package edu.eci.dosw.tdd.core.port;

import edu.eci.dosw.tdd.core.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    Book save(Book book);
    Optional<Book> findById(String id);
    List<Book> findAll();
    List<Book> findAvailable();
    void deleteById(String id);
    boolean existsById(String id);
    void updateAvailableCopies(String bookId, int delta);
}