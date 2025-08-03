package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.BookDTO;
import com.PahanaEdu.exception.DuplicateResourceException;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.Book;
import com.PahanaEdu.model.BookCategory;
import com.PahanaEdu.model.Publisher;
import com.PahanaEdu.repository.BookCategoryRepository;
import com.PahanaEdu.repository.BookRepository;
import com.PahanaEdu.repository.PublisherRepository;
import com.PahanaEdu.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateResourceException("ISBN already exists"  );
        }

        BookCategory category = bookCategoryRepository.findByName(bookDTO.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Publisher publisher = publisherRepository.findByName(bookDTO.getPublisherName())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        Book book = new Book();
        book.setIsbn(bookDTO.getIsbn());
        book.setName(bookDTO.getName());
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setPages(bookDTO.getPages());
        book.setPrice(bookDTO.getPrice());
        book.setDescription(bookDTO.getDescription());
        book.setCover(bookDTO.getCover());
        book.setIsAvailable(bookDTO.getIsAvailable());

        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByCategory(String category) {
        BookCategory existingCategory = bookCategoryRepository.findByName(category)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return bookRepository.findByCategory_Name(existingCategory.getName()).stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByPublisher(String publisher) {
        Publisher existingPublisher = publisherRepository.findByName(publisher)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        return bookRepository.findByPublisher_Name(existingPublisher.getName()).stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateStock(Long id, Integer quantity) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (quantity < 0 && Math.abs(quantity) > existingBook.getStock()) {
            throw new IllegalArgumentException("Cannot reduce stock below 0");
        }

        existingBook.setStock(existingBook.getStock() - quantity);

        if (existingBook.getStock() <= 0) {
            existingBook.setIsAvailable(false);
        } else {
            existingBook.setIsAvailable(true);
        }

        Book savedBook = bookRepository.save(existingBook);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        BookCategory category = bookCategoryRepository.findByName(bookDTO.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Publisher publisher = publisherRepository.findByName(bookDTO.getPublisherName())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));

        if (!existingBook.getIsbn().equals(bookDTO.getIsbn()) &&
                bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateResourceException("ISBN already exists");
        }

        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setName(bookDTO.getName());
        existingBook.setCategory(category);
        existingBook.setPublisher(publisher);
        existingBook.setPages(bookDTO.getPages());
        existingBook.setStock(bookDTO.getStock());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setCover(bookDTO.getCover());
        existingBook.setIsAvailable(bookDTO.getIsAvailable());

        Book savedBook = bookRepository.save(existingBook);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public void deleteBook(Long id) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        bookRepository.delete(existingBook);
    }
}
