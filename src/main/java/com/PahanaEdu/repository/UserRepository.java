package com.PahanaEdu.repository;

import com.PahanaEdu.model.User;
import com.PahanaEdu.model.enums.USER_ROLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);
    boolean existsByNic(String nic);
    boolean existsByContact(String contact);

    @Query("SELECT u.accountNumber FROM User u WHERE u.role = :role ORDER BY u.accountNumber DESC LIMIT 1")
    Optional<String> findLastAccountNumberByRole(@Param("role") USER_ROLE role);
}
