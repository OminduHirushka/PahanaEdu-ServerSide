package com.PahanaEdu.controller;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.model.enums.ORDER_STATUS;
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

    @PostMapping("/create-order/{cartId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> createOrder(
            @PathVariable Long cartId,
            @Valid @RequestBody OrderDTO orderDTO,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_EMPLOYEE") && !roles.contains("ROLE_CUSTOMER")) {
            throw new AccessDeniedException("Only EMPLOYEE and CUSTOMER can create orders");
        }

        OrderDTO createdOrder = orderService.createOrder(cartId, orderDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order created successfully");
        response.put("order", createdOrder);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
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

    @GetMapping("/customer/{accountNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOrdersByCustomer(@PathVariable String accountNumber) {
        List<OrderDTO> orders = orderService.getOrdersByCustomer(accountNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/update-order/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestParam ORDER_STATUS status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order status updated successfully");
        response.put("order", updatedOrder);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/cancel-order/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean isManager = roles.contains("ROLE_MANAGER");
        String currentUser = authentication.getName();

        orderService.cancelOrder(id, currentUser, isManager);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order cancelled successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
