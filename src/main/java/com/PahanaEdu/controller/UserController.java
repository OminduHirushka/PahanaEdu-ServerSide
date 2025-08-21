package com.PahanaEdu.controller;

import com.PahanaEdu.dto.UserDTO;
import com.PahanaEdu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getAllUsers(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") &&
                !roles.contains("ROLE_MANAGER") &&
                !roles.contains("ROLE_EMPLOYEE")) {
            throw new AccessDeniedException("Only EMPLOYEE, ADMIN and MANAGER can view all users");
        }

        List<UserDTO> allUsers = userService.getAllUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All users found");
        response.put("users", allUsers);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/accNum/{accNum}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserByAccountNumber(
            @PathVariable String accNum,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") &&
                !roles.contains("ROLE_MANAGER") &&
                !roles.contains("ROLE_EMPLOYEE") &&
                !authentication.getName().equals(accNum)) {
            throw new AccessDeniedException("You can only view your own profile");
        }

        UserDTO userDTO = userService.getUserByAccountNumber(accNum);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User found");
        response.put("user", userDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-user/{accNum}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String accNum,
            @Valid @RequestBody UserDTO userDTO) {

        UserDTO updatedUser = userService.updateUser(accNum, userDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully");
        response.put("updatedUser", updatedUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-user/{accNum}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable String accNum,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.contains("ROLE_EMPLOYEE") &&
                (roles.contains("ROLE_ADMIN") ||
                        roles.contains("ROLE_MANAGER") ||
                        roles.contains("ROLE_EMPLOYEE"))) {
            throw new AccessDeniedException("You can't delete your account");
        }

        userService.deleteUser(accNum);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserProfileFromToken(
            @RequestHeader("Authorization") String token,
            Authentication authentication) {

        UserDTO userDTO = userService.findUserByToken(token);

        if (!userDTO.getAccountNumber().equals(authentication.getName())) {
            throw new AccessDeniedException("Token doesn't match authenticated user");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User found");
        response.put("user", userDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
