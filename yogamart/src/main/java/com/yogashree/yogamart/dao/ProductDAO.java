package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {

    List<Product> findAll() throws SQLException;

    Optional<Product> findById(int id) throws SQLException;

    List<Product> search(String keyword, String category) throws SQLException;
}
