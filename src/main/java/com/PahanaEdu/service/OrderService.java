package com.PahanaEdu.service;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.dto.OrderItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByCustomer(Long customerId);
    OrderDTO updateOrderStatus(Long id, String status);
    void cancelOrder(Long id);
}