package com.yogashree.yogamart.service;

import com.yogashree.yogamart.dao.OrderDAO;
import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.dao.UserDAO;
import com.yogashree.yogamart.dto.AdminOrderSummary;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.model.Product;
import com.yogashree.yogamart.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * F7 — admin: view all users/orders, moderate (remove) listings.
 * Thin orchestration only, same pattern as the other service classes —
 * all SQL stays in the DAOs.
 */
public class AdminService {

    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public AdminService(UserDAO userDAO, OrderDAO orderDAO, ProductDAO productDAO) {
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    public List<AdminOrderSummary> getAllOrders() throws SQLException {
        return orderDAO.findAllForAdmin();
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    public void removeListing(int productId) throws SQLException, ValidationException {
        boolean removed = productDAO.deleteAsAdmin(productId);
        if (!removed) {
            throw new ValidationException("Product not found.");
        }
    }
}
