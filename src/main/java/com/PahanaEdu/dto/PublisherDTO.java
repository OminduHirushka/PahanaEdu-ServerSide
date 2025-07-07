package com.PahanaEdu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherDTO {

    private Long id;

    @NotBlank(message = "Publisher code is required")
    private String code;

    @NotBlank(message = "Publisher name is required")
    private String name;

}
