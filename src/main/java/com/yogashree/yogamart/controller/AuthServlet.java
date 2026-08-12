package com.yogashree.yogamart.controller;

import com.yogashree.yogamart.dao.UserDAO;
import com.yogashree.yogamart.dao.UserDAOImpl;
import com.yogashree.yogamart.exception.AuthenticationException;
import com.yogashree.yogamart.exception.DuplicateEmailException;
import com.yogashree.yogamart.exception.ValidationException;
import com.yogashree.yogamart.listener.AppContextListener;
import com.yogashree.yogamart.model.User;
import com.yogashree.yogamart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Thin controller — HTTP orchestration only, no business logic and no SQL
 * (Section 12 SOLID rule). Delegates everything to AuthService.
 *
 * Routes:
 *   GET  /register        -> show registration form
 *   POST /register         -> create account
 *   GET  /login             -> show login form
 *   POST /login              -> authenticate, start session
 *   POST /logout           -> invalidate session
 */
@WebServlet(urlPatterns = {"/register", "/login", "/logout"})
public class AuthServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute(AppContextListener.DATASOURCE_ATTR);
        UserDAO userDAO = new UserDAOImpl(ds);
        this.authService = new AuthService(userDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/register".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        } else if ("/login".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/register":
                handleRegister(req, resp);
                break;
            case "/login":
                handleLogin(req, resp);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try {
            authService.register(name, email, password, role);
            req.setAttribute("success", "Account created. Please log in.");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } catch (ValidationException | DuplicateEmailException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Something went wrong. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = authService.login(email, password);

            // Regenerate session ID on login per Section 2, rule 3.
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(30 * 60); // 30-minute explicit timeout

            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (AuthenticationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Something went wrong. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
