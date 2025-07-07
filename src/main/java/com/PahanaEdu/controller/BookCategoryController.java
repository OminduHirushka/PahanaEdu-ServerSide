package com.PahanaEdu.controller;

import com.PahanaEdu.dto.BookCategoryDTO;
import com.PahanaEdu.model.BookCategory;
import com.PahanaEdu.service.BookCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
public class BookCategoryController {
    @Autowired
    private BookCategoryService bookCategoryService;

    @PostMapping("/create-category")
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody BookCategoryDTO bookCategoryDTO) {
        BookCategoryDTO createdCategory = bookCategoryService.createBookCategory(bookCategoryDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category created successfully");
        response.put("createdCategory", createdCategory);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllCategory() {
        List<BookCategoryDTO> allCategories = bookCategoryService.getAllBookCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All categories found");
        response.put("allCategories", allCategories);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getCategoryByName(@PathVariable String name) {
        BookCategoryDTO category = bookCategoryService.getBookCategoryByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category found");
        response.put("category", category);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-category/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id, @Valid @RequestBody BookCategoryDTO bookCategoryDTO) {
        BookCategoryDTO createdCategory = bookCategoryService.updateBookCategory(id, bookCategoryDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category updated successfully");
        response.put("updatedCategory", createdCategory);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-category/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        bookCategoryService.deleteBookCategory(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
