package com.PahanaEdu.service;

import com.PahanaEdu.dto.CartDTO;
import com.PahanaEdu.dto.CartItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    CartDTO createCartForUser(Long userId);
    CartItemDTO addCartItem(CartItemDTO cartItemDTO, Long userID);
    List<CartDTO> getCarts();
    List<CartItemDTO> getCartItems(Long cartId);
    CartDTO getCartById(Long cartId);
    CartDTO getCartByUserId(Long userId);
    CartDTO checkOutCart(Long userId);
    CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity);
    CartItemDTO removeCartItem(Long cartItemId, Long userId);
    void updateCartTotal(Long cartId);
    void clearCart(Long userId);
}
