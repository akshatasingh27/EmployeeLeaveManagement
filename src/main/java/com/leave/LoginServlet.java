package com.leave;

import com.leave.dao.EmployeeDAO;
import com.leave.util.CsrfUtil;
import com.leave.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller Servlet for User Authentication (Login).
 * 
 * In MVC architecture:
 * - Acts as the Controller: intercepts HTTP GET and POST requests for /login.
 * - Coordinates with Model (EmployeeDAO, PasswordUtil) to authenticate credentials.
 * - Decides which View (JSP or redirect) to render next.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            Employee user = (Employee) session.getAttribute("user");
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
            return;
        }

        // Make sure a fresh session and CSRF token are available for the login form
        HttpSession newSession = request.getSession(true);
        CsrfUtil.ensureToken(newSession);

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Server-side validation for empty fields
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and Password are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        try {
            Employee employee = employeeDAO.findByEmail(email);

            if (employee != null && PasswordUtil.verifyPassword(password, employee.getPasswordHash())) {
                // Authentication successful!
                // Invalidate old session to prevent Session Fixation vulnerability
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }

                // Create fresh secure session
                HttpSession session = request.getSession(true);
                session.setAttribute("user", employee);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout
                CsrfUtil.ensureToken(session);

                // Role-based redirection
                if (employee.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
            } else {
                // Invalid credentials
                request.setAttribute("errorMessage", "Invalid email or password. Please try again.");
                request.setAttribute("enteredEmail", email);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database connection error. Please verify MySQL service is running.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
