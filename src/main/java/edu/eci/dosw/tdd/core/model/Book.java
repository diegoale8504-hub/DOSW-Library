package edu.eci.dosw.tdd.core.model;

import lombok.*;

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
}