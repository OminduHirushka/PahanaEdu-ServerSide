package com.PahanaEdu.model;

import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.model.enums.PAYMENT_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee_orders")
public class EmployeeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "employeeOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeOrderItem> items;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Double subTotal;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PAYMENT_STATUS paymentStatus = PAYMENT_STATUS.PAID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ORDER_STATUS orderStatus = ORDER_STATUS.COMPLETED;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}