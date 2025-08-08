package com.PahanaEdu.dto;

import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.model.enums.PAYMENT_STATUS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private Long userId;
    private String userAccountNumber;
    private String userName;
    private List<OrderItemDTO> items;
    private String address;
    private LocalDateTime createdAt;
    private Double subtotal;
    private Double shippingFee;
    private Double totalAmount;
    private PAYMENT_STATUS paymentStatus;
    private ORDER_STATUS orderStatus;
    private LocalDateTime updatedAt;

}
