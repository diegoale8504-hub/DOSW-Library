package edu.eci.dosw.tdd.persistence.nonRelational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.persistence.nonRelational.document.BookSnapshot;
import edu.eci.dosw.tdd.persistence.nonRelational.document.LoanDocument;
import org.springframework.stereotype.Component;

@Component
public class LoanMongoMapper {

    public LoanDocument toDocument(Loan loan) {
        BookSnapshot snapshot = BookSnapshot.builder()
                .bookId(loan.getBook().getId())
                .title(loan.getBook().getTitle())
                .author(loan.getBook().getAuthor())
                .build();

        return LoanDocument.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .bookSnapshot(snapshot)
                .status(loan.getStatus().name())
                .loanDate(loan.getLoanDate() != null
                        ? loan.getLoanDate().atStartOfDay() : null)
                .returnDate(loan.getReturnDate() != null
                        ? loan.getReturnDate().atStartOfDay() : null)
                .build();
    }

    public Loan toDomain(LoanDocument doc) {
        return Loan.builder()
                .id(doc.getId())
                .status(LoanStatus.valueOf(doc.getStatus()))
                .loanDate(doc.getLoanDate() != null
                        ? doc.getLoanDate().toLocalDate() : null)
                .returnDate(doc.getReturnDate() != null
                        ? doc.getReturnDate().toLocalDate() : null)
                .build();
    }
}