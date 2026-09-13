package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.model.CartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private final DataSource dataSource;

    public CartDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<CartItem> findRaw(int userId, int productId) throws SQLException {
        String sql = "SELECT id, user_id, product_id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    return Optional.of(item);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<CartLineItem> findByUser(int userId) throws SQLException {
        String sql = "SELECT ci.id AS cart_item_id, ci.quantity, " +
                "p.id AS product_id, p.name, p.image_url, p.price, p.stock_qty " +
                "FROM cart_items ci " +
                "JOIN products p ON p.id = ci.product_id " +
                "WHERE ci.user_id = ? " +
                "ORDER BY ci.id";
        List<CartLineItem> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartLineItem line = new CartLineItem();
                    line.setCartItemId(rs.getInt("cart_item_id"));
                    line.setProductId(rs.getInt("product_id"));
                    line.setProductName(rs.getString("name"));
                    line.setImageUrl(rs.getString("image_url"));
                    line.setPrice(rs.getBigDecimal("price"));
                    line.setQuantity(rs.getInt("quantity"));
                    line.setAvailableStock(rs.getInt("stock_qty"));
                    results.add(line);
                }
            }
        }
        return results;
    }

    @Override
    public void insert(CartItem item) throws SQLException {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getUserId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public boolean updateQuantity(int cartItemId, int userId, int quantity) throws SQLException {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int cartItemId, int userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public void clearForUser(int userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
