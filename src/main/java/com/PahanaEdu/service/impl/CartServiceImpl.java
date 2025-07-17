package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.CartDTO;
import com.PahanaEdu.dto.CartItemDTO;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.Book;
import com.PahanaEdu.model.Cart;
import com.PahanaEdu.model.CartItem;
import com.PahanaEdu.model.User;
import com.PahanaEdu.repository.BookRepository;
import com.PahanaEdu.repository.CartItemRepository;
import com.PahanaEdu.repository.CartRepository;
import com.PahanaEdu.repository.UserRepository;
import com.PahanaEdu.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {
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
    public CartItemDTO addCartItem(CartItemDTO cartItemDTO, Long userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        Cart cart = cartRepository.findById(cartItemDTO.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));
        Book book = bookRepository.findById(cartItemDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book Not Found"));

        CartDTO cartDTO = getCartByUserId(user.getId());

        for (CartItem existingItem : cartDTO.getItems()) {
            if (existingItem.getBook().getId().equals(book.getId())) {
                int newQuantity = existingItem.getQuantity() + cartItemDTO.getQuantity();
                return updateCartItemQuantity(existingItem.getId(), newQuantity);
            }
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setPrice(book.getPrice());
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setTotalPrice(book.getPrice() * cartItemDTO.getQuantity());

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return modelMapper.map(savedCartItem, CartItemDTO.class);
    }

    @Override
    public List<CartDTO> getCarts() {
        return cartRepository.findAll().stream()
                .map(cart -> modelMapper.map(cart, CartDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItemDTO> getCartItems(Long cartId) {
        return cartItemRepository.findByCart_Id(cartId).stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartById(Long cartId) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));

        return modelMapper.map(existingCart, CartDTO.class);
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart existingCart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));

        return modelMapper.map(existingCart, CartDTO.class);
    }

    @Override
    public CartDTO checkOutCart(Long userId) {
        Cart existingCart = cartRepository.findByUser_IdAndCheckedOutFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));

        existingCart.setCheckedOut(true);
        return modelMapper.map(existingCart, CartDTO.class);
    }

    @Override
    public CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem existingCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item Not Found"));

        existingCartItem.setQuantity(quantity);
        existingCartItem.setPrice((existingCartItem.getBook().getPrice() * quantity));

        CartItem savedCartItem = cartItemRepository.save(existingCartItem);
        return modelMapper.map(savedCartItem, CartItemDTO.class);
    }

    @Override
    public CartItemDTO removeCartItem(Long cartItemId, Long userId) {
        Cart existingCart = cartRepository.findByUser_IdAndCheckedOutFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));
        CartItem existingCartItem = cartItemRepository.findByIdAndCart(cartItemId, existingCart)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item Not Found"));

        cartItemRepository.delete(existingCartItem);
        return modelMapper.map(existingCartItem, CartItemDTO.class);
    }
}
