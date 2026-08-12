package com.yogashree.yogamart.exception;

/** Thrown when a registration attempts to use an email already on file. */
public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
