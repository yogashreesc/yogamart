package com.yogashree.yogamart.dto;

import java.math.BigDecimal;

/**
 * F4 — one line in the cart view: a cart_items row joined with its
 * product's current name/price/image/stock, so the JSP never has to
 * do its own lookups. Price shown is the product's CURRENT price
 * (not locked at add-to-cart time) — order_items (F5) is where price
 * gets locked in at checkout, per the schema's unit_price column.
 */
public class CartLineItem {

    private int cartItemId;
    private int productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;
    private int availableStock;

    public CartLineItem() {
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
