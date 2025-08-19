package com.PahanaEdu.repository;

import com.PahanaEdu.model.EmployeeOrder;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeOrderRepository extends JpaRepository<EmployeeOrder, Long> {
    List<EmployeeOrder> findByEmployee_Id(Long employeeId);
    List<EmployeeOrder> findByCustomer_Id(Long customerId);
    List<EmployeeOrder> findByOrderStatus(ORDER_STATUS orderStatus);

    @Query("SELECT DISTINCT eo FROM EmployeeOrder eo " +
            "LEFT JOIN FETCH eo.items i " +
            "LEFT JOIN FETCH i.book b " +
            "LEFT JOIN FETCH b.publisher p " +
            "WHERE eo.employee.accountNumber = :accountNumber " +
            "ORDER BY eo.createdAt DESC")
    List<EmployeeOrder> findEmployeeOrdersWithItemsByEmployeeAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT DISTINCT eo FROM EmployeeOrder eo " +
            "LEFT JOIN FETCH eo.items i " +
            "LEFT JOIN FETCH i.book b " +
            "LEFT JOIN FETCH b.publisher p " +
            "WHERE eo.customer.accountNumber = :accountNumber " +
            "ORDER BY eo.createdAt DESC")
    List<EmployeeOrder> findEmployeeOrdersWithItemsByCustomerAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT DISTINCT eo FROM EmployeeOrder eo " +
            "LEFT JOIN FETCH eo.items i " +
            "LEFT JOIN FETCH i.book b " +
            "LEFT JOIN FETCH b.publisher p " +
            "WHERE eo.id = :orderId")
    Optional<EmployeeOrder> findEmployeeOrderWithItemsById(@Param("orderId") Long orderId);
}
