package edu.eci.dosw.tdd.core.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanHistoryEntry {
    private String status;
    private LocalDateTime executedAt;
}