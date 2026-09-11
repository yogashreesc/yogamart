package com.yogashree.yogamart.service;

import com.yogashree.yogamart.dao.UserDAO;
import com.yogashree.yogamart.exception.AuthenticationException;
import com.yogashree.yogamart.exception.DuplicateEmailException;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.model.User;
import com.yogashree.yogamart.util.PasswordUtil;
import com.yogashree.yogamart.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Business rules for registration and login. No JDBC here — the service
 * layer depends only on the UserDAO interface (Section 2, rule per
 * architecture diagram; Section 12 SOLID rule).
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new BUYER or SELLER account. Validation happens here,
     * before any DAO call, per Section 13 rule 5.
     */
    public User register(String name, String email, String rawPassword, String role)
            throws ValidationException, DuplicateEmailException, SQLException {

        if (ValidationUtil.isBlank(name)) {
            throw new ValidationException("Name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("A valid email is required.");
        }
        if (ValidationUtil.isBlank(rawPassword) || rawPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters.");
        }
        if (!ValidationUtil.isValidRole(role)) {
            throw new ValidationException("Role must be BUYER or SELLER.");
        }

        Optional<User> existing = userDAO.findByEmail(email);
        if (existing.isPresent()) {
            throw new DuplicateEmailException("An account with this email already exists.");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(PasswordUtil.hash(rawPassword));
        user.setRole(role);

        return userDAO.create(user);
    }

    /**
     * Verifies credentials and returns the matching user. Never leaks
     * whether the failure was "no such email" vs. "wrong password" —
     * both surface as the same AuthenticationException.
     */
    public User login(String email, String rawPassword) throws AuthenticationException, SQLException {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(rawPassword)) {
            throw new AuthenticationException("Email and password are required.");
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty() || !PasswordUtil.verify(rawPassword, userOpt.get().getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return userOpt.get();
    }
}
