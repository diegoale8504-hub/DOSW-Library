package edu.eci.dosw.tdd.persistence.nonRelational.document;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSnapshot {

    private String bookId;
    private String title;
    private String author;
}