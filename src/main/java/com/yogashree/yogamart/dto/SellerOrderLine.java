package com.yogashree.yogamart.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * F6 — one row in a seller's "incoming orders" view: an order_items
 * row for one of THIS seller's products, joined with the parent
 * order's status/date and the buyer's name. A single real order can
 * span multiple sellers, so this is deliberately a flat line-item
 * list (one row per product sold) rather than grouped by order —
 * simpler to query correctly and matches what a seller actually
 * needs to see ("what of mine sold, to whom, when").
 */
public class SellerOrderLine {

    private int orderId;
    private String orderStatus;
    private Timestamp orderDate;
    private String buyerName;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    public SellerOrderLine() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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
