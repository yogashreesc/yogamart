package com.yogashree.yogamart.exception;

/**
 * Thrown by OrderDAO when a product's stock changes between when it
 * was added to the cart and when checkout tries to lock it in —
 * e.g. another buyer bought the last units first. Caught by
 * OrderService and re-surfaced as a ValidationException so it reaches
 * the servlet through the same error-handling path as everything else.
 */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}