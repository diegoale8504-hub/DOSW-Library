package edu.eci.dosw.tdd.persistence.relacional.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.port.LoanRepositoryPort;
import edu.eci.dosw.tdd.persistence.relacional.entity.LoanHistoryEntity;
import edu.eci.dosw.tdd.persistence.relacional.entity.LoanStatusEntity;
import edu.eci.dosw.tdd.persistence.relacional.mapper.LoanPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("relational")
@RequiredArgsConstructor
public class LoanRepositoryJpaImpl implements LoanRepositoryPort {

    private final LoanRepository repository;

    @Override
    public Loan save(Loan loan) {
        return LoanPersistenceMapper.toDomain(
                repository.save(LoanPersistenceMapper.toEntity(loan)));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return repository.findById(id).map(LoanPersistenceMapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll().stream()
                .map(LoanPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return repository.findByUserId(userId).stream()
                .map(LoanPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Loan> findActiveLoanByBookAndUser(String bookId, String userId) {
        return repository.findByBookIdAndUserIdAndStatus(bookId, userId, LoanStatusEntity.ACTIVE)
                .map(LoanPersistenceMapper::toDomain);
    }

    @Override
    public void markAsReturned(String bookId, String userId) {
        repository.findByBookIdAndUserIdAndStatus(bookId, userId, LoanStatusEntity.ACTIVE)
                .ifPresent(entity -> {
                    entity.setStatus(LoanStatusEntity.RETURNED);
                    entity.setReturnDate(LocalDate.now());
                    LoanHistoryEntity historyEntry = LoanHistoryEntity.builder()
                            .loan(entity)
                            .status(LoanStatus.RETURNED.name())
                            .executedAt(LocalDateTime.now())
                            .build();
                    entity.getHistory().add(historyEntry);
                    repository.save(entity);
                });
    }
}