package com.PahanaEdu.dto;

import com.PahanaEdu.model.enums.ORDER_STATUS;
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
    private Long customerId;
    private LocalDateTime orderDate;
    private ORDER_STATUS status;
    private List<OrderItemDTO> items;
    private Double totalAmount;

}
