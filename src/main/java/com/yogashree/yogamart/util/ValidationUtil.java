package com.yogashree.yogamart.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return !isBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidRole(String role) {
        return "BUYER".equals(role) || "SELLER".equals(role);
        // ADMIN is seeded, never chosen via signup — per spec F1
    }
}
