package edu.eci.dosw.tdd.core.exception;

public class BookNoAvaliableException extends RuntimeException {

    public BookNoAvaliableException(String bookId) {
        super("El libro con ID " + bookId + " no está disponible para préstamo.");
    }
}