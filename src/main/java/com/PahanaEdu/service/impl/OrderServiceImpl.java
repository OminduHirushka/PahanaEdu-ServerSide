package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.OrderDTO;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.*;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.model.enums.PAYMENT_STATUS;
import com.PahanaEdu.repository.*;
import com.PahanaEdu.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final double SHIPPING_FEE = 250.00;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderDTO createOrder(Long cartId, OrderDTO orderDTO) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));

        User customer = userRepository.findById(cart.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        Order order = new Order();
        order.setOrderNumber(orderDTO.getOrderNumber());
        order.setUser(customer);
        order.setAddress(customer.getAddress());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(ORDER_STATUS.PENDING);
        order.setPaymentStatus(PAYMENT_STATUS.PENDING);

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();

                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getBook().getPrice());
                    orderItem.setTotalPrice(calculateItemTotal(cartItem));
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());

        double subtotal = calculateSubtotal(orderItems);
        double shippingFee = calculateShippingFee();
        double totalAmount = calculateTotalAmount(subtotal, shippingFee);

        order.setSubTotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        clearCart(cart);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findOrderWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByCustomer(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with account number: " + accountNumber));

        List<Order> orders = orderRepository.findOrdersWithItemsByUserId(user.getId());

        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(ORDER_STATUS status) {
        return orderRepository.findByOrderStatus(status).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, ORDER_STATUS orderStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setOrderStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());

        if (orderStatus == ORDER_STATUS.COMPLETED) {
            order.setPaymentStatus(PAYMENT_STATUS.PAID);
        }

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public OrderDTO cancelOrder(Long id, String currentUser, boolean isManager) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!isManager) {
            if (!order.getUser().getAccountNumber().equals(currentUser) &&
                    !order.getUser().getEmail().equals(currentUser)) {
                throw new AccessDeniedException("You can only cancel your own orders");
            }
        }

        if (order.getOrderStatus() != ORDER_STATUS.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled");
        }

        returnBooksToStock(order.getItems());

        order.setOrderStatus(ORDER_STATUS.CANCELLED);
        order.setPaymentStatus(PAYMENT_STATUS.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    private double calculateItemTotal(CartItem cartItem) {
        return cartItem.getBook().getPrice() * cartItem.getQuantity();
    }

    private double calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    private double calculateShippingFee() {
        return SHIPPING_FEE;
    }

    private double calculateTotalAmount(double subtotal, double shippingFee) {
        return subtotal + shippingFee;
    }

    private void returnBooksToStock(List<OrderItem> items) {
        items.forEach(item -> {
            Book book = item.getBook();
            book.setStock(book.getStock() + item.getQuantity());
            bookRepository.save(book);
        });
    }

    private void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }
}
