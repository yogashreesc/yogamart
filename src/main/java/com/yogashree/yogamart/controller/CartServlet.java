package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.CartDAO;
import com.yogashree.yogamart.dao.CartDAOImpl;
import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.dao.ProductDAOImpl;
import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.service.CartService;

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

/**
 * F4 — cart. Routed by getServletPath(), same pattern as
 * SellerProductServlet. Session check inline for now (moves to
 * AuthFilter once more protected routes exist).
 */
@WebServlet(urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove"})
public class CartServlet extends HttpServlet {

    private CartService cartService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute(AppContextListener.DATASOURCE_ATTR);
        CartDAO cartDAO = new CartDAOImpl(ds);
        ProductDAO productDAO = new ProductDAOImpl(ds);
        this.cartService = new CartService(cartDAO, productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer userId = requireLogin(req, resp);
        if (userId == null) {
            return;
        }
        renderCart(req, resp, userId, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer userId = requireLogin(req, resp);
        if (userId == null) {
            return;
        }

        String path = req.getServletPath();
        switch (path) {
            case "/cart/add":
                handleAdd(req, resp, userId);
                break;
            case "/cart/update":
                handleUpdate(req, resp, userId);
                break;
            case "/cart/remove":
                handleRemove(req, resp, userId);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
                Object role = req.getSession().getAttribute("userRole");
if (!"BUYER".equals(role)) {
    resp.sendRedirect(req.getContextPath() + "/products");
    return;
}
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int quantity = parseQuantity(req.getParameter("quantity"));
            cartService.addToCart(userId, productId, quantity);
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (ValidationException e) {
            // Send them back to the product page with the error rather
            // than a bare redirect, so they see why the add failed.
            try {
                resp.sendRedirect(req.getContextPath() + "/products/view?id=" +
                        req.getParameter("productId") + "&cartError=" +
                        java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException ignored) {
                resp.sendRedirect(req.getContextPath() + "/products");
            }
        } catch (SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        try {
            int cartItemId = Integer.parseInt(req.getParameter("cartItemId"));
            int quantity = parseQuantity(req.getParameter("quantity"));
            cartService.updateQuantity(userId, cartItemId, quantity);
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (ValidationException e) {
            renderCart(req, resp, userId, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException("Failed to update cart", e);
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        try {
            int cartItemId = Integer.parseInt(req.getParameter("cartItemId"));
            cartService.removeItem(userId, cartItemId);
        } catch (NumberFormatException | ValidationException | SQLException e) {
            // Same approach as SellerProductServlet's delete: fall through
            // to the cart view either way, nothing stale will show.
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void renderCart(HttpServletRequest req, HttpServletResponse resp, int userId, String error)
            throws ServletException, IOException {
        try {
            List<CartLineItem> items = cartService.getCartItems(userId);
            BigDecimal total = cartService.getCartTotal(items);
            req.setAttribute("cartItems", items);
            req.setAttribute("cartTotal", total);
            if (error != null) {
                req.setAttribute("error", error);
            }
            req.getRequestDispatcher("/WEB-INF/jsp/cart.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load cart", e);
        }
    }

    private int parseQuantity(String raw) {
        try {
            int qty = Integer.parseInt(raw);
            return Math.max(qty, 1);
        } catch (NumberFormatException | NullPointerException e) {
            return 1;
        }
    }

    private Integer requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return (Integer) req.getSession().getAttribute("userId");
    }
}
