package com.leave.dao;

import com.leave.DBConnection;
import com.leave.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object (DAO) for the `employees` table.
 * 
 * In a college viva, explain:
 * 1. The DAO pattern separates low-level database operations (SQL queries, connections)
 *    from high-level business logic (servlets).
 * 2. PreparedStatement uses parameterized queries (e.g. `WHERE email = ?`) which compiles
 *    the SQL structure in advance and treats parameters strictly as data. This completely
 *    prevents SQL Injection attacks.
 * 3. Try-with-resources automatically closes Connection, PreparedStatement, and ResultSet,
 *    preventing memory leaks and connection pool exhaustion.
 */
public class EmployeeDAO {

    /**
     * Finds an employee by their unique email address.
     * 
     * @param email User email
     * @return Employee object if found, or null if not found
     * @throws SQLException on database error
     */
    public Employee findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, department, created_at " +
                     "FROM employees WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmployee(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds an employee by their primary key ID.
     */
    public Employee findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, department, created_at " +
                     "FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmployee(rs);
                }
            }
        }
        return null;
    }

    /**
     * Counts total registered employees in the organization (excluding pure ADMIN or total).
     */
    public int getTotalEmployeesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM employees WHERE role = 'EMPLOYEE'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Inserts a new employee into the database.
     */
    public boolean createEmployee(Employee emp) throws SQLException {
        String sql = "INSERT INTO employees (name, email, password_hash, role, department) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPasswordHash());
            ps.setString(4, emp.getRole() != null ? emp.getRole() : "EMPLOYEE");
            ps.setString(5, emp.getDepartment() != null ? emp.getDepartment() : "General");

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        emp.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to map a single ResultSet row into an Employee object.
     */
    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getInt("id"));
        emp.setName(rs.getString("name"));
        emp.setEmail(rs.getString("email"));
        emp.setPasswordHash(rs.getString("password_hash"));
        emp.setRole(rs.getString("role"));
        emp.setDepartment(rs.getString("department"));
        emp.setCreatedAt(rs.getTimestamp("created_at"));
        return emp;
    }
}
