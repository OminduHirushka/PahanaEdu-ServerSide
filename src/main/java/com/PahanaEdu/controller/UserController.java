package com.PahanaEdu.controller;

import com.PahanaEdu.dto.UserDTO;
import com.PahanaEdu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User created successfully");
        response.put("createdUser", createdUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserDTO> allUsers = userService.getAllUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All users found");
        response.put("users", allUsers);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/accNum/{accNum}")
    public ResponseEntity<Map<String, Object>> getUserByAccountNumber(@PathVariable String accNum) {
        UserDTO userDTO = userService.getUserByAccountNumber(accNum);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User found");
        response.put("user", userDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-user/{accNum}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String accNum, @Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.updateUser(accNum, userDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully");
        response.put("updatedUser", createdUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-user/{accNum}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String accNum) {
        userService.deleteUser(accNum);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
