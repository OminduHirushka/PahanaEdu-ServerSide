package com.PahanaEdu.controller;

import com.PahanaEdu.dto.EmployeeOrderDTO;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.service.EmployeeOrderService;
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
@RequestMapping("/api/v1/employee-orders")
public class EmployeeOrderController {

    @Autowired
    private EmployeeOrderService employeeOrderService;

    @PostMapping("/create/{employeeId}/{customerId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createEmployeeOrder(
            @PathVariable Long employeeId,
            @PathVariable Long customerId,
            @Valid @RequestBody EmployeeOrderDTO orderDTO,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_EMPLOYEE")) {
            throw new AccessDeniedException("Only EMPLOYEE can create in-store orders");
        }

        EmployeeOrderDTO createdOrder = employeeOrderService.createEmployeeOrder(employeeId, customerId, orderDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "In-store order created successfully");
        response.put("order", createdOrder);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getEmployeeOrderById(@PathVariable Long id) {
        EmployeeOrderDTO order = employeeOrderService.getEmployeeOrderById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee order retrieved successfully");
        response.put("order", order);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getAllEmployeeOrders() {
        List<EmployeeOrderDTO> orders = employeeOrderService.getAllEmployeeOrders();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/employee/account/{accountNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getOrdersByEmployeeAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!roles.contains("ROLE_ADMIN") &&
                !roles.contains("ROLE_MANAGER") &&
                !roles.contains("ROLE_EMPLOYEE")) {
            throw new AccessDeniedException("Only EMPLOYEE, ADMIN and MANAGER can update stock");
        }

        List<EmployeeOrderDTO> orders = employeeOrderService.getOrdersByEmployee(accountNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customer/account/{accountNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOrdersByCustomerAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean isManagerOrAdmin = roles.contains("ROLE_MANAGER") || roles.contains("ROLE_ADMIN");
        String currentUser = authentication.getName();

        if (!isManagerOrAdmin && !currentUser.equals(accountNumber)) {
            throw new AccessDeniedException("You can only view your own customer orders");
        }

        List<EmployeeOrderDTO> orders = employeeOrderService.getOrdersByCustomer(accountNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer in-store orders retrieved successfully");
        response.put("orders", orders);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> updateEmployeeOrderStatus(
            @PathVariable Long id,
            @Valid @RequestParam ORDER_STATUS status) {
        EmployeeOrderDTO updatedOrder = employeeOrderService.updateEmployeeOrderStatus(id, status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee order status updated successfully");
        response.put("order", updatedOrder);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> cancelEmployeeOrder(
            @PathVariable Long id,
            Authentication authentication) {

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean isManager = roles.contains("ROLE_MANAGER");
        String currentUser = authentication.getName();

        employeeOrderService.cancelEmployeeOrder(id, currentUser, isManager);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee order cancelled successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}