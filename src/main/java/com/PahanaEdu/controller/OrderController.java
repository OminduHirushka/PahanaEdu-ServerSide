package com.PahanaEdu.controller;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order created successfully");
        response.put("order", createdOrder);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order retrieved successfully");
        response.put("order", order);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderDTO> orders = orderService.getOrdersByCustomer(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestParam String status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order status updated successfully");
        response.put("order", updatedOrder);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}