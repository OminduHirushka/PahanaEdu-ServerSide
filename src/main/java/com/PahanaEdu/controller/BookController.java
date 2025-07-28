package com.PahanaEdu.controller;

import com.PahanaEdu.dto.BookDTO;
import com.PahanaEdu.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/create-book")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book created successfully");
        response.put("createdBook", createdBook);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllBooks() {
        List<BookDTO> allBooks = bookService.getAllBooks();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All books found");
        response.put("books", allBooks);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getAllBooksByCategory(@PathVariable String category) {
        List<BookDTO> books = bookService.getBooksByCategory(category);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All books found");
        response.put("books", books);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<Map<String, Object>> getAllBooksByPublisher(@PathVariable String publisher) {
        List<BookDTO> books = bookService.getBooksByPublisher(publisher);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All books found");
        response.put("books", books);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getBookByName(@PathVariable String name) {
        BookDTO bookDTO = bookService.getBookByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book found");
        response.put("book", bookDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/update-stock/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        BookDTO updatedBook = bookService.updateStock(id, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Stock updated successfully");
        response.put("updatedBook", updatedBook);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-book/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book updated successfully");
        response.put("updatedBook", updatedBook);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-book/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
