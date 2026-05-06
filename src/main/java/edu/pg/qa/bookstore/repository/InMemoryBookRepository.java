package edu.pg.qa.bookstore.repository;


import edu.pg.qa.bookstore.domain.Book;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryBookRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    public InMemoryBookRepository() {
        seedData();
    }

    private void seedData() {
        save(new Book(null, "Clean Code", "Robert C. Martin",
                new BigDecimal("129.90"), "978-0132350884",
                Instant.now(), Instant.now()));
        save(new Book(null, "Effective Java", "Joshua Bloch",
                new BigDecimal("159.90"), "978-0134685991",
                Instant.now(), Instant.now()));
        save(new Book(null, "Java Concurrency in Practice", "Brian Goetz",
                new BigDecimal("189.90"), "978-0321349606",
                Instant.now(), Instant.now()));
    }

    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Book> findByAuthor(String author) {
        return storage.values().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            long id = idSequence.incrementAndGet();
            book.setId(id);
            book.setCreatedAt(Instant.now());
        }
        book.setUpdatedAt(Instant.now());
        storage.put(book.getId(), book);
        return book;
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}