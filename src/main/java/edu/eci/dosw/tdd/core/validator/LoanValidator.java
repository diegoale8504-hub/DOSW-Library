package edu.eci.dosw.tdd.core.validator;



import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.util.ValidationUtil;

public final class LoanValidator {

    private LoanValidator() {}

    public static void validateForCreate(LoanDTO dto) {
        ValidationUtil.requireNonNull(dto, "loanDTO");
        ValidationUtil.requireNonBlank(dto.getBookId(), "loan.bookId");
        ValidationUtil.requireNonBlank(dto.getUserId(), "loan.userId");
    }

    public static void validateReturn(String loanId) {
        ValidationUtil.requireNonBlank(loanId, "loanId");
    }
}
