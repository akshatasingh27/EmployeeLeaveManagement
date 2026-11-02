package com.leave;

import com.leave.dao.LeaveRequestDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller Servlet for Employee Leave History and Status.
 * 
 * In MVC architecture:
 * - Controls the retrieval of personal leave history for the authenticated employee.
 * - Enforces that an employee can ONLY see their own records (Isolation/Authorization).
 * - Forwards data to leave-status.jsp.
 */
@WebServlet(name = "LeaveStatusServlet", urlPatterns = {"/leave-status"})
public class LeaveStatusServlet extends HttpServlet {
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
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            // Retrieve only the logged-in employee's leave records
            List<LeaveRequest> leaveList = leaveRequestDAO.getRequestsByEmployeeId(user.getId());
            request.setAttribute("leaveList", leaveList);

            // Forward to the JSP view
            request.getRequestDispatcher("/leave-status.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load leave history from database.");
            request.getRequestDispatcher("/leave-status.jsp").forward(request, response);
        }
    }
}
