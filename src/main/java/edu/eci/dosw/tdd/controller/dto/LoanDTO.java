package edu.eci.dosw.tdd.controller.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private String id;
    private String bookId;
    private String userId;
    private LocalDate loanDate;
    private String status;
    private LocalDate returnDate;
}