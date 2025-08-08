package com.PahanaEdu.repository;

import com.PahanaEdu.model.Book;
import com.PahanaEdu.model.Cart;
import com.PahanaEdu.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_Id(Long cartId);
    Optional<CartItem> findByIdAndCart(Long id, Cart cart);
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
