package edu.eci.dosw.tdd.persistence.nonRelational.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LoanDocument {

    @Id
    private String id;

    private String userId;

    private BookSnapshot bookSnapshot;

    private String status;

    private LocalDateTime loanDate;

    private LocalDateTime returnDate;

    @Builder.Default
    private List<LoanHistoryEntryDocument> history = new ArrayList<>();

}