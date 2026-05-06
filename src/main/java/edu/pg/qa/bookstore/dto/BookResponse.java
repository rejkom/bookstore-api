package edu.pg.qa.bookstore.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BookResponse(Long id, String title, String author, BigDecimal price, String isbn, Instant createdAt,
                           Instant updatedAt) {
}