package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.model.CartItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CartDAO {

    /**
     * The raw cart_items row for this user+product, if one already
     * exists — used by the service to decide insert-new vs.
     * increment-existing when adding to cart.
     */
    Optional<CartItem> findRaw(int userId, int productId) throws SQLException;

    /**
     * Cart contents joined with current product details, for display.
     */
    List<CartLineItem> findByUser(int userId) throws SQLException;

    void insert(CartItem item) throws SQLException;

    /**
     * Ownership-scoped: WHERE clause includes userId so a tampered
     * cartItemId can never update another user's cart row.
     */
    boolean updateQuantity(int cartItemId, int userId, int quantity) throws SQLException;

    boolean delete(int cartItemId, int userId) throws SQLException;

    void clearForUser(int userId) throws SQLException;
}
