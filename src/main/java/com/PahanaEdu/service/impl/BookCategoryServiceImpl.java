package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.BookCategoryDTO;
import com.PahanaEdu.exception.DuplicateResourceException;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.BookCategory;
import com.PahanaEdu.repository.BookCategoryRepository;
import com.PahanaEdu.service.BookCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookCategoryServiceImpl implements BookCategoryService {
    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookCategoryDTO createBookCategory(BookCategoryDTO bookCategoryDTO) {
        if (bookCategoryRepository.findByName(bookCategoryDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Category already exists");
        }

        BookCategory bookCategory = new BookCategory();
        bookCategory.setName(bookCategoryDTO.getName());

        BookCategory savedBookCategory = bookCategoryRepository.save(bookCategory);
        return modelMapper.map(savedBookCategory, BookCategoryDTO.class);
    }

    @Override
    public List<BookCategoryDTO> getAllBookCategories() {
        return bookCategoryRepository.findAll().stream()
                .map(bookCategory -> modelMapper.map(bookCategory, BookCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookCategoryDTO getBookCategoryByName(String name) {
        BookCategory bookCategory = bookCategoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return modelMapper.map(bookCategory, BookCategoryDTO.class);
    }

    @Override
    public BookCategoryDTO updateBookCategory(Long id, BookCategoryDTO bookCategoryDTO) {
        BookCategory existingCategory = bookCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!existingCategory.getName().equals(bookCategoryDTO.getName())
                && bookCategoryRepository.findByName(bookCategoryDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Category already exists");
        }

        existingCategory.setName(bookCategoryDTO.getName());

        BookCategory updatedCategory = bookCategoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, BookCategoryDTO.class);
    }

    @Override
    public void deleteBookCategory(Long id) {
        BookCategory existingCategory = bookCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        bookCategoryRepository.delete(existingCategory);
    }
}
