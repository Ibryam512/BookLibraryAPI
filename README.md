# BookLibraryAPI

A RESTful API built with **Quarkus 3** for managing a book library. Demonstrates clean CRUD operations using the Repository Pattern, Result Pattern, Bean Validation, and automatic Swagger documentation.

---

## Features

- Full CRUD for books — create, read, update (full & partial), delete
- Filter books by author or genre via query parameters
- Lookup by ISBN
- Result Pattern via Java Sealed Interfaces — no thrown exceptions for business failures
- Swagger UI available in dev mode at `/q/swagger-ui`
- H2 in-memory database with auto-generated schema

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Quarkus 3.8.3 | Core framework |
| Hibernate ORM Panache | Database access (Repository Pattern) |
| H2 | In-memory database |
| RESTEasy Reactive + Jackson | REST layer and JSON serialization |
| Hibernate Validator | Bean Validation |
| SmallRye OpenAPI | Swagger UI |
| Java 17 | Language (uses Records and Sealed Interfaces) |

---

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/books` | List all books |
| `GET` | `/books?author={author}` | Filter books by author |
| `GET` | `/books?genre={genre}` | Filter books by genre |
| `GET` | `/books/{id}` | Get a book by ID |
| `GET` | `/books/isbn/{isbn}` | Get a book by ISBN |
| `POST` | `/books` | Create a new book |
| `PUT` | `/books/{id}` | Fully update a book |
| `PATCH` | `/books/{id}` | Partially update a book |
| `DELETE` | `/books/{id}` | Delete a book |

---

## Prerequisites

- Java 17+
- Maven 3.9+

No database setup required — H2 runs in-memory automatically.

---

## Running the project

**Clone the repository**
```bash
git clone https://github.com/Ibryam512/BookLibraryAPI.git
cd BookLibraryAPI
```

**Start in dev mode**
```bash
./mvnw quarkus:dev
```

The API will be available at `http://localhost:8080/books`.

Swagger UI will be available at `http://localhost:8080/q/swagger-ui`.

