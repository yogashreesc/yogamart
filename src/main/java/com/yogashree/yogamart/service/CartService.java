package com.yogashree.yogamart.service;

import com.yogashree.yogamart.dao.CartDAO;
import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.model.CartItem;
import com.yogashree.yogamart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * F4 — cart business rules. No JDBC here (Section 2's layering rule);
 * all SQL stays in CartDAO/ProductDAO.
 */
public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /**
     * Adds a product to the cart, or increments quantity if it's
     * already in there. Validates against live stock each time —
     * stock_qty can change between visits, so this isn't just a
     * one-time check at first add.
     */
    public void addToCart(int userId, int productId, int requestedQty) throws SQLException, ValidationException {
        if (requestedQty < 1) {
            throw new ValidationException("Quantity must be at least 1.");
        }

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found."));

        Optional<CartItem> existing = cartDAO.findRaw(userId, productId);
        int newTotalQty = requestedQty + existing.map(CartItem::getQuantity).orElse(0);

        if (newTotalQty > product.getStockQty()) {
            throw new ValidationException(
                    "Only " + product.getStockQty() + " in stock" +
                    (existing.isPresent() ? " (" + existing.get().getQuantity() + " already in your cart)." : "."));
        }

        if (existing.isPresent()) {
            cartDAO.updateQuantity(existing.get().getId(), userId, newTotalQty);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(requestedQty);
            cartDAO.insert(item);
        }
    }

    public void updateQuantity(int userId, int cartItemId, int newQty) throws SQLException, ValidationException {
        if (newQty < 1) {
            throw new ValidationException("Quantity must be at least 1. Use Remove to delete an item.");
        }

        // Look up the line via the user's own cart, so we both confirm
        // ownership and get the product's current stock in one pass.
        List<CartLineItem> lines = cartDAO.findByUser(userId);
        CartLineItem target = lines.stream()
                .filter(l -> l.getCartItemId() == cartItemId)
                .findFirst()
                .orElseThrow(() -> new ValidationException("Cart item not found."));

        if (newQty > target.getAvailableStock()) {
            throw new ValidationException("Only " + target.getAvailableStock() + " in stock.");
        }

        cartDAO.updateQuantity(cartItemId, userId, newQty);
    }

    public void removeItem(int userId, int cartItemId) throws SQLException, ValidationException {
        boolean removed = cartDAO.delete(cartItemId, userId);
        if (!removed) {
            throw new ValidationException("Cart item not found, or it doesn't belong to you.");
        }
    }

    public List<CartLineItem> getCartItems(int userId) throws SQLException {
        return cartDAO.findByUser(userId);
    }

    public BigDecimal getCartTotal(List<CartLineItem> items) {
        return items.stream()
                .map(CartLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
