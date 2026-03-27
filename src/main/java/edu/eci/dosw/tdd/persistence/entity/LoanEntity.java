package edu.eci.dosw.tdd.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatusEntity status;

    @Column(name = "return_date")
    private LocalDate returnDate;
}