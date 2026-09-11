package com.yogashree.yogamart.service;

import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.model.Product;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for browsing/searching products (F3). Thin for now —
 * this is where stock checks, pricing rules, etc. will grow into as
 * F2 (seller CRUD) and later features are added in Weeks 3+.
 */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> browse(String keyword, String category) throws SQLException {
        if ((keyword == null || keyword.isBlank()) && (category == null || category.isBlank())) {
            return productDAO.findAll();
        }
        return productDAO.search(keyword, category);
    }
}
