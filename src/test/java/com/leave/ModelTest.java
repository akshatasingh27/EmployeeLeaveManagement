package com.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    @DisplayName("Employee model role checks work correctly")
    void testEmployeeRoleMethods() {
        Employee admin = new Employee();
        admin.setRole("ADMIN");
        assertTrue(admin.isAdmin());
        assertFalse(admin.isEmployee());

        Employee emp = new Employee();
        emp.setRole("EMPLOYEE");
        assertTrue(emp.isEmployee());
        assertFalse(emp.isAdmin());
    }

    @Test
    @DisplayName("LeaveRequest calculates duration accurately (inclusive of start and end dates)")
    void testLeaveDurationCalculation() {
        LeaveRequest req = new LeaveRequest();
        LocalDate start = LocalDate.of(2026, 11, 10);
        LocalDate end = LocalDate.of(2026, 11, 12); // 10th, 11th, 12th = 3 days

        req.setStartDate(Date.valueOf(start));
        req.setEndDate(Date.valueOf(end));

        assertEquals(3, req.getDurationInDays(), "Duration must be inclusive (3 days)");

        // 1-day leave test
        req.setEndDate(Date.valueOf(start));
        assertEquals(1, req.getDurationInDays(), "Same-day leave must equal 1 day");
    }

    @Test
    @DisplayName("LeaveRequest status helper methods work correctly")
    void testLeaveStatusHelpers() {
        LeaveRequest req = new LeaveRequest();
        req.setStatus("PENDING");
        assertTrue(req.isPending());
        assertFalse(req.isApproved());
        assertFalse(req.isRejected());

        req.setStatus("APPROVED");
        assertTrue(req.isApproved());
        assertFalse(req.isPending());

        req.setStatus("REJECTED");
        assertTrue(req.isRejected());
    }
}
