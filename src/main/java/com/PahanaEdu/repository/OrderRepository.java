package com.PahanaEdu.repository;

import com.PahanaEdu.model.Order;
import com.PahanaEdu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Collection<Object> findByCustomer(User user);

}