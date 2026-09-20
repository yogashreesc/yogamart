package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {

    List<Product> findAll() throws SQLException;

    Optional<Product> findById(int id) throws SQLException;

    List<Product> search(String keyword, String category) throws SQLException;

    Product create(Product product) throws SQLException;

    List<Product> findBySeller(int sellerId) throws SQLException;

    boolean update(Product product) throws SQLException;

    boolean delete(int id, int sellerId) throws SQLException;

    /**
     * F7 — admin moderation: removes a listing regardless of seller,
     * unlike delete() above which is ownership-scoped. Only ever
     * called from an admin-gated route.
     */
    boolean deleteAsAdmin(int id) throws SQLException;
}
