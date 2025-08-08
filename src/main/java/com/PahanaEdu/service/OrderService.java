package com.PahanaEdu.service;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    OrderDTO createOrder(Long cartId, OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByCustomer(String accountNumber);
    List<OrderDTO> getOrdersByStatus(ORDER_STATUS status);
    OrderDTO updateOrderStatus(Long id, ORDER_STATUS orderStatus);
    OrderDTO cancelOrder(Long id, String currentUser, boolean isManager);
}
