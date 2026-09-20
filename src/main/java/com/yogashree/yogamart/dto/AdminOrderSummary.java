package com.yogashree.yogamart.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * F7 — one row in the admin's "view all orders" list: an order
 * joined with its buyer's name, across every buyer in the system.
 */
public class AdminOrderSummary {

    private int orderId;
    private String buyerName;
    private String status;
    private BigDecimal totalAmount;
    private Timestamp createdAt;

    public AdminOrderSummary() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
