package com.PahanaEdu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long id;
    private Long cartId;
    private Long bookId;
    private Double price;
    private Integer quantity;
    private Double totalPrice;

}