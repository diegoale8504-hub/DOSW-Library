package edu.eci.dosw.tdd.persistence.nonRelational.mapper;

import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.persistence.nonRelational.document.BookSnapshot;
import edu.eci.dosw.tdd.persistence.nonRelational.document.LoanDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
                .history(loan.getHistory() != null
                        ? loan.getHistory().stream()
                        .map(LoanHistoryMongoMapper::toDocument)
                        .toList()
                        : List.of())
                .build();
    }

    public Loan toDomain(LoanDocument doc) {
        Book book = null;
        if (doc.getBookSnapshot() != null) {
            book = Book.builder()
                    .id(doc.getBookSnapshot().getBookId())
                    .title(doc.getBookSnapshot().getTitle())
                    .author(doc.getBookSnapshot().getAuthor())
                    .build();
        }

        User user = null;
        if (doc.getUserId() != null) {
            user = User.builder()
                    .id(doc.getUserId())
                    .build();
        }

        // ⚠️ history DEBE ser mutable (ArrayList), nunca null.
        // Si viene null de Mongo (documentos viejos sin history), se inicializa vacío.
        List<LoanHistoryEntry> history = new ArrayList<>();
        if (doc.getHistory() != null) {
            doc.getHistory().stream()
                    .map(LoanHistoryMongoMapper::toDomain)
                    .forEach(history::add);
        }

        return Loan.builder()
                .id(doc.getId())
                .book(book)
                .user(user)
                .status(LoanStatus.valueOf(doc.getStatus()))
                .loanDate(doc.getLoanDate() != null
                        ? doc.getLoanDate().toLocalDate() : null)
                .returnDate(doc.getReturnDate() != null
                        ? doc.getReturnDate().toLocalDate() : null)
                .history(history)
                .build();
    }
}