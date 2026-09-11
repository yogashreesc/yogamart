package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.dao.ProductDAOImpl;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * F2 (seller product CRUD) — create, list, edit, delete. Routed by
 * getServletPath() since all four actions share the same session-check
 * and ProductService wiring.
 *
 * Session check is inline here, same pattern as ProductServlet — both
 * will move to a shared AuthFilter once more protected routes exist.
 */
@WebServlet(urlPatterns = {"/seller/add-product", "/seller/products", "/seller/edit-product", "/seller/delete-product"})
public class SellerProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute(AppContextListener.DATASOURCE_ATTR);
        ProductDAO productDAO = new ProductDAOImpl(ds);
        this.productService = new ProductService(productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isSeller(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        switch (path) {
            case "/seller/products":
                handleListProducts(req, resp);
                break;
            case "/seller/edit-product":
                handleShowEditForm(req, resp);
                break;
            default:
                req.getRequestDispatcher("/WEB-INF/jsp/add-product.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isSeller(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        switch (path) {
            case "/seller/edit-product":
                handleUpdateProduct(req, resp);
                break;
            case "/seller/delete-product":
                handleDeleteProduct(req, resp);
                break;
            default:
                handleAddProduct(req, resp);
        }
    }

    private void handleAddProduct(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sellerId = (int) req.getSession().getAttribute("userId");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");

        try {
            BigDecimal price = parseBigDecimal(priceStr);
            int stockQty = parseInt(stockStr);

            productService.addProduct(sellerId, name, description, price, stockQty, category, imageUrl);

            req.setAttribute("success", "Product added successfully.");
            req.getRequestDispatcher("/WEB-INF/jsp/add-product.jsp").forward(req, resp);
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/add-product.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Something went wrong while saving the product. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/add-product.jsp").forward(req, resp);
        }
    }

    private void handleListProducts(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sellerId = (int) req.getSession().getAttribute("userId");
        try {
            List<com.yogashree.yogamart.model.Product> products = productService.getSellerProducts(sellerId);
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/jsp/seller-products.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load your products. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/seller-products.jsp").forward(req, resp);
        }
    }

    private void handleShowEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sellerId = (int) req.getSession().getAttribute("userId");
        try {
            int productId = parseInt(req.getParameter("id"));
            Optional<com.yogashree.yogamart.model.Product> product = productService.getById(productId);

            if (product.isEmpty() || product.get().getSellerId() != sellerId) {
                resp.sendRedirect(req.getContextPath() + "/seller/products");
                return;
            }

            req.setAttribute("product", product.get());
            req.getRequestDispatcher("/WEB-INF/jsp/edit-product.jsp").forward(req, resp);
        } catch (ValidationException | SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/seller/products");
        }
    }

    private void handleUpdateProduct(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sellerId = (int) req.getSession().getAttribute("userId");
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");

        try {
            int productId = parseInt(idStr);
            BigDecimal price = parseBigDecimal(priceStr);
            int stockQty = parseInt(stockStr);

            productService.updateProduct(productId, sellerId, name, description, price, stockQty, category, imageUrl);

            resp.sendRedirect(req.getContextPath() + "/seller/products");
        } catch (ValidationException e) {
            try {
                Optional<com.yogashree.yogamart.model.Product> product = productService.getById(parseInt(idStr));
                req.setAttribute("product", product.orElse(null));
            } catch (Exception ignored) {
                // fall through with no product attribute; JSP handles null gracefully
            }
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/edit-product.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Something went wrong while updating the product. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/edit-product.jsp").forward(req, resp);
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int sellerId = (int) req.getSession().getAttribute("userId");
        try {
            int productId = parseInt(req.getParameter("id"));
            productService.deleteProduct(productId, sellerId);
        } catch (ValidationException | SQLException e) {
            // Deliberately silent: if delete fails (already gone, or not owned by
            // this seller), the list view is still the correct place to land —
            // no stale product will appear in it either way.
        }
        resp.sendRedirect(req.getContextPath() + "/seller/products");
    }

    private boolean isSeller(HttpServletRequest req) {
        if (req.getSession(false) == null || req.getSession().getAttribute("userId") == null) {
            return false;
        }
        Object role = req.getSession().getAttribute("userRole");
        return "SELLER".equals(role) || "ADMIN".equals(role);
    }

    private BigDecimal parseBigDecimal(String s) throws ValidationException {
        try {
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            throw new ValidationException("Price must be a valid number.");
        }
    }

    private int parseInt(String s) throws ValidationException {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            throw new ValidationException("Stock quantity must be a valid whole number.");
        }
    }
}
