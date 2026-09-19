package com.yogashree.yogamart.dto;

import java.math.BigDecimal;

/**
 * F5 — one line item in a placed order, joined with the product's
 * name/image for display. unit_price here is what was LOCKED IN at
 * checkout (order_items.unit_price), not the product's current price
 * — matches the schema's intent of preserving historical pricing.
 */
public class OrderLineItem {

    private String productName;
    private String imageUrl;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderLineItem() {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}