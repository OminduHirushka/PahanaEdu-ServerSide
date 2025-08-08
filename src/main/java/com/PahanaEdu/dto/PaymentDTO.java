package com.PahanaEdu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String paymentMethod;
    private String transactionId;
    private Double totalPrice;
    private LocalDateTime paymentDate;
    private String status;
}