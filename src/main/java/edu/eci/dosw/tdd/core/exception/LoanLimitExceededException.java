package edu.eci.dosw.tdd.core.exception;

public class LoanLimitExceededException extends RuntimeException{
    
    public LoanLimitExceededException(String loanid){
        super("el usuario ha excedido el limite de prestamos" );
    }
}
