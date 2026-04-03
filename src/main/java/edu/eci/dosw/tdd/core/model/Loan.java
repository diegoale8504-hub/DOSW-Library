package edu.eci.dosw.tdd.core.model;

import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LoanStatus status;
    private LocalDate returnDate;

    @Builder.Default
    private List<LoanHistoryEntry> history = new ArrayList<>();
}