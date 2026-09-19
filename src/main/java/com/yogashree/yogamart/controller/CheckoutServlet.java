package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.CartDAO;
import com.yogashree.yogamart.dao.CartDAOImpl;
import com.yogashree.yogamart.dao.OrderDAO;
import com.yogashree.yogamart.dao.OrderDAOImpl;
import com.yogashree.yogamart.dto.CartLineItem;
import com.yogashree.yogamart.dto.OrderLineItem;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.model.Order;
import com.yogashree.yogamart.service.CartService;
import com.yogashree.yogamart.service.OrderService;

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
 * F5 — checkout. GET /checkout shows the order summary (reusing the
 * cart contents, read-only); POST /checkout/confirm is the mock
 * payment confirmation step (Section 1's scope constraint: no real
 * payment gateway); GET /order-confirmation shows the result.
 */
@WebServlet(urlPatterns = {"/checkout", "/checkout/confirm", "/order-confirmation"})
public class CheckoutServlet extends HttpServlet {

    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute(AppContextListener.DATASOURCE_ATTR);
        CartDAO cartDAO = new CartDAOImpl(ds);
        com.yogashree.yogamart.dao.ProductDAO productDAO = new com.yogashree.yogamart.dao.ProductDAOImpl(ds);
        OrderDAO orderDAO = new OrderDAOImpl(ds);
        this.cartService = new CartService(cartDAO, productDAO);
        this.orderService = new OrderService(orderDAO, cartDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer userId = requireLogin(req, resp);
        if (userId == null) {
            return;
        }

        String path = req.getServletPath();
        if ("/order-confirmation".equals(path)) {
            handleShowConfirmation(req, resp, userId);
        } else {
            handleShowSummary(req, resp, userId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer userId = requireLogin(req, resp);
        if (userId == null) {
            return;
        }
        handleConfirm(req, resp, userId);
    }

    private void handleShowSummary(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        try {
            List<CartLineItem> items = cartService.getCartItems(userId);
            if (items.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            BigDecimal total = cartService.getCartTotal(items);
            req.setAttribute("cartItems", items);
            req.setAttribute("cartTotal", total);
            req.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load checkout summary", e);
        }
    }

    private void handleConfirm(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        try {
            int orderId = orderService.checkout(userId);
            resp.sendRedirect(req.getContextPath() + "/order-confirmation?orderId=" + orderId);
        } catch (ValidationException e) {
            // Stock changed, or cart was already empty — send back to
            // cart (not checkout) since the summary they confirmed is stale.
            resp.sendRedirect(req.getContextPath() + "/cart?error=" +
                    java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        } catch (SQLException e) {
            throw new ServletException("Failed to place order", e);
        }
    }

    private void handleShowConfirmation(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            Optional<Order> order = orderService.getOrder(orderId);

            // Ownership check: a buyer can only view their own order
            // confirmation, even if they guess another order's id.
            if (order.isEmpty() || order.get().getBuyerId() != userId) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }

            List<OrderLineItem> items = orderService.getOrderItems(orderId);
            req.setAttribute("order", order.get());
            req.setAttribute("orderItems", items);
            req.getRequestDispatcher("/WEB-INF/jsp/order-confirmation.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (SQLException e) {
            throw new ServletException("Failed to load order", e);
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
