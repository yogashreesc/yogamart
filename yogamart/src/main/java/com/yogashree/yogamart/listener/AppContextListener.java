package com.yogashree.yogamart.listener;

import com.yogashree.yogamart.dao.UserDAO;
import com.yogashree.yogamart.dao.UserDAOImpl;
import com.yogashree.yogamart.model.User;
import com.yogashree.yogamart.util.PasswordUtil;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Owns the DataSource lifecycle end-to-end (Section 2, rule 5: no
 * DriverManager.getConnection() calls anywhere outside this listener).
 *
 * NOTE on scope for the MVP checkpoint: this uses H2's JdbcDataSource
 * directly rather than the HikariCP connection pool specified in
 * Section 3. HikariCP wiring is the first item planned for Week 3 —
 * flagged here and in the README as known tech debt so the swap is a
 * one-file change (this listener only) when it happens, since the
 * Service/DAO layers only depend on javax.sql.DataSource, not on
 * which pool implementation provides it.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);
    public static final String DATASOURCE_ATTR = "dataSource";

    private JdbcDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        dataSource = new JdbcDataSource();
        // Embedded H2 for local dev; DB_CLOSE_DELAY=-1 keeps data alive across
        // reconnects within the same JVM. Swap to jdbc:h2:tcp://... for the
        // Sep 21 server-mode deployment per Section 10.
        dataSource.setURL("jdbc:h2:./data/yogamart;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        try {
            runScript(dataSource, "/schema.sql");
            seedUsersIfEmpty(dataSource);
            runScript(dataSource, "/seed.sql");
            log.info("YogaMart: schema initialized and seed data loaded.");
        } catch (SQLException | IOException e) {
            log.error("YogaMart: failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }

        sce.getServletContext().setAttribute(DATASOURCE_ATTR, dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // JdbcDataSource holds no pooled connections to close explicitly;
        // this hook is where HikariCP's dataSource.close() will go post-swap.
        log.info("YogaMart: context shutting down.");
    }

    /**
     * Seeds one ADMIN and one SELLER account with bcrypt-hashed passwords,
     * only if the users table is currently empty — keeps re-deploys from
     * duplicating seed users.
     */
    private void seedUsersIfEmpty(JdbcDataSource ds) throws SQLException {
        UserDAO userDAO = new UserDAOImpl(ds);
        if (userDAO.findByEmail("admin@yogamart.local").isPresent()) {
            return; // already seeded
        }

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@yogamart.local");
        admin.setPasswordHash(PasswordUtil.hash("Admin@123"));
        admin.setRole("ADMIN");
        userDAO.create(admin);

        User seller = new User();
        seller.setName("Demo Seller");
        seller.setEmail("seller@yogamart.local");
        seller.setPasswordHash(PasswordUtil.hash("Seller@123"));
        seller.setRole("SELLER");
        userDAO.create(seller); // becomes seller_id = 2, matching seed.sql product rows

        log.info("YogaMart: seeded admin@yogamart.local and seller@yogamart.local (see README for passwords).");
    }

    private void runScript(JdbcDataSource ds, String classpathResource) throws SQLException, IOException {
        StringBuilder script = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream(classpathResource);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        }

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String statement : script.toString().split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
}
