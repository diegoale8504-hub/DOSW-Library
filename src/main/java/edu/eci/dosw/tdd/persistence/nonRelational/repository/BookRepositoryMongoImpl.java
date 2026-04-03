package edu.eci.dosw.tdd.persistence.nonRelational.repository;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.port.BookRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonRelational.document.BookDocument;
import edu.eci.dosw.tdd.persistence.nonRelational.mapper.BookMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
@RequiredArgsConstructor
public class BookRepositoryMongoImpl implements BookRepositoryPort {

    private final BookMongoRepository repository;
    private final BookMongoMapper mapper;

    @Override
    public Book save(Book book) {
        BookDocument saved = repository.save(mapper.toDocument(book));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findAvailable() {
        return repository.findByAvailableCopiesGreaterThan(0).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public void updateAvailableCopies(String bookId, int delta) {
        BookDocument doc = repository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + bookId));
        int updated = doc.getAvailableCopies() + delta;
        if (updated < 0)
            throw new IllegalStateException("No hay ejemplares disponibles");
        if (updated > doc.getTotalCopies())
            throw new IllegalStateException("Ejemplares disponibles ya están al máximo");
        doc.setAvailableCopies(updated);
        doc.setLoanedCopies(doc.getTotalCopies() - updated);
        repository.save(doc);
    }
}