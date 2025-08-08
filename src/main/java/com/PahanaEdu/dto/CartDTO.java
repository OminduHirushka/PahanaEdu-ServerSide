package com.PahanaEdu.dto;

import com.PahanaEdu.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long id;
    private Long userId;
    private String userAccountNumber;
    private List<CartItem> items;
    private Double totalPrice;
    private boolean checkedOut;
    private LocalDateTime createdAt;

}