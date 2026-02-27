package com.example;

import com.example.common.Result;
import com.example.entity.Book;
import com.example.repository.BookRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book library CRUD operations")
public class BookResource {
    @Inject
    BookRepository bookRepository;

    @GET
    @Operation(summary = "List all books", description = "Supports optional filtering by author or genre")
    public Response listAll(
            @QueryParam("author") String author,
            @QueryParam("genre") String genre) {

        List<Book> books;

        if (author != null && !author.isBlank()) {
            books = bookRepository.findByAuthor(author);
        } else if (genre != null && !genre.isBlank()) {
            books = bookRepository.findByGenre(genre);
        } else {
            books = bookRepository.listAll();
        }

        return toResponse(Result.ok(books));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a book by ID")
    public Response getById(@PathParam("id") Long id) {
        Result<Book> result = bookRepository.findByIdOptional(id)
                .map(Result::ok)
                .orElse(Result.fail(Collections.singletonList("Book with id " + id + " not found"), 404));

        return toResponse(result);
    }

    @GET
    @Path("/isbn/{isbn}")
    @Operation(summary = "Get a book by ISBN")
    public Response getByIsbn(@PathParam("isbn") String isbn) {
        Result<Book> result = bookRepository.findByIsbn(isbn)
                .map(Result::ok)
                .orElse(Result.fail(Collections.singletonList("Book with ISBN " + isbn + " not found"), 404));

        return toResponse(result);
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new book")
    public Response create(@Valid Book book) {
        if (bookRepository.isbnExists(book.getIsbn())) {
            return toResponse(Result.fail(Collections.singletonList("ISBN " + book.getIsbn() + " already exists"), 409));
        }

        bookRepository.persist(book);

        return toResponse(Result.ok(book), book.getId());
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Fully update a book")
    public Response update(@PathParam("id") Long id, @Valid Book updated) {
        Result<Book> result = bookRepository.findByIdOptional(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setAuthor(updated.getAuthor());
                    existing.setIsbn(updated.getIsbn());
                    existing.setPublicationYear(updated.getPublicationYear());
                    existing.setGenre(updated.getGenre());
                    return Result.ok(existing);
                })
                .orElse(Result.fail(Collections.singletonList("Book with id " + id + " not found"), 404));

        return toResponse(result);
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Partially update a book")
    public Response patch(@PathParam("id") Long id, Book partial) {
        Result<Book> result = bookRepository.findByIdOptional(id)
                .map(existing -> {
                    if (partial.getTitle()  != null) existing.setTitle(partial.getTitle());
                    if (partial.getAuthor() != null) existing.setAuthor(partial.getAuthor());
                    if (partial.getIsbn()   != null) existing.setIsbn(partial.getIsbn());
                    if (partial.getPublicationYear() != 0) existing.setPublicationYear(partial.getPublicationYear());
                    if (partial.getGenre()  != null) existing.setGenre(partial.getGenre());
                    return Result.ok(existing);
                })
                .orElse(Result.fail(Collections.singletonList("Book with id " + id + " not found"), 404));

        return toResponse(result);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a book")
    public Response delete(@PathParam("id") Long id) {
        Result<Void> result = bookRepository.findByIdOptional(id)
                .map(existing -> {
                    bookRepository.delete(existing);
                    return Result.<Void>ok(null);
                })
                .orElse(Result.fail(Collections.singletonList("Book with id " + id + " not found"), 404));

        return switch (result) {
            case Result.Success<Void> ignored -> Response.noContent().build();
            case Result.Failure<Void> f       -> Response.status(f.statusCode())
                    .entity(new ErrorMessage(f.errors()))
                    .build();
        };
    }

    private <T> Response toResponse(Result<T> result) {
        return switch (result) {
            case Result.Success<T> s -> Response.ok(s.value()).build();
            case Result.Failure<T> f -> Response.status(f.statusCode())
                    .entity(new ErrorMessage(f.errors()))
                    .build();
        };
    }

    private <T> Response toResponse(Result<T> result, Long createdId) {
        return switch (result) {
            case Result.Success<T> s -> Response
                    .created(UriBuilder.fromResource(BookResource.class)
                            .path(String.valueOf(createdId)).build())
                    .entity(s.value())
                    .build();
            case Result.Failure<T> f -> Response.status(f.statusCode())
                    .entity(new ErrorMessage(f.errors()))
                    .build();
        };
    }

    public record ErrorMessage(List<String> errors) {}
}
