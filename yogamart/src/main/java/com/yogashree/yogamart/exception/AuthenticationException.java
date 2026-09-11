package com.yogashree.yogamart.exception;

/** Thrown on login when credentials don't match a known user. */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
