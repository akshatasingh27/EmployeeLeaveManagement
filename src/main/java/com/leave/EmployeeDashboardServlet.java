package com.leave;

import com.leave.dao.LeaveRequestDAO;
import com.leave.util.CsrfUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Controller Servlet for Employee Dashboard.
 * 
 * Fetches real-time leave metrics and recent requests from the MySQL database
 * for the logged-in employee and forwards to dashboard.jsp.
 */
@WebServlet(name = "EmployeeDashboardServlet", urlPatterns = {"/dashboard"})
public class EmployeeDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private LeaveRequestDAO leaveRequestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        leaveRequestDAO = new LeaveRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Employee user = (Employee) session.getAttribute("user");

        // Role verification: Administrators belong in the admin dashboard
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // Ensure CSRF token is available
        CsrfUtil.ensureToken(session);

        try {
            // Retrieve dynamic summary cards data from database
            Map<String, Integer> stats = leaveRequestDAO.getEmployeeStats(user.getId());

            // Retrieve recent leave requests
            List<LeaveRequest> allRequests = leaveRequestDAO.getRequestsByEmployeeId(user.getId());
            List<LeaveRequest> recentRequests = allRequests.size() > 5 ? allRequests.subList(0, 5) : allRequests;

            request.setAttribute("stats", stats);
            request.setAttribute("recentRequests", recentRequests);

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load dashboard data from database.");
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
