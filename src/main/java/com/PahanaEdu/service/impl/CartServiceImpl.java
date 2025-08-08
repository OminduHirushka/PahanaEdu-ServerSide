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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private User findUserByIdentifier(String identifier) {
        Optional<User> userByAccountNumber = userRepository.findByAccountNumber(identifier);
        if (userByAccountNumber.isPresent()) {
            return userByAccountNumber.get();
        }

        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }

        throw new ResourceNotFoundException("User not found with identifier: " + identifier);
    }

    @Override
    public CartDTO createCartForUser(String identifier) {
        User user = findUserByIdentifier(identifier);

        Optional<Cart> existingCartOpt = cartRepository.findByUser_IdAndCheckedOutFalse(user.getId());

        if (existingCartOpt.isPresent()) {
            return modelMapper.map(existingCartOpt.get(), CartDTO.class);
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCheckedOut(false);
        cart.setTotalPrice(0.0);
        cart.setCreatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        return modelMapper.map(savedCart, CartDTO.class);
    }

    @Override
    public CartItemDTO addCartItem(CartItemDTO cartItemDTO, String identifier) {
        User user = findUserByIdentifier(identifier);

        Book book = bookRepository.findById(cartItemDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book Not Found"));

        if (book.getStock() < cartItemDTO.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        if (cartItemDTO == null || cartItemDTO.getQuantity() == null || cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        Cart cart = cartRepository.findByUser_IdAndCheckedOutFalse(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCheckedOut(false);
                    newCart.setTotalPrice(0.0);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndBook(cart, book);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + cartItemDTO.getQuantity();

            if (book.getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock available for updated quantity");
            }

            book.setStock(book.getStock() - cartItemDTO.getQuantity());
            bookRepository.save(book);

            return updateCartItemQuantity(existingItem.getId(), newQuantity);
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setPrice(book.getPrice());
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setTotalPrice(book.getPrice() * cartItemDTO.getQuantity());

        book.setStock(book.getStock() - cartItemDTO.getQuantity());
        bookRepository.save(book);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        updateCartTotal(cart.getId());

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
                .map(cartItem -> {
                    CartItemDTO cartItemDTO = modelMapper.map(cartItem, CartItemDTO.class);

                    cartItemDTO.setBookName(cartItem.getBook().getName());
                    cartItemDTO.setBookCover(cartItem.getBook().getCover());
                    cartItemDTO.setPublisherName(cartItem.getBook().getPublisher().getName());
                    return cartItemDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartById(Long cartId) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));

        return modelMapper.map(existingCart, CartDTO.class);
    }

    @Override
    public CartDTO getCartByUserAccountNumber(String identifier) {
        User user = findUserByIdentifier(identifier);

        Optional<Cart> cartOpt = cartRepository.findByUser_IdAndCheckedOutFalse(user.getId());

        if (cartOpt.isPresent()) {
            return modelMapper.map(cartOpt.get(), CartDTO.class);
        }

        return createCartForUser(identifier);
    }

    @Override
    public CartDTO checkOutCart(String identifier) {
        User user = findUserByIdentifier(identifier);

        Cart existingCart = cartRepository.findByUser_IdAndCheckedOutFalse(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found for user"));

        if (existingCart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout empty cart");
        }

        existingCart.setCheckedOut(true);
        Cart savedCart = cartRepository.save(existingCart);

        createCartForUser(identifier);
        return modelMapper.map(savedCart, CartDTO.class);
    }

    @Override
    public CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem existingCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item Not Found"));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Book book = existingCartItem.getBook();
        int quantityDifference = quantity - existingCartItem.getQuantity();

        if (book.getStock() < quantityDifference) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        book.setStock(book.getStock() - quantityDifference);
        bookRepository.save(book);

        existingCartItem.setQuantity(quantity);
        existingCartItem.setTotalPrice(existingCartItem.getBook().getPrice() * quantity);

        CartItem savedCartItem = cartItemRepository.save(existingCartItem);
        updateCartTotal(existingCartItem.getCart().getId());

        return modelMapper.map(savedCartItem, CartItemDTO.class);
    }

    @Override
    public CartItemDTO removeCartItem(Long cartItemId, String identifier) {
        User user = findUserByIdentifier(identifier);

        Cart cart = cartRepository.findByUser_IdAndCheckedOutFalse(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found for user"));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to the user's cart");
        }

        Book book = cartItem.getBook();
        book.setStock(book.getStock() + cartItem.getQuantity());
        bookRepository.save(book);

        cartItemRepository.delete(cartItem);

        if (cart.getItems() != null) {
            cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        }

        updateCartTotal(cart.getId());
        return modelMapper.map(cartItem, CartItemDTO.class);
    }

    @Override
    public void updateCartTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));

        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String identifier) {
        try {
            User user = findUserByIdentifier(identifier);

            Cart cart = cartRepository.findByUser_IdAndCheckedOutFalse(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No active cart found for user"));

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                return;
            }

            List<CartItem> itemsToClear = new ArrayList<>(cart.getItems());

            for (CartItem item : itemsToClear) {
                Book book = item.getBook();
                book.setStock(book.getStock() + item.getQuantity());
                bookRepository.save(book);
            }

            cart.getItems().clear();
            cartRepository.saveAndFlush(cart);
            cartItemRepository.deleteAllByCartId(cart.getId());
            cart.setTotalPrice(0.0);

            cartRepository.save(cart);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear cart: " + e.getMessage(), e);
        }
    }
}
