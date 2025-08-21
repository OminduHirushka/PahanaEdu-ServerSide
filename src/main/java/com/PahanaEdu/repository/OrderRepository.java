package com.PahanaEdu.repository;

import com.PahanaEdu.model.Order;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.model.enums.PAYMENT_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUser_Id(long id);
    List<Order> findByOrderStatus(ORDER_STATUS orderStatus);
    List<Order> findByPaymentStatus(PAYMENT_STATUS paymentStatus);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.book b " +
            "LEFT JOIN FETCH b.publisher p " +
            "WHERE o.user.id = :userId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findOrdersWithItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.book b " +
            "LEFT JOIN FETCH b.publisher p " +
            "WHERE o.id = :orderId")
    Optional<Order> findOrderWithItemsById(@Param("orderId") Long orderId);
}
