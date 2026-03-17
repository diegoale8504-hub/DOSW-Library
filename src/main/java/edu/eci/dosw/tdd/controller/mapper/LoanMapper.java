package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;

public final class LoanMapper {

    private LoanMapper() {}

    public static LoanDTO toDTO(Loan loan) {
        if (loan == null) return null;

        return LoanDTO.builder()
                .id(loan.getId())
                .bookId(loan.getBook() != null ? loan.getBook().getId() : null)
                .userId(loan.getUser() != null ? loan.getUser().getId() : null)
                .loanDate(loan.getLoanDate())
                .status(loan.getStatus() != null ? loan.getStatus().name() : null)
                .returnDate(loan.getReturnDate())
                .build();
    }
}