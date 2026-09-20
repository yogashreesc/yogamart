package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.dto.OrderLineItem;
import com.yogashree.yogamart.dto.SellerOrderLine;
import com.yogashree.yogamart.exception.InsufficientStockException;
import com.yogashree.yogamart.model.Order;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {

    /**
     * F5 — the whole checkout operation as one atomic transaction:
     * creates the order row, decrements stock per item (re-checked
     * against live stock, not the cart's cached copy), inserts
     * order_items with price locked at current value, and clears the
     * buyer's cart. All-or-nothing: any failure rolls back everything.
     *
     * @return the new order's id
     */
    int placeOrder(int buyerId, List<CartLineItem> cartItems) throws SQLException, InsufficientStockException;

    Optional<Order> findById(int orderId) throws SQLException;

    List<OrderLineItem> findLineItems(int orderId) throws SQLException;

    List<Order> findByBuyer(int buyerId) throws SQLException;

    /**
     * F6 — seller's incoming orders: one row per order_items entry
     * for any of this seller's products, across all buyers.
     */
    List<SellerOrderLine> findLineItemsForSeller(int sellerId) throws SQLException;
}
