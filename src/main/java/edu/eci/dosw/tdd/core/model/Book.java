package edu.eci.dosw.tdd.core.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    private List<String> categories;
    private String publicationType;
    private LocalDate publicationDate;
    private String isbn;
    private String metadataPages;
    private String metadataLanguage;
    private String metadataPublisher;
    private String availabilityStatus;
    private int loanedCopies;
    private LocalDate addedToCatalogDate;
}