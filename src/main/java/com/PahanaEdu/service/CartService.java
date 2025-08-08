package com.PahanaEdu.service;

import com.PahanaEdu.dto.CartDTO;
import com.PahanaEdu.dto.CartItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    CartDTO createCartForUser(String accountNumber);
    CartItemDTO addCartItem(CartItemDTO cartItemDTO, String accountNumber);
    List<CartDTO> getCarts();
    List<CartItemDTO> getCartItems(Long cartId);
    CartDTO getCartById(Long cartId);
    CartDTO getCartByUserAccountNumber(String accountNumber);
    CartDTO checkOutCart(String accountNumber);
    CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity);
    CartItemDTO removeCartItem(Long cartItemId, String accountNumber);
    void updateCartTotal(Long cartId);
    void clearCart(String accountNumber);
}