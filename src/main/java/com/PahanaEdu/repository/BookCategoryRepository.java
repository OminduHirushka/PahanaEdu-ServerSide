package com.PahanaEdu.repository;

import com.PahanaEdu.model.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    Optional<BookCategory> findByName(String name);
}
