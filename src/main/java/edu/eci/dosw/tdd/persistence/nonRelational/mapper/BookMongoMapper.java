package edu.eci.dosw.tdd.persistence.nonRelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.nonRelational.document.BookDocument;
import org.springframework.stereotype.Component;

@Component
public class BookMongoMapper {

    public BookDocument toDocument(Book book) {
        return BookDocument.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .categories(book.getCategories())
                .publicationType(book.getPublicationType())
                .publicationDate(book.getPublicationDate())
                .isbn(book.getIsbn())
                .metadataPages(book.getMetadataPages())
                .metadataLanguage(book.getMetadataLanguage())
                .metadataPublisher(book.getMetadataPublisher())
                .availabilityStatus(book.getAvailabilityStatus())
                .loanedCopies(book.getLoanedCopies())
                .addedToCatalogDate(book.getAddedToCatalogDate())
                .build();
    }

    public Book toDomain(BookDocument doc) {
        return Book.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .author(doc.getAuthor())
                .totalCopies(doc.getTotalCopies())
                .availableCopies(doc.getAvailableCopies())
                .categories(doc.getCategories())
                .publicationType(doc.getPublicationType())
                .publicationDate(doc.getPublicationDate())
                .isbn(doc.getIsbn())
                .metadataPages(doc.getMetadataPages())
                .metadataLanguage(doc.getMetadataLanguage())
                .metadataPublisher(doc.getMetadataPublisher())
                .availabilityStatus(doc.getAvailabilityStatus())
                .loanedCopies(doc.getLoanedCopies())
                .addedToCatalogDate(doc.getAddedToCatalogDate())
                .build();
    }
}