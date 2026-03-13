package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> loanBook(@RequestParam String bookId,
                                         @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.loanBook(bookId, userId));
    }

    @PatchMapping("/return")
    public ResponseEntity<Loan> returnBook(@RequestParam String bookId,
                                           @RequestParam String userId) {
        return ResponseEntity.ok(loanService.returnBook(bookId, userId));
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
}