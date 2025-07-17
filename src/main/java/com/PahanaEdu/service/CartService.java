package com.PahanaEdu.service;

import com.PahanaEdu.dto.CartDTO;
import com.PahanaEdu.dto.CartItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    CartItemDTO addCartItem(CartItemDTO cartItemDTO, Long userID);
    List<CartDTO> getCarts();
    List<CartItemDTO> getCartItems(Long cartId);
    CartDTO getCartById(Long cartId);
    CartDTO getCartByUserId(Long userId);
    CartDTO checkOutCart(Long userId);
    CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity);
    CartItemDTO removeCartItem(Long cartItemId, Long userId);
}
