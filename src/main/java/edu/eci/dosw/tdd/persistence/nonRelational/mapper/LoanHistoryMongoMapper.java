package edu.eci.dosw.tdd.persistence.nonRelational.mapper;

import edu.eci.dosw.tdd.core.model.LoanHistoryEntry;
import edu.eci.dosw.tdd.persistence.nonRelational.document.LoanHistoryEntryDocument;

public final class LoanHistoryMongoMapper {

    private LoanHistoryMongoMapper() {}

    public static LoanHistoryEntryDocument toDocument(LoanHistoryEntry entry) {
        if (entry == null) return null;
        return LoanHistoryEntryDocument.builder()
                .status(entry.getStatus())
                .executedAt(entry.getExecutedAt())
                .build();
    }

    public static LoanHistoryEntry toDomain(LoanHistoryEntryDocument doc) {
        if (doc == null) return null;
        return LoanHistoryEntry.builder()
                .status(doc.getStatus())
                .executedAt(doc.getExecutedAt())
                .build();
    }
}