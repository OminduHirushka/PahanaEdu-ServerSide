package com.PahanaEdu.repository;

import com.PahanaEdu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);
    boolean existsByNic(String nic);
    boolean existsByContact(String contact);
}
