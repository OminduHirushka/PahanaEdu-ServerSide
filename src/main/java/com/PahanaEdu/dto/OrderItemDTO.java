package com.PahanaEdu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;
    private Long bookId;
    private String bookName;
    private String bookCover;
    private Double unitPrice;
    private Integer quantity;
    private Double totalPrice;

}
