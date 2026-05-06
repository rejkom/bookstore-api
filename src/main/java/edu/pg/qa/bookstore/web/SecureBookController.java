package edu.pg.qa.bookstore.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import edu.pg.qa.bookstore.dto.AuthRequest;
import edu.pg.qa.bookstore.dto.AuthResponse;
import edu.pg.qa.bookstore.dto.BookResponse;
import edu.pg.qa.bookstore.service.BookService;
import edu.pg.qa.bookstore.service.TokenService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SecureBookController {

    private final TokenService tokenService;
    private final BookService bookService;

    public SecureBookController(TokenService tokenService, BookService bookService) {
        this.tokenService = tokenService;
        this.bookService = bookService;
    }

    @PostMapping("/auth/token")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse issueToken(@Valid @RequestBody AuthRequest request) {
        // Prosty, dydaktyczny mechanizm:
        // tylko username=student, password=student są akceptowane
        if (!"student".equals(request.getUsername()) ||
                !"student".equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = tokenService.issueTokenForUser(request.getUsername());
        return new AuthResponse(token, tokenService.getTokenTtlSeconds());
    }

    @GetMapping("/secure/books")
    public List<BookResponse> getSecureBooks(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        if (!tokenService.isTokenValid(token)) {
            throw new SecurityException("Invalid or expired token");
        }

        return bookService.getAllBooks(null).stream()
                .map(book -> new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPrice(),
                        book.getIsbn(),
                        book.getCreatedAt(),
                        book.getUpdatedAt()
                )).toList();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }
        return authorizationHeader.substring("Bearer ".length());
    }
}