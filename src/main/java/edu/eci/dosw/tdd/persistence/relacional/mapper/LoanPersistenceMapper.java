package edu.eci.dosw.tdd.persistence.relacional.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.persistence.relacional.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relacional.entity.LoanStatusEntity;
import edu.eci.dosw.tdd.persistence.relacional.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relacional.entity.UserEntity;

public final class LoanPersistenceMapper {

    private LoanPersistenceMapper() {}

    public static Loan toDomain(LoanEntity e) {
        if (e == null) return null;
        return Loan.builder()
                .id(e.getId())
                .book(BookPersistenceMapper.toDomain(e.getBook()))
                .user(UserPersistenceMapper.toDomain(e.getUser()))
                .loanDate(e.getLoanDate())
                .status(LoanStatus.valueOf(e.getStatus().name()))
                .returnDate(e.getReturnDate())
                .build();
    }

    public static LoanEntity toEntity(Loan l) {
        if (l == null) return null;

        BookEntity bookEntity = null;
        if (l.getBook() != null) {
            bookEntity = BookEntity.builder()
                    .id(l.getBook().getId())
                    .title(l.getBook().getTitle())
                    .author(l.getBook().getAuthor())
                    .totalCopies(l.getBook().getTotalCopies())
                    .availableCopies(l.getBook().getAvailableCopies())
                    .build();
        }

        UserEntity userEntity = null;
        if (l.getUser() != null) {
            userEntity = UserPersistenceMapper.toEntity(l.getUser());
        }

        return LoanEntity.builder()
                .id(l.getId())
                .book(bookEntity)
                .user(userEntity)
                .loanDate(l.getLoanDate())
                .status(LoanStatusEntity.valueOf(l.getStatus().name()))
                .returnDate(l.getReturnDate())
                .build();
    }
}