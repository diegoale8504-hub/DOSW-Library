package edu.eci.dosw.tdd.persistence.nonRelational.document;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanHistoryEntryDocument {
    private String status;
    private LocalDateTime executedAt;
}