package edu.eci.dosw.tdd.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;
    private boolean avaliable;
}