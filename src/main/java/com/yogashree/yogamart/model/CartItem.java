package com.yogashree.yogamart.model;

/**
 * Represents a row in the cart_items table (F4). Kept separate from
 * CartLineItem (dto), which is the joined, display-ready version used
 * by the cart view.
 */
public class CartItem {

    private int id;
    private int userId;
    private int productId;
    private int quantity;

    public CartItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
