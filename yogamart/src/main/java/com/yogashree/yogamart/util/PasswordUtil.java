package com.yogashree.yogamart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps jBCrypt so no other class ever touches raw hashing calls directly.
 * Passwords are never stored, logged, or compared in plaintext anywhere
 * in this codebase — see Section 2, engineering rule 2 of the spec.
 */
public final class PasswordUtil {

    private PasswordUtil() {
        // static utility, no instances
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean verify(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
