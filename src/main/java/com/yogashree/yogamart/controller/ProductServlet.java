package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.ProductDAO;
import com.yogashree.yogamart.dao.ProductDAOImpl;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.model.Product;
import com.yogashree.yogamart.service.ProductService;

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
 * Thin controller for browsing/searching products (F3). Requires an
 * active session — enforced here for now; moves to AuthFilter once
 * more protected routes exist (Week 3+).
 */
@WebServlet(urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

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

        if (req.getSession(false) == null || req.getSession().getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("q");
        String category = req.getParameter("category");

        try {
            List<Product> products = productService.browse(keyword, category);
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/jsp/products.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load products", e);
        }
    }
}
