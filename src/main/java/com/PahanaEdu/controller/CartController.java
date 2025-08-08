package com.PahanaEdu.controller;

import com.PahanaEdu.dto.CartDTO;
import com.PahanaEdu.dto.CartItemDTO;
import com.PahanaEdu.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin(origins = "*")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/{accountNumber}/add-item")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> addCartItem(
            @PathVariable String accountNumber,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        try {
            CartItemDTO savedItem = cartService.addCartItem(cartItemDTO, accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Item added to cart successfully");
            response.put("cartItem", savedItem);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getAllCarts() {
        try {
            List<CartDTO> allCarts = cartService.getCarts();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "All carts retrieved successfully");
            response.put("carts", allCarts);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{cartId}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCartItems(@PathVariable Long cartId) {
        try {
            List<CartItemDTO> cartItems = cartService.getCartItems(cartId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart items retrieved successfully");
            response.put("items", cartItems);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{cartId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCartById(@PathVariable Long cartId) {
        try {
            CartDTO cart = cartService.getCartById(cartId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart retrieved successfully");
            response.put("cart", cart);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{accountNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCartByUserAccountNumber(@PathVariable String accountNumber) {
        try {
            CartDTO cart = cartService.getCartByUserAccountNumber(accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User cart retrieved successfully");
            response.put("cart", cart);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{accountNumber}/with-items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCartWithItemsByUserAccountNumber(@PathVariable String accountNumber) {
        try {
            CartDTO cart = cartService.getCartByUserAccountNumber(accountNumber);
            List<CartItemDTO> items = cartService.getCartItems(cart.getId());

            Map<String, Object> cleanCart = new HashMap<>();
            cleanCart.put("id", cart.getId());
            cleanCart.put("userId", cart.getUserId());
            cleanCart.put("userAccountNumber", cart.getUserAccountNumber());
            cleanCart.put("totalPrice", cart.getTotalPrice());
            cleanCart.put("items", items);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User cart retrieved successfully");
            response.put("cart", cleanCart);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{accountNumber}/checkout")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> checkOutCart(@PathVariable String accountNumber) {
        try {
            CartDTO checkedOutCart = cartService.checkOutCart(accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart checked out successfully");
            response.put("cart", checkedOutCart);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/items/{cartItemId}/quantity")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        try {
            CartItemDTO updatedItem = cartService.updateCartItemQuantity(cartItemId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart item quantity updated successfully");
            response.put("cartItem", updatedItem);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{accountNumber}/items/{cartItemId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @PathVariable String accountNumber,
            @PathVariable Long cartItemId) {
        try {
            CartItemDTO removedItem = cartService.removeCartItem(cartItemId, accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Item removed from cart successfully");
            response.put("removedItem", removedItem);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{accountNumber}/clear")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> clearCart(@PathVariable String accountNumber) {
        try {
            cartService.clearCart(accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{accountNumber}/create")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createCartForUser(@PathVariable String accountNumber) {
        try {
            CartDTO cart = cartService.createCartForUser(accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart created successfully");
            response.put("cart", cart);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
