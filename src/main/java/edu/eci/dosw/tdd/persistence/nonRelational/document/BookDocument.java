package edu.eci.dosw.tdd.persistence.nonRelational.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {

    @Id
    private String id;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    // Campos nuevos Parte 3
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