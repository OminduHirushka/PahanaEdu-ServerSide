package com.PahanaEdu.repository;

import com.PahanaEdu.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByCode(String code);
    Optional<Publisher> findByName(String name);
}
