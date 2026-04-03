package edu.eci.dosw.tdd.persistence.relacional.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "loan_history")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanHistoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    @Column(nullable = false)
    private String status;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;
}