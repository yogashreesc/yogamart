package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.OrderDAO;
import com.yogashree.yogamart.dao.OrderDAOImpl;
import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.dao.ProductDAOImpl;
import com.yogashree.yogamart.dao.UserDAO;
import com.yogashree.yogamart.dao.UserDAOImpl;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * F7 — admin. GET /admin/users, GET /admin/orders, GET /admin/products
 * (view-all pages), POST /admin/products/delete (moderation). Every
 * route here is gated to ADMIN role — not just hidden in the nav.
 */
@WebServlet(urlPatterns = {"/admin/users", "/admin/orders", "/admin/products", "/admin/products/delete"})
public class AdminServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute(AppContextListener.DATASOURCE_ATTR);
        UserDAO userDAO = new UserDAOImpl(ds);
        OrderDAO orderDAO = new OrderDAOImpl(ds);
        ProductDAO productDAO = new ProductDAOImpl(ds);
        this.adminService = new AdminService(userDAO, orderDAO, productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireAdmin(req, resp)) {
            return;
        }

        String path = req.getServletPath();
        try {
            switch (path) {
                case "/admin/users":
                    req.setAttribute("users", adminService.getAllUsers());
                    req.getRequestDispatcher("/WEB-INF/jsp/admin-users.jsp").forward(req, resp);
                    break;
                case "/admin/orders":
                    req.setAttribute("orders", adminService.getAllOrders());
                    req.getRequestDispatcher("/WEB-INF/jsp/admin-orders.jsp").forward(req, resp);
                    break;
                case "/admin/products":
                    req.setAttribute("products", adminService.getAllProducts());
                    req.getRequestDispatcher("/WEB-INF/jsp/admin-products.jsp").forward(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } catch (SQLException e) {
            throw new ServletException("Failed to load admin data", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!requireAdmin(req, resp)) {
            return;
        }

        if ("/admin/products/delete".equals(req.getServletPath())) {
            try {
                int productId = Integer.parseInt(req.getParameter("id"));
                adminService.removeListing(productId);
            } catch (NumberFormatException | ValidationException | SQLException e) {
                // Same approach as seller delete: fall through to the
                // list view either way, nothing stale will show.
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (!"ADMIN".equals(req.getSession().getAttribute("userRole"))) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return false;
        }
        return true;
    }
}
