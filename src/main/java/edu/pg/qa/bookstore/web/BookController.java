package edu.pg.qa.bookstore.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.pg.qa.bookstore.domain.Book;
import edu.pg.qa.bookstore.dto.*;
import edu.pg.qa.bookstore.service.BookService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> getBooks(
            @RequestParam(name = "author", required = false) String author) {

        List<Book> books = service.getAllBooks(author);
        return books.stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable("id") Long id) {
        Book book = service.getBookById(id);
        return toResponse(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        Book created = service.createBook(request);
        BookResponse response = toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/books/" + created.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable("id") Long id,
                                   @Valid @RequestBody UpdateBookRequest request) {
        Book updated = service.updateBook(id, request);
        return toResponse(updated);
    }

    @PatchMapping("/{id}/price")
    public BookResponse updateBookPrice(@PathVariable("id") Long id,
                                        @Valid @RequestBody UpdateBookPriceRequest request) {
        Book updated = service.updateBookPrice(id, request);
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        service.deleteBook(id);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getIsbn(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}