package com.PahanaEdu.service;

import com.PahanaEdu.dto.EmployeeOrderDTO;
import com.PahanaEdu.model.enums.ORDER_STATUS;

import java.util.List;

public interface EmployeeOrderService {
    EmployeeOrderDTO createEmployeeOrder(Long employeeId, Long customerId, EmployeeOrderDTO orderDTO);
    EmployeeOrderDTO getEmployeeOrderById(Long id);
    List<EmployeeOrderDTO> getAllEmployeeOrders();
    List<EmployeeOrderDTO> getOrdersByEmployee(String employeeAccNum);
    List<EmployeeOrderDTO> getOrdersByCustomer(String customerAccNum);
    List<EmployeeOrderDTO> getEmployeeOrdersByStatus(ORDER_STATUS status);
    EmployeeOrderDTO updateEmployeeOrderStatus(Long id, ORDER_STATUS orderStatus);
    EmployeeOrderDTO cancelEmployeeOrder(Long id, String currentUser, boolean isManager);
}