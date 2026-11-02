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
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Controller Servlet for Leave Application Submission.
 * 
 * In MVC architecture:
 * - Controls the leave application form workflow (GET) and processing (POST).
 * - Implements strict server-side validation.
 * - Prevents parameter tampering by binding the leave request directly to the
 *   authenticated session employee ID.
 */
@WebServlet(name = "LeaveRequestServlet", urlPatterns = {"/apply-leave"})
public class LeaveRequestServlet extends HttpServlet {
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

        CsrfUtil.ensureToken(session);
        request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
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
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 1. Verify CSRF Token to protect against cross-site forged requests
        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("errorMessage", "Security verification failed (Invalid CSRF Token). Please submit the form again.");
            request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
            return;
        }

        // 2. Read and sanitize form parameters
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        // Retain values in case of validation failure
        request.setAttribute("selectedLeaveType", leaveType);
        request.setAttribute("enteredStartDate", startDateStr);
        request.setAttribute("enteredEndDate", endDateStr);
        request.setAttribute("enteredReason", reason);

        // 3. Server-side validation: Required fields
        if (leaveType == null || leaveType.trim().isEmpty() ||
            startDateStr == null || startDateStr.trim().isEmpty() ||
            endDateStr == null || endDateStr.trim().isEmpty() ||
            reason == null || reason.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required. Please complete the form.");
            request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
            return;
        }

        // 4. Server-side validation: Date parsing & date sequence
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr.trim());
            endDate = LocalDate.parse(endDateStr.trim());
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid date format. Please use YYYY-MM-DD.");
            request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
            return;
        }

        // End date cannot be before start date
        if (endDate.isBefore(startDate)) {
            request.setAttribute("errorMessage", "End date cannot be earlier than start date.");
            request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
            return;
        }

        // Reason length check
        if (reason.trim().length() < 5) {
            request.setAttribute("errorMessage", "Please provide a valid reason (minimum 5 characters).");
            request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
            return;
        }

        // 5. Construct LeaveRequest model
        // SECURITY REQUIREMENT: Never trust client parameters for employee_id!
        // Always bind to user.getId() from the authenticated server session.
        LeaveRequest newRequest = new LeaveRequest();
        newRequest.setEmployeeId(user.getId());
        newRequest.setLeaveType(leaveType.trim());
        newRequest.setStartDate(Date.valueOf(startDate));
        newRequest.setEndDate(Date.valueOf(endDate));
        newRequest.setReason(reason.trim());
        newRequest.setStatus("PENDING");

        // 6. Save in MySQL via LeaveRequestDAO
        try {
            boolean created = leaveRequestDAO.createLeaveRequest(newRequest);
            if (created) {
                // Success: redirect to Leave Status view with a success indicator
                response.sendRedirect(request.getContextPath() + "/leave-status?submitted=true");
            } else {
                request.setAttribute("errorMessage", "Failed to submit leave request. Please try again.");
                request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error occurred while saving your leave application.");
            request.getRequestDispatcher("/apply-leave.jsp").forward(request, response);
        }
    }
}
