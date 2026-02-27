package com.example.repository;

import com.example.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
    public Optional<Book> findByIsbn(String isbn) {
        return find("isbn", isbn).firstResultOptional();
    }

    public List<Book> findByAuthor(String author) {
        return list("author", author);
    }

    public List<Book> findByGenre(String genre) {
        return list("genre", genre);
    }

    public boolean isbnExists(String isbn) {
        return find("isbn", isbn).count() > 0;
    }
}
