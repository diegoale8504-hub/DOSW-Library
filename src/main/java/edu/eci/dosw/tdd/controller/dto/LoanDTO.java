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
    private String bookTitle;
    private String bookAuthor;
    private String userId;
    private String username;
    private LocalDate loanDate;
    private String status;
    private LocalDate returnDate;
}