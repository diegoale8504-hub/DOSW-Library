package edu.eci.dosw.tdd.persistence.relacional.repository;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.port.BookRepositoryPort;
import edu.eci.dosw.tdd.persistence.relacional.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relacional.mapper.BookPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("relational")
@RequiredArgsConstructor
public class BookRepositoryJpaImpl implements BookRepositoryPort {

    private final BookRepository repository;

    @Override
    public Book save(Book book) {
        BookEntity saved = repository.save(BookPersistenceMapper.toEntity(book));
        return BookPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id).map(BookPersistenceMapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream()
                .map(BookPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findAvailable() {
        return repository.findByAvailableCopiesGreaterThan(0).stream()
                .map(BookPersistenceMapper::toDomain)
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
        BookEntity entity = repository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + bookId));
        int updated = entity.getAvailableCopies() + delta;
        if (updated < 0)
            throw new IllegalStateException("No hay ejemplares disponibles");
        if (updated > entity.getTotalCopies())
            throw new IllegalStateException("Ejemplares disponibles ya están al máximo");
        entity.setAvailableCopies(updated);
        entity.setLoanedCopies(entity.getTotalCopies() - updated);
        repository.save(entity);
    }
}