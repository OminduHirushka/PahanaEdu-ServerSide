package com.PahanaEdu.service;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.model.enums.PAYMENT_STATUS;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    OrderDTO createOrder(Long cartId, OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByCustomer(String accountNumber);
    List<OrderDTO> getOrdersByStatus(ORDER_STATUS status);
    List<OrderDTO> getOrdersByPaymentStatus(PAYMENT_STATUS paymentStatus);
    OrderDTO updateOrderStatus(Long id, ORDER_STATUS orderStatus);
    OrderDTO updatePaymentStatus(Long id, PAYMENT_STATUS paymentStatus);
    OrderDTO cancelOrder(Long id, String currentUser, boolean isManager);
}
