package com.PahanaEdu.service;

import com.PahanaEdu.dto.BookCategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookCategoryService {
    BookCategoryDTO createBookCategory(BookCategoryDTO bookCategoryDTO);
    List<BookCategoryDTO> getAllBookCategories();
    BookCategoryDTO getBookCategoryByName(String name);
    BookCategoryDTO updateBookCategory(Long id, BookCategoryDTO bookCategoryDTO);
    void deleteBookCategory(Long id);
}
