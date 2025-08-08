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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Column(nullable = false)
    private String address;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Double subTotal;

    @Column(nullable = false)
    private Double shippingFee;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PAYMENT_STATUS paymentStatus = PAYMENT_STATUS.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ORDER_STATUS orderStatus = ORDER_STATUS.PENDING;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
