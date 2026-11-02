package com.leave.dao;

import com.leave.DBConnection;
import com.leave.LeaveRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for the `leave_requests` table.
 * 
 * Handles all database operations for:
 * - Submitting leave applications
 * - Retrieving an employee's personal leave history
 * - Retrieving all leave applications for Administrator review
 * - Approving and rejecting pending leave requests
 * - Aggregating statistical metrics for dashboards
 */
public class LeaveRequestDAO {

    /**
     * Inserts a new leave request with PENDING status.
     * Uses PreparedStatement to prevent SQL injection.
     */
    public boolean createLeaveRequest(LeaveRequest request) throws SQLException {
        String sql = "INSERT INTO leave_requests (employee_id, leave_type, start_date, end_date, reason, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, request.getEmployeeId());
            ps.setString(2, request.getLeaveType());
            ps.setDate(3, request.getStartDate());
            ps.setDate(4, request.getEndDate());
            ps.setString(5, request.getReason());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves all leave requests for a single employee, sorted newest first.
     */
    public List<LeaveRequest> getRequestsByEmployeeId(int employeeId) throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT id, employee_id, leave_type, start_date, end_date, reason, status, " +
                     "admin_remark, created_at, updated_at " +
                     "FROM leave_requests WHERE employee_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToLeaveRequest(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all leave requests across the company with employee details joined.
     * Used exclusively on the Administrator dashboard.
     */
    public List<LeaveRequest> getAllRequestsWithEmployeeDetails() throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT lr.id, lr.employee_id, lr.leave_type, lr.start_date, lr.end_date, " +
                     "lr.reason, lr.status, lr.admin_remark, lr.created_at, lr.updated_at, " +
                     "e.name AS employee_name, e.email AS employee_email, e.department " +
                     "FROM leave_requests lr " +
                     "JOIN employees e ON lr.employee_id = e.id " +
                     "ORDER BY lr.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveRequest req = mapRowToLeaveRequest(rs);
                req.setEmployeeName(rs.getString("employee_name"));
                req.setEmployeeEmail(rs.getString("employee_email"));
                req.setDepartment(rs.getString("department"));
                list.add(req);
            }
        }
        return list;
    }

    /**
     * Finds a single leave request by ID.
     */
    public LeaveRequest getRequestById(int id) throws SQLException {
        String sql = "SELECT lr.id, lr.employee_id, lr.leave_type, lr.start_date, lr.end_date, " +
                     "lr.reason, lr.status, lr.admin_remark, lr.created_at, lr.updated_at, " +
                     "e.name AS employee_name, e.email AS employee_email, e.department " +
                     "FROM leave_requests lr " +
                     "JOIN employees e ON lr.employee_id = e.id " +
                     "WHERE lr.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LeaveRequest req = mapRowToLeaveRequest(rs);
                    req.setEmployeeName(rs.getString("employee_name"));
                    req.setEmployeeEmail(rs.getString("employee_email"));
                    req.setDepartment(rs.getString("department"));
                    return req;
                }
            }
        }
        return null;
    }

    /**
     * Updates the status of a leave request (APPROVED or REJECTED) with an optional admin remark.
     * Enforces that the request must currently be 'PENDING' to prevent repeated/conflicting processing.
     * 
     * @return true if updated, false if request not found or not in PENDING state
     */
    public boolean updateStatus(int requestId, String newStatus, String adminRemark) throws SQLException {
        if (!"APPROVED".equalsIgnoreCase(newStatus) && !"REJECTED".equalsIgnoreCase(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        String sql = "UPDATE leave_requests " +
                     "SET status = ?, admin_remark = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.toUpperCase());
            ps.setString(2, adminRemark);
            ps.setInt(3, requestId);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Retrieves summary counts for an individual employee:
     * total, pending, approved, rejected.
     */
    public Map<String, Integer> getEmployeeStats(int employeeId) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("approved", 0);
        stats.put("rejected", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM leave_requests WHERE employee_id = ? GROUP BY status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                int total = 0;
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("cnt");
                    total += count;

                    if ("PENDING".equalsIgnoreCase(status)) {
                        stats.put("pending", count);
                    } else if ("APPROVED".equalsIgnoreCase(status)) {
                        stats.put("approved", count);
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        stats.put("rejected", count);
                    }
                }
                stats.put("total", total);
            }
        }
        return stats;
    }

    /**
     * Retrieves overall organization summary counts for Administrator:
     * total requests, pending, approved, rejected.
     */
    public Map<String, Integer> getAdminStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("approved", 0);
        stats.put("rejected", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM leave_requests GROUP BY status";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int total = 0;
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("cnt");
                total += count;

                if ("PENDING".equalsIgnoreCase(status)) {
                    stats.put("pending", count);
                } else if ("APPROVED".equalsIgnoreCase(status)) {
                    stats.put("approved", count);
                } else if ("REJECTED".equalsIgnoreCase(status)) {
                    stats.put("rejected", count);
                }
            }
            stats.put("total", total);
        }
        return stats;
    }

    private LeaveRequest mapRowToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest req = new LeaveRequest();
        req.setId(rs.getInt("id"));
        req.setEmployeeId(rs.getInt("employee_id"));
        req.setLeaveType(rs.getString("leave_type"));
        req.setStartDate(rs.getDate("start_date"));
        req.setEndDate(rs.getDate("end_date"));
        req.setReason(rs.getString("reason"));
        req.setStatus(rs.getString("status"));
        req.setAdminRemark(rs.getString("admin_remark"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            req.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException ignored) {
            // Updated_at column may not exist in minimal queries
        }
        return req;
    }
}
