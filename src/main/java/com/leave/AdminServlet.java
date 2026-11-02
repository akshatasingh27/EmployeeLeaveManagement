package com.leave;

import com.leave.dao.EmployeeDAO;
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
 * Controller Servlet for Administrator Operations.
 * 
 * In MVC architecture:
 * - Controls the Administrator Dashboard view (GET).
 * - Processes Leave Approvals and Rejections (POST).
 * - Enforces strict Role-Based Access Control (RBAC): only users with role="ADMIN" are permitted.
 * - Enforces state validation: only PENDING requests can be approved or rejected.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard", "/admin/leave-action"})
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EmployeeDAO employeeDAO;
    private LeaveRequestDAO leaveRequestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        employeeDAO = new EmployeeDAO();
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

        // Role-Based Access Control Check
        if (!user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator privileges required.");
            return;
        }

        CsrfUtil.ensureToken(session);

        try {
            // Retrieve organization-wide metrics
            Map<String, Integer> stats = leaveRequestDAO.getAdminStats();
            int totalEmployees = employeeDAO.getTotalEmployeesCount();

            // Retrieve all leave requests with employee details
            List<LeaveRequest> allRequests = leaveRequestDAO.getAllRequestsWithEmployeeDetails();

            request.setAttribute("stats", stats);
            request.setAttribute("totalEmployees", totalEmployees);
            request.setAttribute("leaveRequests", allRequests);

            // Pop any flash messages stored in session
            String flashMessage = (String) session.getAttribute("flashMessage");
            String flashType = (String) session.getAttribute("flashType");
            if (flashMessage != null) {
                request.setAttribute("flashMessage", flashMessage);
                request.setAttribute("flashType", flashType);
                session.removeAttribute("flashMessage");
                session.removeAttribute("flashType");
            }

            request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error occurred while loading administrator dashboard.");
            request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Employee user = (Employee) session.getAttribute("user");

        // Role-Based Access Control Check
        if (!user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only administrators can approve or reject leaves.");
            return;
        }

        // 1. Verify CSRF Token
        if (!CsrfUtil.isValidToken(request)) {
            session.setAttribute("flashMessage", "Security token validation failed (CSRF). Action aborted.");
            session.setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 2. Read request parameters
        String requestIdStr = request.getParameter("requestId");
        String action = request.getParameter("action"); // "APPROVE" or "REJECT"
        String adminRemark = request.getParameter("adminRemark");

        if (requestIdStr == null || action == null) {
            session.setAttribute("flashMessage", "Missing required parameters.");
            session.setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdStr.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("flashMessage", "Invalid Request ID.");
            session.setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        String targetStatus;
        if ("APPROVE".equalsIgnoreCase(action)) {
            targetStatus = "APPROVED";
        } else if ("REJECT".equalsIgnoreCase(action)) {
            targetStatus = "REJECTED";
        } else {
            session.setAttribute("flashMessage", "Invalid action requested.");
            session.setAttribute("flashType", "danger");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        if (adminRemark != null) {
            adminRemark = adminRemark.trim();
        }

        // 3. Process status change in MySQL
        try {
            boolean updated = leaveRequestDAO.updateStatus(requestId, targetStatus, adminRemark);
            if (updated) {
                session.setAttribute("flashMessage", "Leave Request #" + requestId + " successfully " + targetStatus.toLowerCase() + ".");
                session.setAttribute("flashType", "success");
            } else {
                session.setAttribute("flashMessage", "Action failed: Request #" + requestId + " was either not found or is already processed.");
                session.setAttribute("flashType", "warning");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("flashMessage", "Database error occurred while processing leave request #" + requestId);
            session.setAttribute("flashType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
