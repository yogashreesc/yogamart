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
import java.util.Optional;

/**
 * F4 — product detail page. Add to Cart lives here rather than on the
 * browse grid, so the buyer sees full product info (description,
 * live stock) before committing to a quantity.
 */
@WebServlet(urlPatterns = {"/products/view"})
public class ProductDetailServlet extends HttpServlet {

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

        String idParam = req.getParameter("id");
        int productId;
        try {
            productId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            Optional<Product> product = productService.getById(productId);
            if (product.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }
            req.setAttribute("product", product.get());
            req.getRequestDispatcher("/WEB-INF/jsp/product-detail.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load product", e);
        }
    }
}
