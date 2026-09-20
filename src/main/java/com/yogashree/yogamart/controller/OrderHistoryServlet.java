package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.CartDAO;
import com.yogashree.yogamart.dao.CartDAOImpl;
import com.yogashree.yogamart.dao.OrderDAO;
import com.yogashree.yogamart.dao.OrderDAOImpl;
import com.yogashree.yogamart.dto.SellerOrderLine;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.model.Order;
import com.yogashree.yogamart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * F6 — order history. GET /orders is the buyer's own past orders
 * (BUYER role only); GET /seller/orders is a seller's incoming
 * orders across all their products (SELLER role only). Viewing an
 * individual past order reuses the existing /order-confirmation page
 * (CheckoutServlet), which already has the buyer-ownership check —
 * no need to duplicate that logic here.
 */
@WebServlet(urlPatterns = {"/orders", "/seller/orders"})
public class OrderHistoryServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute(AppContextListener.DATASOURCE_ATTR);
        OrderDAO orderDAO = new OrderDAOImpl(ds);
        CartDAO cartDAO = new CartDAOImpl(ds);
        this.orderService = new OrderService(orderDAO, cartDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (int) req.getSession().getAttribute("userId");
        String role = (String) req.getSession().getAttribute("userRole");
        String path = req.getServletPath();

        try {
            if ("/seller/orders".equals(path)) {
                if (!"SELLER".equals(role)) {
                    resp.sendRedirect(req.getContextPath() + "/products");
                    return;
                }
                List<SellerOrderLine> lines = orderService.getSellerOrderLines(userId);
                req.setAttribute("orderLines", lines);
                req.getRequestDispatcher("/WEB-INF/jsp/seller-orders.jsp").forward(req, resp);
            } else {
                if (!"BUYER".equals(role)) {
                    resp.sendRedirect(req.getContextPath() + "/products");
                    return;
                }
                List<Order> orders = orderService.getOrdersForBuyer(userId);
                req.setAttribute("orders", orders);
                req.getRequestDispatcher("/WEB-INF/jsp/orders.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Failed to load order history", e);
        }
    }
}
