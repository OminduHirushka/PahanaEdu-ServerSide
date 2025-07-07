package com.PahanaEdu.repository;

import com.PahanaEdu.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategory_Name(String category);
    List<Book> findByPublisher_Name(String publisher);
    Optional<Book> findByName(String name);
    boolean existsByIsbn(String isbn);
}
