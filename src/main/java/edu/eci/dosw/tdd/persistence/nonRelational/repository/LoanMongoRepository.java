package edu.eci.dosw.tdd.persistence.nonRelational.repository;

import edu.eci.dosw.tdd.persistence.nonRelational.document.LoanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanMongoRepository
        extends MongoRepository<LoanDocument, String> {

    List<LoanDocument> findByUserId(String userId);
    List<LoanDocument> findByStatus(String status);
    Optional<LoanDocument> findByIdAndUserId(String id, String userId);
    Optional<LoanDocument> findByBookSnapshot_BookIdAndUserIdAndStatus(
            String bookId, String userId, String status);

    long countByUserIdAndStatus(String userId, String status);
}