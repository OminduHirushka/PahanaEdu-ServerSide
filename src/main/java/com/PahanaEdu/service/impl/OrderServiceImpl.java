package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.*;
import com.PahanaEdu.exception.*;
import com.PahanaEdu.model.*;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.repository.*;
import com.PahanaEdu.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Order order = new Order();
        order.setCustomer(user);
        order.setStatus(ORDER_STATUS.PENDING);

        double totalAmount = 0;

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found "));

            if (book.getStock() < itemDTO.getQuantity()) {
                throw new ResourceNotAvailableException("Insufficient stock");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(book.getPrice());
            orderItem.setTotalPrice(book.getPrice() * itemDTO.getQuantity());

            totalAmount += orderItem.getTotalPrice();

            book.setStock(book.getStock() - itemDTO.getQuantity());
            if (book.getStock() == 0) {
                book.setIsAvailable(false);
            }
            bookRepository.save(book);

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return orderRepository.findByCustomer(user).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try {
            ORDER_STATUS newStatus = ORDER_STATUS.valueOf(status.toUpperCase());

            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid order status");
        }
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        orderRepository.delete(order);
    }
}