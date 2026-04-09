package edu.eci.dosw.tdd.core.port;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;

import java.util.List;
import java.util.Optional;

public interface LoanRepositoryPort {
    Loan save(Loan loan);
    List<Loan> findAll();
    List<Loan> findByUserId(String userId);
    List<Loan> findByUserIdAndStatus(String userId, LoanStatus status);
    Optional<Loan> findActiveLoanByBookAndUser(String bookId, String userId);
    void markAsReturned(String bookId, String userId);
    List<Loan> findByStatus(LoanStatus status);
    Optional<Loan> findById(String loanId);
    Optional<Loan> findAcceptedLoanByBookAndUser(String bookId, String userId);
}