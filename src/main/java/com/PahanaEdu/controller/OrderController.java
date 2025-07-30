package com.PahanaEdu.controller;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.service.OrderService;
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
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_EMPLOYEE") && !roles.contains("ROLE_CUSTOMER")) {
            throw new AccessDeniedException("Only EMPLOYEE and CUSTOMER can create orders");
        }

        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order created successfully");
        response.put("order", createdOrder);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") &&
                !roles.contains("ROLE_MANAGER") &&
                !roles.contains("ROLE_EMPLOYEE") &&
                !authentication.getName().equals(id)) {
            throw new AccessDeniedException("You can only view your own order");
        }

        OrderDTO order = orderService.getOrderById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order retrieved successfully");
        response.put("order", order);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getAllOrders(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") && !roles.contains("ROLE_MANAGER")) {
            throw new AccessDeniedException("Only ADMIN and MANAGER can view all orders");
        }

        List<OrderDTO> orders = orderService.getAllOrders();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOrdersByCustomer(
            @PathVariable Long customerId,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") &&
                !roles.contains("ROLE_MANAGER") &&
                !roles.contains("ROLE_EMPLOYEE") &&
                !authentication.getName().equals(customerId)) {
            throw new AccessDeniedException("You can only view your own orders");
        }

        List<OrderDTO> orders = orderService.getOrdersByCustomer(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestParam String status,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") &&
                !roles.contains("ROLE_MANAGER") &&
                !roles.contains("ROLE_EMPLOYEE") &&
                !authentication.getName().equals(id)) {
            throw new AccessDeniedException("You can only update your own order");
        }

        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order status updated successfully");
        response.put("order", updatedOrder);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}