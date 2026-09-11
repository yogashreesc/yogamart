package com.yogashree.yogamart.service;

import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.model.Product;
import com.yogashree.yogamart.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    /**
     * F2 (seller product CRUD) — create step. Sellers list a new product;
     * validated here before hitting the DAO so bad data never reaches SQL.
     */
    public Product addProduct(int sellerId, String name, String description,
                               BigDecimal price, int stockQty, String category, String imageUrl)
            throws SQLException, ValidationException {

        if (ValidationUtil.isBlank(name)) {
            throw new ValidationException("Product name is required.");
        }
        if (price == null || price.signum() <= 0) {
            throw new ValidationException("Price must be a positive number.");
        }
        if (stockQty < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }
        if (ValidationUtil.isBlank(category)) {
            throw new ValidationException("Category is required.");
        }

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(description == null ? "" : description.trim());
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setCategory(category.trim());
        product.setImageUrl(ValidationUtil.isBlank(imageUrl)
                ? "https://placehold.co/300x200?text=" + name.trim().replace(" ", "+")
                : imageUrl.trim());

        return productDAO.create(product);
    }

    public Optional<Product> getById(int id) throws SQLException {
        return productDAO.findById(id);
    }

    /**
     * F2 — seller's own listings, for the "my products" management view.
     */
    public List<Product> getSellerProducts(int sellerId) throws SQLException {
        return productDAO.findBySeller(sellerId);
    }

    /**
     * F2 — edit. Re-validates the same rules as addProduct() since this is
     * a fresh save, not a partial patch. Ownership is enforced again here
     * (belt-and-braces with the DAO's WHERE seller_id = ? clause) so a
     * tampered id in the form can never edit someone else's listing.
     */
    public void updateProduct(int productId, int sellerId, String name, String description,
                               BigDecimal price, int stockQty, String category, String imageUrl)
            throws SQLException, ValidationException {

        if (ValidationUtil.isBlank(name)) {
            throw new ValidationException("Product name is required.");
        }
        if (price == null || price.signum() <= 0) {
            throw new ValidationException("Price must be a positive number.");
        }
        if (stockQty < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }
        if (ValidationUtil.isBlank(category)) {
            throw new ValidationException("Category is required.");
        }

        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(description == null ? "" : description.trim());
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setCategory(category.trim());
        product.setImageUrl(ValidationUtil.isBlank(imageUrl)
                ? "https://placehold.co/300x200?text=" + name.trim().replace(" ", "+")
                : imageUrl.trim());

        boolean updated = productDAO.update(product);
        if (!updated) {
            throw new ValidationException("Product not found, or you don't have permission to edit it.");
        }
    }

    /**
     * F2 — delete. Ownership enforced in the DAO's WHERE clause.
     */
    public void deleteProduct(int productId, int sellerId) throws SQLException, ValidationException {
        boolean deleted = productDAO.delete(productId, sellerId);
        if (!deleted) {
            throw new ValidationException("Product not found, or you don't have permission to delete it.");
        }
    }
}
