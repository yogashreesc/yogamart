package com.yogashree.yogamart.exception;

/** Thrown by the service layer when input fails validation before any DAO call. */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
