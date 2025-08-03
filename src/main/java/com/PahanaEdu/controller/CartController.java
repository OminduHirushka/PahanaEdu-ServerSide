package com.PahanaEdu.controller;

import com.PahanaEdu.dto.CartDTO;
import com.PahanaEdu.dto.CartItemDTO;
import com.PahanaEdu.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}/add-item")
    public ResponseEntity<Map<String, Object>> addCartItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        CartItemDTO savedItem = cartService.addCartItem(cartItemDTO, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item added to cart successfully");
        response.put("cartItem", savedItem);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllCarts() {
        List<CartDTO> allCarts = cartService.getCarts();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All carts retrieved successfully");
        response.put("carts", allCarts);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{cartId}/items")
    public ResponseEntity<Map<String, Object>> getCartItems(@PathVariable Long cartId) {
        List<CartItemDTO> cartItems = cartService.getCartItems(cartId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart items retrieved successfully");
        response.put("items", cartItems);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Map<String, Object>> getCartById(@PathVariable Long cartId) {
        CartDTO cart = cartService.getCartById(cartId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart retrieved successfully");
        response.put("cart", cart);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCartByUserId(@PathVariable Long userId) {
        CartDTO cart = cartService.getCartByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User cart retrieved successfully");
        response.put("cart", cart);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{accNum}")
    public ResponseEntity<Map<String, Object>> getCartByUserAccountNumber(@PathVariable String accNum) {
        CartDTO cart = cartService.getCartByUserAccountNumber(accNum);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User cart retrieved successfully");
        response.put("cart", cart);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Map<String, Object>> checkOutCart(@PathVariable Long userId) {
        CartDTO checkedOutCart = cartService.checkOutCart(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart checked out successfully");
        response.put("cart", checkedOutCart);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        CartItemDTO updatedItem = cartService.updateCartItemQuantity(cartItemId, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cart item quantity updated successfully");
        response.put("cartItem", updatedItem);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        CartItemDTO removedItem = cartService.removeCartItem(cartItemId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Item removed from cart successfully");
        response.put("removedItem", removedItem);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}