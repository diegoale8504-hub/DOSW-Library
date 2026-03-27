package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    List<LoanEntity> findByUserId(String userId);
    Optional<LoanEntity> findByBookIdAndUserIdAndStatus(
            String bookId, String userId, LoanStatusEntity status);
    List<LoanEntity> findByStatus(LoanStatusEntity status);
}