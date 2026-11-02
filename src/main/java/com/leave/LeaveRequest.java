package com.leave;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

/**
 * Model class representing a Leave Request application.
 * 
 * In MVC architecture, this Model stores the leave details submitted by an employee
 * and any processing decision made by an administrator.
 */
public class LeaveRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int employeeId;
    private String employeeName;    // Populated from JOIN query for Admin dashboard
    private String employeeEmail;   // Populated from JOIN query for Admin dashboard
    private String department;      // Populated from JOIN query
    private String leaveType;       // e.g. "Medical Leave", "Casual Leave", "Earned Leave"
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;          // "PENDING", "APPROVED", "REJECTED"
    private String adminRemark;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public LeaveRequest() {
    }

    public LeaveRequest(int id, int employeeId, String leaveType, Date startDate, Date endDate,
                        String reason, String status, String adminRemark, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.adminRemark = adminRemark;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Calculates the total calendar days of the leave (inclusive of start and end date).
     */
    public long getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(this.status);
    }
}
