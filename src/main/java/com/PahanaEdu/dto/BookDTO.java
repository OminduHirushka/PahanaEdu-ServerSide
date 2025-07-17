package com.PahanaEdu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotNull(message = "ISBN is required")
    private String isbn;

    @NotNull(message = "Book name is required")
    private String name;

    private String categoryId;

    @NotNull(message = "Category is required")
    private String categoryName;

    private String publisher;

    @NotNull(message = "Publisher is required")
    private String publisherName;

    private int pages;

    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "Stock is required")
    private Integer stock = 1;

    @NotNull(message = "Status is required")
    private Boolean isAvailable = true;

}
