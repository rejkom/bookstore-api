package edu.pg.qa.bookstore.service;

import org.springframework.stereotype.Service;
import edu.pg.qa.bookstore.domain.Book;
import edu.pg.qa.bookstore.dto.CreateBookRequest;
import edu.pg.qa.bookstore.dto.UpdateBookPriceRequest;
import edu.pg.qa.bookstore.dto.UpdateBookRequest;
import edu.pg.qa.bookstore.repository.InMemoryBookRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {

    private final InMemoryBookRepository repository;

    public BookService() {
        this.repository = new InMemoryBookRepository();
    }

    public List<Book> getAllBooks(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }
        return repository.findByAuthor(author);
    }

    public Book getBookById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + id));
    }

    public Book createBook(CreateBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setIsbn(request.getIsbn());
        return repository.save(book);
    }

    public Book updateBook(Long id, UpdateBookRequest request) {
        Book existing = getBookById(id);
        existing.setTitle(request.getTitle());
        existing.setAuthor(request.getAuthor());
        existing.setPrice(request.getPrice());
        existing.setIsbn(request.getIsbn());
        return repository.save(existing);
    }

    public Book updateBookPrice(Long id, UpdateBookPriceRequest request) {
        Book existing = getBookById(id);
        existing.setPrice(request.getPrice());
        return repository.save(existing);
    }

    public void deleteBook(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Book not found: " + id);
        }
        repository.deleteById(id);
    }
}