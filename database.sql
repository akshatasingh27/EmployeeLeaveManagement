
-- =====================================================================
-- EMPLOYEE LEAVE MANAGEMENT SYSTEM
-- Technologies: Java, Servlets, JSP, MySQL
-- Database: employee_leave_management
-- =====================================================================

-- STEP 1: Create database
CREATE DATABASE IF NOT EXISTS employee_leave_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE employee_leave_management;

-- STEP 2: Drop existing tables
-- WARNING: This deletes existing employee and leave request records.
DROP TABLE IF EXISTS leave_requests;
DROP TABLE IF EXISTS employees;

-- STEP 3: Create employees table
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE',
    department VARCHAR(100) DEFAULT 'General',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- STEP 4: Create leave requests table
CREATE TABLE leave_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_remark TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),

    CONSTRAINT chk_dates
        CHECK (end_date >= start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- STEP 5: Insert administrator and employee accounts
-- Existing project password hashes are retained.
-- Verify password compatibility with the Java authentication code.

INSERT INTO employees
    (id, name, email, password_hash, role, department)
VALUES
(
    1,
    'System Administrator',
    'admin@company.com',
    '7b02aec37eaaa1e0cb000068cde59fdc2c61e3a6436735839ba77b77a830cb0b',
    'ADMIN',
    'Executive IT'
),
(
    2,
    'Akshata Singh',
    'akshata.singh@company.com',
    '8909a20bf98193b7a96d58ba56f60af9c7e86635585d3cad1c99a81d64ce7353',
    'EMPLOYEE',
    'Software Engineering'
),
(
    3,
    'Harshpreet Kaur',
    'harshpreet.kaur@company.com',
    '8909a20bf98193b7a96d58ba56f60af9c7e86635585d3cad1c99a81d64ce7353',
    'EMPLOYEE',
    'Quality Assurance'
),
(
    4,
    'Lakshay Mishra',
    'lakshay.mishra@company.com',
    '8909a20bf98193b7a96d58ba56f60af9c7e86635585d3cad1c99a81d64ce7353',
    'EMPLOYEE',
    'Human Resources'
);

-- STEP 6: Insert demonstration leave requests
-- Employee ID 2: Akshata Singh
-- Employee ID 3: Harshpreet Kaur
-- Employee ID 4: Lakshay Mishra

INSERT INTO leave_requests
    (id, employee_id, leave_type, start_date, end_date,
     reason, status, admin_remark, created_at)
VALUES
(
    1,
    2,
    'Medical Leave',
    '2026-11-10',
    '2026-11-12',
    'Medical appointment and recovery.',
    'APPROVED',
    'Approved by HR.',
    NOW() - INTERVAL 5 DAY
),
(
    2,
    2,
    'Casual Leave',
    '2026-11-20',
    '2026-11-21',
    'Attending a family wedding.',
    'PENDING',
    NULL,
    NOW() - INTERVAL 1 DAY
),
(
    3,
    3,
    'Earned Leave',
    '2026-12-01',
    '2026-12-05',
    'Annual family vacation.',
    'PENDING',
    NULL,
    NOW() - INTERVAL 2 DAY
),
(
    4,
    3,
    'Sick Leave',
    '2026-10-15',
    '2026-10-16',
    'Rest due to illness.',
    'APPROVED',
    'Approved by HR.',
    NOW() - INTERVAL 18 DAY
),
(
    5,
    4,
    'Casual Leave',
    '2026-10-28',
    '2026-10-29',
    'Personal documentation work.',
    'REJECTED',
    'Unable to approve due to work requirements.',
    NOW() - INTERVAL 7 DAY
);

-- STEP 7: Verify employee accounts
SELECT
    id,
    name,
    email,
    role,
    department
FROM employees
ORDER BY id;

-- STEP 8: Verify leave applications
SELECT
    l.id AS leave_request_id,
    e.name AS employee_name,
    e.email,
    l.leave_type,
    l.start_date,
    l.end_date,
    l.reason,
    l.status,
    l.admin_remark
FROM leave_requests l
JOIN employees e ON l.employee_id = e.id
ORDER BY l.id;

-- STEP 9: Dashboard summary
SELECT
    COUNT(*) AS total_requests,
    SUM(status = 'PENDING') AS pending_requests,
    SUM(status = 'APPROVED') AS approved_requests,
    SUM(status = 'REJECTED') AS rejected_requests
FROM leave_requests;
