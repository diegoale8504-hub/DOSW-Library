package edu.eci.dosw.tdd.persistence.nonRelational.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.port.LoanRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonRelational.document.LoanHistoryEntryDocument;
import edu.eci.dosw.tdd.persistence.nonRelational.mapper.LoanMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
@RequiredArgsConstructor
public class LoanRepositoryMongoImpl implements LoanRepositoryPort {

    private final LoanMongoRepository repository;
    private final LoanMongoMapper mapper;

    @Override
    public Loan save(Loan loan) {
        return mapper.toDomain(repository.save(mapper.toDocument(loan)));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Loan> findActiveLoanByBookAndUser(String bookId, String userId) {
        return repository.findByBookSnapshot_BookIdAndUserIdAndStatus(
                        bookId, userId, LoanStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public void markAsReturned(String bookId, String userId) {
        repository.findByBookSnapshot_BookIdAndUserIdAndStatus(
                        bookId, userId, LoanStatus.ACTIVE.name())
                .ifPresent(doc -> {
                    doc.setStatus(LoanStatus.RETURNED.name());
                    doc.setReturnDate(LocalDate.now().atStartOfDay());
                    LoanHistoryEntryDocument historyEntry = LoanHistoryEntryDocument.builder()
                            .status(LoanStatus.RETURNED.name())
                            .executedAt(LocalDateTime.now())
                            .build();
                    doc.getHistory().add(historyEntry);
                    repository.save(doc);
                });
    }
}