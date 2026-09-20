package com.yogashree.yogamart.service;

import com.yogashree.yogamart.dao.CartDAO;
import com.yogashree.yogamart.dao.OrderDAO;
import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.dto.OrderLineItem;
import com.yogashree.yogamart.dto.SellerOrderLine;
import com.yogashree.yogamart.exception.InsufficientStockException;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.model.Order;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * F5 — checkout. Thin orchestration layer: OrderDAO.placeOrder() owns
 * the actual transaction (order + order_items + stock decrement +
 * cart clear, all-or-nothing). This service's job is validating there
 * is something to check out at all, and translating
 * InsufficientStockException into the same ValidationException path
 * every other controller already handles.
 */
public class OrderService {

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;

    public OrderService(OrderDAO orderDAO, CartDAO cartDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
    }

    /**
     * Mock payment confirmation: no real payment gateway per the
     * spec's scope constraints (Section 1) — this step simply commits
     * the order once the buyer confirms.
     */
    public int checkout(int buyerId) throws SQLException, ValidationException {
        List<CartLineItem> cartItems = cartDAO.findByUser(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("Your cart is empty.");
        }

        try {
            return orderDAO.placeOrder(buyerId, cartItems);
        } catch (InsufficientStockException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public Optional<Order> getOrder(int orderId) throws SQLException {
        return orderDAO.findById(orderId);
    }

    public List<OrderLineItem> getOrderItems(int orderId) throws SQLException {
        return orderDAO.findLineItems(orderId);
    }

    public List<Order> getOrdersForBuyer(int buyerId) throws SQLException {
        return orderDAO.findByBuyer(buyerId);
    }

    /**
     * F6 — seller's incoming orders view.
     */
    public List<SellerOrderLine> getSellerOrderLines(int sellerId) throws SQLException {
        return orderDAO.findLineItemsForSeller(sellerId);
    }
}