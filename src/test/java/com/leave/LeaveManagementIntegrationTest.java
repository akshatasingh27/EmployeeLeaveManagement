package com.leave;

import com.leave.dao.EmployeeDAO;
import com.leave.dao.LeaveRequestDAO;
import com.leave.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End DAO and Database Integration Tests.
 * Uses an embedded in-memory H2 database configured in MySQL compatibility mode.
 * 
 * Verifies that:
 * 1. Employees and Leave Requests are correctly inserted.
 * 2. Prepared statements execute properly.
 * 3. Role-based records and statistics are calculated correctly.
 * 4. Administrator approval and rejection update status in the database.
 * 5. Double-processing of already processed requests is rejected.
 * 6. Record isolation ensures employees only see their own requests.
 */
public class LeaveManagementIntegrationTest {

    private static EmployeeDAO employeeDAO;
    private static LeaveRequestDAO leaveRequestDAO;

    @BeforeAll
    static void setupDatabase() throws Exception {
        // Point DBConnection to in-memory H2 database in MySQL mode
        String testUrl = "jdbc:h2:mem:employee_leave_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        DBConnection.setCustomConnectionDetails(testUrl, "sa", "", "org.h2.Driver");

        employeeDAO = new EmployeeDAO();
        leaveRequestDAO = new LeaveRequestDAO();

        // Create schema matching database.sql
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS leave_requests");
            stmt.execute("DROP TABLE IF EXISTS employees");

            stmt.execute("CREATE TABLE employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE', " +
                    "department VARCHAR(100) DEFAULT 'General', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE leave_requests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "employee_id INT NOT NULL, " +
                    "leave_type VARCHAR(50) NOT NULL, " +
                    "start_date DATE NOT NULL, " +
                    "end_date DATE NOT NULL, " +
                    "reason TEXT NOT NULL, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'PENDING', " +
                    "admin_remark TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE)");
        }
    }

    @Test
    @DisplayName("Employee Creation and Retrieval by Email works correctly")
    void testEmployeeCreationAndRetrieval() throws SQLException {
        Employee emp = new Employee();
        emp.setName("Alice Walker");
        emp.setEmail("alice@company.com");
        emp.setPasswordHash(PasswordUtil.hashPassword("Password@123"));
        emp.setRole("EMPLOYEE");
        emp.setDepartment("Mobile App Development");

        boolean created = employeeDAO.createEmployee(emp);
        assertTrue(created, "Employee should be successfully inserted");
        assertTrue(emp.getId() > 0, "Generated ID should be populated in model");

        Employee fetched = employeeDAO.findByEmail("alice@company.com");
        assertNotNull(fetched, "Should find employee by email");
        assertEquals("Alice Walker", fetched.getName());
        assertEquals("EMPLOYEE", fetched.getRole());
        assertEquals("Mobile App Development", fetched.getDepartment());
        assertTrue(PasswordUtil.verifyPassword("Password@123", fetched.getPasswordHash()));
    }

    @Test
    @DisplayName("Leave Request lifecycle: create -> pending -> admin approve -> verify isolation")
    void testLeaveRequestLifecycle() throws SQLException {
        // 1. Create two test employees
        Employee emp1 = new Employee(0, "Employee One", "emp1@test.com", PasswordUtil.hashPassword("Pass@1"), "EMPLOYEE", "Sales", null);
        Employee emp2 = new Employee(0, "Employee Two", "emp2@test.com", PasswordUtil.hashPassword("Pass@2"), "EMPLOYEE", "Marketing", null);
        employeeDAO.createEmployee(emp1);
        employeeDAO.createEmployee(emp2);

        // 2. Submit Leave Request for emp1
        LeaveRequest req1 = new LeaveRequest();
        req1.setEmployeeId(emp1.getId());
        req1.setLeaveType("Medical Leave");
        req1.setStartDate(Date.valueOf(LocalDate.of(2026, 12, 1)));
        req1.setEndDate(Date.valueOf(LocalDate.of(2026, 12, 3)));
        req1.setReason("Dental appointment and recovery.");

        boolean submitted = leaveRequestDAO.createLeaveRequest(req1);
        assertTrue(submitted, "Leave request submission should succeed");
        assertTrue(req1.getId() > 0, "Generated leave request ID should be assigned");

        // 3. Submit Leave Request for emp2
        LeaveRequest req2 = new LeaveRequest();
        req2.setEmployeeId(emp2.getId());
        req2.setLeaveType("Casual Leave");
        req2.setStartDate(Date.valueOf(LocalDate.of(2026, 12, 10)));
        req2.setEndDate(Date.valueOf(LocalDate.of(2026, 12, 11)));
        req2.setReason("Personal relocation work.");
        leaveRequestDAO.createLeaveRequest(req2);

        // 4. Verify Record Isolation: emp1 should ONLY see req1, NOT req2
        List<LeaveRequest> emp1List = leaveRequestDAO.getRequestsByEmployeeId(emp1.getId());
        assertEquals(1, emp1List.size(), "emp1 should only see 1 request");
        assertEquals(req1.getId(), emp1List.get(0).getId());
        assertEquals("PENDING", emp1List.get(0).getStatus());

        List<LeaveRequest> emp2List = leaveRequestDAO.getRequestsByEmployeeId(emp2.getId());
        assertEquals(1, emp2List.size(), "emp2 should only see 1 request");
        assertEquals(req2.getId(), emp2List.get(0).getId());

        // 5. Admin retrieves all requests across the organization
        List<LeaveRequest> allAdminRequests = leaveRequestDAO.getAllRequestsWithEmployeeDetails();
        assertTrue(allAdminRequests.size() >= 2, "Admin should see all employee requests");

        // 6. Admin approves req1
        boolean approved = leaveRequestDAO.updateStatus(req1.getId(), "APPROVED", "Approved by Manager. Take care.");
        assertTrue(approved, "Pending request should be approved");

        // Verify status change in DB
        LeaveRequest updatedReq1 = leaveRequestDAO.getRequestById(req1.getId());
        assertNotNull(updatedReq1);
        assertEquals("APPROVED", updatedReq1.getStatus());
        assertEquals("Approved by Manager. Take care.", updatedReq1.getAdminRemark());

        // 7. Security check: cannot re-approve or re-reject an already approved request (must be PENDING)
        boolean repeatAttempt = leaveRequestDAO.updateStatus(req1.getId(), "REJECTED", "Trying to reject already approved");
        assertFalse(repeatAttempt, "Updating an already processed request must return false");

        // 8. Admin rejects req2
        boolean rejected = leaveRequestDAO.updateStatus(req2.getId(), "REJECTED", "Team staffing shortage on selected dates.");
        assertTrue(rejected, "Pending request should be rejected");

        LeaveRequest updatedReq2 = leaveRequestDAO.getRequestById(req2.getId());
        assertNotNull(updatedReq2);
        assertEquals("REJECTED", updatedReq2.getStatus());
        assertEquals("Team staffing shortage on selected dates.", updatedReq2.getAdminRemark());

        // 9. Verify Statistics
        Map<String, Integer> emp1Stats = leaveRequestDAO.getEmployeeStats(emp1.getId());
        assertEquals(1, emp1Stats.get("total"));
        assertEquals(1, emp1Stats.get("approved"));
        assertEquals(0, emp1Stats.get("pending"));
        assertEquals(0, emp1Stats.get("rejected"));

        Map<String, Integer> emp2Stats = leaveRequestDAO.getEmployeeStats(emp2.getId());
        assertEquals(1, emp2Stats.get("total"));
        assertEquals(1, emp2Stats.get("rejected"));
        assertEquals(0, emp2Stats.get("pending"));
        assertEquals(0, emp2Stats.get("approved"));
    }

    @Test
    @DisplayName("Admin metrics and employee counts match database state")
    void testAdminMetricsAndEmployeeCounts() throws SQLException {
        int employeeCount = employeeDAO.getTotalEmployeesCount();
        assertTrue(employeeCount >= 2, "Active employee count must be at least 2");

        Map<String, Integer> adminStats = leaveRequestDAO.getAdminStats();
        assertNotNull(adminStats);
        assertTrue(adminStats.get("total") >= 2, "Total requests count should include all employee submissions");
        assertTrue(adminStats.containsKey("pending"));
        assertTrue(adminStats.containsKey("approved"));
        assertTrue(adminStats.containsKey("rejected"));
    }

    @Test
    @DisplayName("Employee lookup by primary key ID works correctly")
    void testFindEmployeeById() throws SQLException {
        Employee emp = new Employee(0, "Robert Tester", "tester.rob@company.com", PasswordUtil.hashPassword("Pass@123"), "EMPLOYEE", "DevOps", null);
        employeeDAO.createEmployee(emp);
        assertTrue(emp.getId() > 0);

        Employee found = employeeDAO.findById(emp.getId());
        assertNotNull(found);
        assertEquals("Robert Tester", found.getName());
        assertEquals("tester.rob@company.com", found.getEmail());
        assertEquals("DevOps", found.getDepartment());
    }
}
