package com.PahanaEdu.service;

import com.PahanaEdu.dto.BookDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    List<BookDTO> getAllBooks();
    List<BookDTO> getBooksByCategory(String category);
    List<BookDTO> getBooksByPublisher(String publisher);
    BookDTO getBookByName(String name);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
}
