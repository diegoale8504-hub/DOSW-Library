package edu.eci.dosw.tdd.persistence.nonRelational.repository;

import edu.eci.dosw.tdd.persistence.nonRelational.document.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookMongoRepository extends MongoRepository<BookDocument, String> {
    List<BookDocument> findByAvailableCopiesGreaterThan(int copies);
}