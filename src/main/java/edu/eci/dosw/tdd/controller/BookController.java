package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookMapper.toDTO(bookService.addBook(BookMapper.toModel(dto))));
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks().stream()
                .map(BookMapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookDTO>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks().stream()
                .map(BookMapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(b -> ResponseEntity.ok(BookMapper.toDTO(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable String id,
                                              @Valid @RequestBody BookDTO dto) {
        return ResponseEntity.ok(
                BookMapper.toDTO(bookService.updateBook(id, BookMapper.toModel(dto))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}