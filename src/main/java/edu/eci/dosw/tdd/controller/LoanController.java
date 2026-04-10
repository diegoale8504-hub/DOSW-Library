package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /** USER solicita préstamo → PENDING. El userId sale del token JWT. */
    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LoanDTO> requestLoan(@RequestParam String bookId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LoanMapper.toDTO(
                        loanService.requestLoanByUsername(bookId, userDetails.getUsername())));
    }

    /** USER ve sus propios préstamos. */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LoanDTO>> getMyLoans(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                loanService.getLoansByUsername(userDetails.getUsername())
                        .stream().map(LoanMapper::toDTO).toList());
    }

    /** LIBRARIAN ve todos los préstamos en PENDING. */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<LoanDTO>> getPendingLoans() {
        return ResponseEntity.ok(
                loanService.getPendingLoans()
                        .stream().map(LoanMapper::toDTO).toList());
    }

    /** LIBRARIAN acepta un préstamo (PENDING → ACCEPTED). */
    @PatchMapping("/{loanId}/accept")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<LoanDTO> acceptLoan(@PathVariable String loanId) {
        return ResponseEntity.ok(LoanMapper.toDTO(loanService.acceptLoan(loanId)));
    }

    /** LIBRARIAN registra devolución (ACCEPTED → RETURNED). */
    @PatchMapping("/return")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<LoanDTO> returnBook(@RequestParam String bookId,
                                              @RequestParam String userId) {
        return ResponseEntity.ok(LoanMapper.toDTO(loanService.returnBook(bookId, userId)));
    }

    /**
     * LIBRARIAN ve todos los préstamos.
     * Filtros opcionales: ?userId=abc  y/o  ?status=PENDING
     */
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<LoanDTO>> getAllLoans(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) LoanStatus status) {

        List<LoanDTO> result;
        if (userId != null && !userId.isBlank()) {
            result = loanService.getLoansByUserIdFilter(userId, status)
                    .stream().map(LoanMapper::toDTO).toList();
        } else if (status != null) {
            result = loanService.getPendingLoans()
                    .stream().map(LoanMapper::toDTO).toList();
        } else {
            result = loanService.getAllLoans()
                    .stream().map(LoanMapper::toDTO).toList();
        }
        return ResponseEntity.ok(result);
    }
}