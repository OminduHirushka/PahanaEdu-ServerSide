package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.EmployeeOrderDTO;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.*;
import com.PahanaEdu.model.enums.ORDER_STATUS;
import com.PahanaEdu.model.enums.PAYMENT_STATUS;
import com.PahanaEdu.repository.*;
import com.PahanaEdu.service.EmployeeOrderService;
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
public class EmployeeOrderServiceImpl implements EmployeeOrderService {

    @Autowired
    private EmployeeOrderRepository employeeOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public EmployeeOrderDTO createEmployeeOrder(Long employeeId, Long customerId, EmployeeOrderDTO orderDTO) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        EmployeeOrder order = new EmployeeOrder();
        order.setOrderNumber(orderDTO.getOrderNumber());
        order.setEmployee(employee);
        order.setCustomer(customer);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(ORDER_STATUS.COMPLETED);
        order.setPaymentStatus(PAYMENT_STATUS.PAID);

        List<EmployeeOrderItem> orderItems = orderDTO.getItems().stream()
                .map(itemDTO -> {
                    Book book = bookRepository.findById(itemDTO.getBookId())
                            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + itemDTO.getBookId()));

                    if (book.getStock() < itemDTO.getQuantity()) {
                        throw new IllegalStateException("Insufficient stock for book: " + book.getName());
                    }

                    book.setStock(book.getStock() - itemDTO.getQuantity());
                    bookRepository.save(book);

                    EmployeeOrderItem orderItem = new EmployeeOrderItem();
                    orderItem.setEmployeeOrder(order);
                    orderItem.setBook(book);
                    orderItem.setQuantity(itemDTO.getQuantity());
                    orderItem.setUnitPrice(itemDTO.getUnitPrice());
                    orderItem.setTotalPrice(itemDTO.getUnitPrice() * itemDTO.getQuantity());

                    return orderItem;
                })
                .collect(Collectors.toList());

        double subtotal = calculateSubtotal(orderItems);
        double totalAmount = subtotal;

        order.setSubTotal(subtotal);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        EmployeeOrder savedOrder = employeeOrderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Override
    public EmployeeOrderDTO getEmployeeOrderById(Long id) {
        EmployeeOrder order = employeeOrderRepository.findEmployeeOrderWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee order not found with id: " + id));
        return convertToDTO(order);
    }

    @Override
    public List<EmployeeOrderDTO> getAllEmployeeOrders() {
        return employeeOrderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeOrderDTO> getOrdersByEmployee(String employeeAccNum) {
        List<EmployeeOrder> orders = employeeOrderRepository.findEmployeeOrdersWithItemsByEmployeeAccountNumber(employeeAccNum);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeOrderDTO> getOrdersByCustomer(String customerAccNum) {
        List<EmployeeOrder> orders = employeeOrderRepository.findEmployeeOrdersWithItemsByCustomerAccountNumber(customerAccNum);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeOrderDTO> getEmployeeOrdersByStatus(ORDER_STATUS status) {
        return employeeOrderRepository.findByOrderStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeOrderDTO updateEmployeeOrderStatus(Long id, ORDER_STATUS orderStatus) {
        EmployeeOrder order = employeeOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee order not found with id: " + id));

        order.setOrderStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());

        if (orderStatus == ORDER_STATUS.COMPLETED) {
            order.setPaymentStatus(PAYMENT_STATUS.PAID);
        }

        EmployeeOrder savedOrder = employeeOrderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Override
    public EmployeeOrderDTO cancelEmployeeOrder(Long id, String currentUser, boolean isManager) {
        EmployeeOrder order = employeeOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee order not found with id: " + id));

        if (!isManager) {
            if (!order.getEmployee().getAccountNumber().equals(currentUser)) {
                throw new AccessDeniedException("You can only cancel your own employee orders");
            }
        }

        if (order.getOrderStatus() != ORDER_STATUS.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled");
        }

        returnBooksToStock(order.getItems());

        order.setOrderStatus(ORDER_STATUS.CANCELLED);
        order.setPaymentStatus(PAYMENT_STATUS.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        EmployeeOrder savedOrder = employeeOrderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    private double calculateSubtotal(List<EmployeeOrderItem> items) {
        return items.stream()
                .mapToDouble(EmployeeOrderItem::getTotalPrice)
                .sum();
    }

    private void returnBooksToStock(List<EmployeeOrderItem> items) {
        items.forEach(item -> {
            Book book = item.getBook();
            book.setStock(book.getStock() + item.getQuantity());
            bookRepository.save(book);
        });
    }

    private EmployeeOrderDTO convertToDTO(EmployeeOrder order) {
        EmployeeOrderDTO dto = modelMapper.map(order, EmployeeOrderDTO.class);

        dto.setEmployeeId(order.getEmployee().getId());
        dto.setEmployeeAccountNumber(order.getEmployee().getAccountNumber());
        dto.setEmployeeName(order.getEmployee().getFullName());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerAccountNumber(order.getCustomer().getAccountNumber());
        dto.setCustomerName(order.getCustomer().getFullName());

        return dto;
    }
}