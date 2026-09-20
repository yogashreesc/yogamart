package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.dto.OrderLineItem;
import com.yogashree.yogamart.dto.SellerOrderLine;
import com.yogashree.yogamart.exception.InsufficientStockException;
import com.yogashree.yogamart.model.Order;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    private final DataSource dataSource;

    public OrderDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int placeOrder(int buyerId, List<CartLineItem> cartItems) throws SQLException, InsufficientStockException {
        BigDecimal total = cartItems.stream()
                .map(CartLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            int orderId = insertOrder(conn, buyerId, total);

            for (CartLineItem item : cartItems) {
                int updated = decrementStock(conn, item.getProductId(), item.getQuantity());
                if (updated == 0) {
                    conn.rollback();
                    throw new InsufficientStockException(
                            "\"" + item.getProductName() + "\" no longer has enough stock for the quantity in your cart.");
                }
                insertOrderItem(conn, orderId, item.getProductId(), item.getQuantity(), item.getPrice());
            }

            clearCart(conn, buyerId);

            conn.commit();
            return orderId;
        } catch (SQLException | InsufficientStockException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private int insertOrder(Connection conn, int buyerId, BigDecimal total) throws SQLException {
        String sql = "INSERT INTO orders (buyer_id, status, total_amount) VALUES (?, 'CONFIRMED', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, buyerId);
            ps.setBigDecimal(2, total);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private int decrementStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate();
        }
    }

    private void insertOrderItem(Connection conn, int orderId, int productId, int quantity, BigDecimal unitPrice)
            throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, unitPrice);
            ps.executeUpdate();
        }
    }

    private void clearCart(Connection conn, int buyerId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Order> findById(int orderId) throws SQLException {
        String sql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapOrder(rs));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<OrderLineItem> findLineItems(int orderId) throws SQLException {
        String sql = "SELECT oi.quantity, oi.unit_price, p.name, p.image_url " +
                "FROM order_items oi " +
                "JOIN products p ON p.id = oi.product_id " +
                "WHERE oi.order_id = ?";
        List<OrderLineItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderLineItem item = new OrderLineItem();
                    item.setProductName(rs.getString("name"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public List<Order> findByBuyer(int buyerId) throws SQLException {
        String sql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders " +
                "WHERE buyer_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    @Override
    public List<SellerOrderLine> findLineItemsForSeller(int sellerId) throws SQLException {
        String sql = "SELECT o.id AS order_id, o.status, o.created_at, u.name AS buyer_name, " +
                "p.name AS product_name, oi.quantity, oi.unit_price " +
                "FROM order_items oi " +
                "JOIN orders o ON o.id = oi.order_id " +
                "JOIN products p ON p.id = oi.product_id " +
                "JOIN users u ON u.id = o.buyer_id " +
                "WHERE p.seller_id = ? " +
                "ORDER BY o.created_at DESC";
        List<SellerOrderLine> lines = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerOrderLine line = new SellerOrderLine();
                    line.setOrderId(rs.getInt("order_id"));
                    line.setOrderStatus(rs.getString("status"));
                    line.setOrderDate(rs.getTimestamp("created_at"));
                    line.setBuyerName(rs.getString("buyer_name"));
                    line.setProductName(rs.getString("product_name"));
                    line.setQuantity(rs.getInt("quantity"));
                    line.setUnitPrice(rs.getBigDecimal("unit_price"));
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setBuyerId(rs.getInt("buyer_id"));
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }
}