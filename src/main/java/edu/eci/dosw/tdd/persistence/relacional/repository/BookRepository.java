package edu.eci.dosw.tdd.persistence.relacional.repository;

import edu.eci.dosw.tdd.persistence.relacional.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, String> {
    List<BookEntity> findByAvailableCopiesGreaterThan(int copies);
}