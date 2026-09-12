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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            seedProductsIfEmpty(dataSource);
            deduplicateProducts(dataSource);
            refreshSeedProductImagesAndAddNew(dataSource);
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

    /**
     * Runs seed.sql only if the products table is currently empty — prevents
     * duplicate product rows piling up every time the app restarts against
     * the same persistent H2 data file (bug found during MVP dev: products
     * were re-inserted, with no existence check, on every single run).
     */
    private void seedProductsIfEmpty(JdbcDataSource ds) throws SQLException, IOException {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            rs.next();
            if (rs.getInt(1) > 0) {
                return; // already seeded
            }
        }
        runScript(ds, "/seed.sql");
    }

    /**
     * Safety net for the seed-duplication bug found during MVP dev
     * (see seedProductsIfEmpty's comment): removes exact duplicate
     * product rows — same seller, name, description, price, stock,
     * category, and image — keeping only the earliest-id copy of each.
     *
     * Runs unconditionally on every startup rather than being gated,
     * because it is idempotent: once duplicates are cleared, running
     * this again finds nothing to delete. This both fixes any
     * duplicates already sitting in an existing data file from before
     * seedProductsIfEmpty's guard existed, and protects against any
     * future re-introduction of the same bug.
     */
    private void deduplicateProducts(JdbcDataSource ds) throws SQLException {
        String sql = "DELETE FROM products p WHERE p.id NOT IN (" +
                "SELECT MIN(id) FROM products " +
                "GROUP BY seller_id, name, description, price, stock_qty, category, image_url)";
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            int removed = stmt.executeUpdate(sql);
            if (removed > 0) {
                log.info("YogaMart: removed {} duplicate product row(s).", removed);
            }
        }
    }

    /**
     * Backfills real product photos onto an already-seeded database (one
     * that predates the switch from via.placeholder.com text placeholders
     * to real Wikimedia Commons photos) and adds two new catalog items.
     *
     * Runs unconditionally on every startup, same idempotency approach as
     * deduplicateProducts(): each UPDATE just re-sets the same URL every
     * time (harmless no-op once applied), and each INSERT is guarded by
     * a "does a product with this name already exist for this seller"
     * check, so re-running never creates duplicates.
     */
    private void refreshSeedProductImagesAndAddNew(JdbcDataSource ds) throws SQLException {
        String[][] imageUpdates = {
                {"Digital Thermometer", "https://commons.wikimedia.org/wiki/Special:FilePath/Digital%20thermometer.jpg?width=500"},
                {"First Aid Kit - Compact", "https://commons.wikimedia.org/wiki/Special:FilePath/First%20Aid%20Kit.png?width=500"},
                {"Multivitamin Tablets (60ct)", "https://commons.wikimedia.org/wiki/Special:FilePath/B%20vitamin%20supplement%20tablets.jpg?width=500"},
                {"Blood Pressure Monitor", "https://commons.wikimedia.org/wiki/Special:FilePath/Blood%20pressure%20measurement.jpg?width=500"},
                {"N95 Face Masks (Pack of 10)", "https://commons.wikimedia.org/wiki/Special:FilePath/N95%20respirator%20transparent.png?width=500"},
                {"Hand Sanitizer 500ml", "https://commons.wikimedia.org/wiki/Special:FilePath/Purell%20hand%20sanitizer%20gel%20in%20bottle%20%288487014501%29.jpg?width=500"}
        };

        try (Connection conn = ds.getConnection()) {
            String updateSql = "UPDATE products SET image_url = ? WHERE name = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                for (String[] row : imageUpdates) {
                    ps.setString(1, row[1]);
                    ps.setString(2, row[0]);
                    ps.executeUpdate();
                }
            }

            addProductIfMissing(conn, "Omega-3 Fish Oil Capsules",
                    "Daily omega-3 fish oil softgels for heart and joint health.",
                    "699.00", 80, "Supplements",
                    "https://commons.wikimedia.org/wiki/Special:FilePath/Omega%203%20capsules%20in%20white%20bottle%20%2852715127894%29.jpg?width=500");

            addProductIfMissing(conn, "Surgical Face Masks (Pack of 50)",
                    "3-ply disposable surgical face masks, box of 50.",
                    "399.00", 120, "Personal Protection",
                    "https://commons.wikimedia.org/wiki/Special:FilePath/3M%20Surgical%20N95%20Respirator.png?width=500");
        }
    }

    private void addProductIfMissing(Connection conn, String name, String description, String price,
                                      int stockQty, String category, String imageUrl) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM products WHERE name = ? AND seller_id = 2";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, name);
            try (ResultSet rs = check.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return; // already added on a previous startup
                }
            }
        }

        String insertSql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) " +
                "VALUES (2, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            insert.setString(1, name);
            insert.setString(2, description);
            insert.setBigDecimal(3, new java.math.BigDecimal(price));
            insert.setInt(4, stockQty);
            insert.setString(5, category);
            insert.setString(6, imageUrl);
            insert.executeUpdate();
        }
        log.info("YogaMart: added new sample product '{}'.", name);
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
