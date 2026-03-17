package edu.eci.dosw.tdd;



import edu.eci.dosw.tdd.core.exception.BookNoAvaliableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    @Test
    void loanBook_success_marksBookUnavailable_andCreatesActiveLoan() {
        // Arrange
        BookService bookService = new BookService();
        UserService userService = new UserService();
        LoanService loanService = new LoanService(bookService, userService);

        bookService.addBook(Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .available(true)
                .build());

        userService.registerUser(User.builder()
                .id("U1")
                .name("Diego")
                .build());


        Loan loan = loanService.loanBook("B1", "U1");

        assertNotNull(loan.getId());
        assertEquals("B1", loan.getBook().getId());
        assertEquals("U1", loan.getUser().getId());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertNotNull(loan.getLoanDate());
        assertNull(loan.getReturnDate());

        assertFalse(bookService.getBookById("B1").orElseThrow().isAvailable());
    }

    @Test
    void loanBook_error_whenBookNotAvailable_throwsBookNoAvaliableException() {
        BookService bookService = new BookService();
        UserService userService = new UserService();
        LoanService loanService = new LoanService(bookService, userService);

        bookService.addBook(Book.builder()
                .id("B2")
                .title("Refactoring")
                .author("Martin Fowler")
                .available(false)
                .build());

        userService.registerUser(User.builder()
                .id("U2")
                .name("Alejandro")
                .build());

        // Act + Assert
        assertThrows(BookNoAvaliableException.class,
                () -> loanService.loanBook("B2", "U2"));
    }

    @Test
    void returnBook_success_setsReturnedStatus_andMakesBookAvailableAgain() {
        BookService bookService = new BookService();
        UserService userService = new UserService();
        LoanService loanService = new LoanService(bookService, userService);

        bookService.addBook(Book.builder()
                .id("B3")
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .available(true)
                .build());

        userService.registerUser(User.builder()
                .id("U3")
                .name("Camilo")
                .build());

        loanService.loanBook("B3", "U3");
        assertFalse(bookService.getBookById("B3").orElseThrow().isAvailable());

        Loan returned = loanService.returnBook("B3", "U3");

        assertEquals(LoanStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());
        assertTrue(bookService.getBookById("B3").orElseThrow().isAvailable());
    }
}
