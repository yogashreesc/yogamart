package com.yogashree.yogamart.dao;

import com.yogashree.yogamart.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Data access abstraction for users. Service layer depends on this
 * interface, never on the concrete JDBC implementation (SOLID/DIP,
 * per Section 12 coding standards).
 */
public interface UserDAO {

    User create(User user) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findById(int id) throws SQLException;
}
