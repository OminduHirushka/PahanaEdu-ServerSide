package com.PahanaEdu.dto.auth;

import com.PahanaEdu.model.enums.USER_ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private USER_ROLE role;
    private String message;

}
