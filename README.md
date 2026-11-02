# Employee Leave Management System

A production-grade, enterprise-ready college project for managing employee leave requests built with Java, Java Servlets, JavaServer Pages (JSP), JDBC, and MySQL.

---

## 1. Project Overview

- **Project Title:** Employee Leave Management System (ELMS)
- **Domain:** Human Resources (HR)
- **Description:** A system for managing employee leave requests using Java, Servlets, and JSP.
- **Objective:** Streamline and automate leave request processing.
- **Targeted Outcome:** An efficient and organized leave management system.

---

## 2. Technology Stack & Compatibility

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Frontend** | HTML5, CSS3, JSP | Jakarta JSP 3.1 / JSTL 3.0 | Responsive web interface, dynamic server-side page rendering |
| **Backend** | Java, Java Servlets | Jakarta Servlet 6.0 | Request dispatching, session management, business logic |
| **Language / SDK** | Java (JDK) | JDK 17, 21, or 25 LTS | Core language runtime and compilation |
| **Database** | MySQL | MySQL 8.0+ / MariaDB | Relational storage for employee records & leave applications |
| **Database Driver** | MySQL Connector/J | 8.3.0 | JDBC driver for database connectivity |
| **Web Server** | Apache Tomcat | Tomcat 10.1.x+ (Jakarta EE 10) | Servlet container and application server |
| **Build Tool** | Apache Maven | 3.9+ | Dependency management, compilation, and WAR packaging |

> **Tomcat Version Note:** This project is built using modern **Jakarta EE 10** (`jakarta.servlet.*`), designed for **Apache Tomcat 10.1.x** and **Java 17/21/25**. If your college lab requires legacy **Apache Tomcat 9.0.x** (`javax.servlet.*`), see the [Tomcat 9 Compatibility](#tomcat-9-compatibility-switch) section below.

---

## 3. Hardware and Software Requirements

### Hardware Requirements
- **Processor:** Intel Core i3 / AMD Ryzen 3 or higher
- **RAM:** Minimum 4 GB (8 GB recommended)
- **Disk Space:** Minimum 500 MB free hard drive space
- **Display:** 1280 x 720 resolution or higher

### Software Requirements
- **Operating System:** Windows 10/11, macOS, or Linux
- **Java Development Kit:** JDK 17 or higher (configured in `JAVA_HOME`)
- **Web Server:** Apache Tomcat 10.1.x
- **Database Server:** MySQL Server 8.0+ (or XAMPP with MySQL)
- **Build Tool:** Apache Maven 3.8+
- **Web Browser:** Google Chrome, Mozilla Firefox, Microsoft Edge, or Safari

---

## 4. Key Features & Functionality

### A. Authentication & Security
- **Secure Password Hashing:** Passwords are never stored in plaintext; hashed using `SHA-256` combined with a cryptographic salt (`PasswordUtil.java`).
- **Session Management:** HTTP sessions with 30-minute inactivity timeout. Existing sessions are invalidated upon login to defeat **Session Fixation attacks**.
- **Role-Based Access Control (RBAC):** Distinct workflows and access levels for **EMPLOYEE** and **ADMIN** roles. Protected routes are guarded by `AuthenticationFilter.java`.
- **CSRF Protection:** Synchronizer CSRF tokens (`CsrfUtil.java`) prevent Cross-Site Request Forgery on all state-changing forms (`/login`, `/apply-leave`, `/admin/leave-action`).
- **SQL Injection Defense:** All database queries utilize **`PreparedStatement`** with strict parameter binding.
- **XSS Protection:** JSP views utilize `<c:out>` and HTML escaping to prevent cross-site scripting.

### B. Employee Role
- **Employee Dashboard:** Displays dynamic metric cards (Total Applications, Pending Approval, Approved Requests, Rejected Requests) loaded directly from MySQL.
- **Apply for Leave:** Interactive form with category selection (Casual, Sick, Medical, Earned, Parental), date-range pickers with validation (end date cannot precede start date), and detailed reason input.
- **Parameter Tampering Prevention:** Form submissions bind the leave to the authenticated server session ID; employees cannot submit requests on behalf of other employees by altering hidden parameters.
- **Leave History & Status:** Real-time table displaying personal leave applications, calendar duration, status badges (amber **Pending**, green **Approved**, red **Rejected**), and administrator feedback remarks.
- **Record Isolation:** Employees can **only view their own** leave records.

### C. Administrator Role
- **Executive Leave Dashboard:** Displays organization-wide metrics (Active Employees, Total Applications, Pending Decisions, Approved, Rejected).
- **All Applications Table:** Comprehensive list of all organization leave applications joining employee name, email, and department.
- **Approval & Rejection:** Managers can approve or reject pending requests with an optional explanatory remark.
- **State Integrity:** Only requests in `PENDING` state can be approved or rejected. Repeated or conflicting status updates are blocked at the database and DAO layers.

---

## 5. Architectural Pattern (MVC)

The system adheres strictly to the **Model-View-Controller (MVC)** architectural pattern:

```
[ Web Browser ]
      │  ▲
 HTTP │  │ HTML / Dynamic JSP
 Req  │  │
      ▼  │
┌────────────────────────────────────────────────────────┐
│ CONTROLLER LAYER (Servlets & Filters)                  │
│ • LoginServlet, LogoutServlet                          │
│ • EmployeeDashboardServlet                             │
│ • LeaveRequestServlet, LeaveStatusServlet              │
│ • AdminServlet, AuthenticationFilter                   │
└───────────────────┬────────────────────────────────────┘
                    │ Uses Models & DAOs
                    ▼
┌────────────────────────────────────────────────────────┐
│ MODEL & DATA ACCESS LAYER (POJOs & DAOs)               │
│ • Employee.java, LeaveRequest.java                     │
│ • EmployeeDAO.java, LeaveRequestDAO.java               │
│ • DBConnection.java, PasswordUtil.java, CsrfUtil.java  │
└───────────────────┬────────────────────────────────────┘
                    │ JDBC (PreparedStatements)
                    ▼
┌────────────────────────────────────────────────────────┐
│ DATABASE LAYER                                         │
│ • MySQL (Database: employee_leave_management)          │
└────────────────────────────────────────────────────────┘
```

---

## 6. Project Directory Structure

```
EmployeeLeaveManagement/
├── pom.xml                               # Maven build & dependency configuration
├── database.sql                          # MySQL schema, table creation & seed records
├── README.md                             # Comprehensive project documentation
├── VIVA_NOTES.md                         # Detailed viva prep, code explanations & Q&A
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/leave/
│   │   │       ├── DBConnection.java     # JDBC connection factory (reads db.properties)
│   │   │       ├── Employee.java         # Employee entity model
│   │   │       ├── LeaveRequest.java     # Leave application entity model
│   │   │       ├── LoginServlet.java     # User authentication controller
│   │   │       ├── LogoutServlet.java    # Session termination controller
│   │   │       ├── EmployeeDashboardServlet.java # Employee dashboard controller
│   │   │       ├── LeaveRequestServlet.java      # Leave submission controller
│   │   │       ├── LeaveStatusServlet.java       # Personal leave history controller
│   │   │       ├── AdminServlet.java             # Admin dashboard & review controller
│   │   │       ├── AuthenticationFilter.java     # Security & RBAC gatekeeper
│   │   │       ├── dao/
│   │   │       │   ├── EmployeeDAO.java          # Employee database operations
│   │   │       │   └── LeaveRequestDAO.java      # Leave request database operations
│   │   │       └── util/
│   │   │           ├── PasswordUtil.java         # SHA-256 salted password hashing
│   │   │           └── CsrfUtil.java             # CSRF token generator & validator
│   │   ├── resources/
│   │   │   ├── db.properties                     # Active MySQL connection settings
│   │   │   └── db.properties.example             # Clean configuration template
│   │   └── webapp/
│   │       ├── index.jsp                         # Route dispatcher / landing page
│   │       ├── login.jsp                         # Sign-in view with demo credentials
│   │       ├── dashboard.jsp                     # Employee dashboard view
│   │       ├── apply-leave.jsp                   # Leave application form view
│   │       ├── leave-status.jsp                  # Employee leave history view
│   │       ├── admin-dashboard.jsp               # Administrator management view
│   │       ├── css/
│   │       │   └── style.css                     # Modern, responsive UI stylesheet
│   │       └── WEB-INF/
│   │           └── web.xml                       # Deployment descriptor & session config
│   └── test/
│       └── java/
│           └── com/leave/
│               ├── PasswordUtilTest.java         # Hashing & verification unit tests
│               ├── ModelTest.java                # Model duration & role logic unit tests
│               └── LeaveManagementIntegrationTest.java # In-memory database DAO integration tests
```

---

## 7. Database Setup & Schema

### Step 1: Open MySQL Console or phpMyAdmin
Log into your MySQL server via Command Prompt / Terminal:
```bash
mysql -u root -p
```
*(Enter your MySQL root password when prompted)*

### Step 2: Execute `database.sql`
Run the script directly from the MySQL command line:
```sql
SOURCE C:/Users/Akshata singh/.gemini/antigravity/scratch/EmployeeLeaveManagement/database.sql;
```
*Or copy and paste the contents of `database.sql` into MySQL Workbench or phpMyAdmin SQL query window.*

### Database Schema Details:

#### 1. `employees` Table
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | `AUTO_INCREMENT, PRIMARY KEY` | Unique employee identifier |
| `name` | `VARCHAR(100)` | `NOT NULL` | Full employee name |
| `email` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | Work email (used for login) |
| `password_hash`| `VARCHAR(255)` | `NOT NULL` | SHA-256 salted password hash |
| `role` | `VARCHAR(20)` | `NOT NULL, DEFAULT 'EMPLOYEE'` | Role (`'EMPLOYEE'` or `'ADMIN'`) |
| `department` | `VARCHAR(100)` | `DEFAULT 'General'` | Department / Team name |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Account registration timestamp |

#### 2. `leave_requests` Table
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | `AUTO_INCREMENT, PRIMARY KEY` | Unique application ID |
| `employee_id` | `INT` | `FOREIGN KEY REFERENCES employees(id)` | Associated employee (CASCADE on delete) |
| `leave_type` | `VARCHAR(50)` | `NOT NULL` | Category (Casual, Sick, Medical, etc.) |
| `start_date` | `DATE` | `NOT NULL` | Leave start date |
| `end_date` | `DATE` | `NOT NULL, CHECK (end_date >= start_date)`| Leave end date |
| `reason` | `TEXT` | `NOT NULL` | Reason submitted by employee |
| `status` | `VARCHAR(20)` | `DEFAULT 'PENDING', CHECK (status IN (...))`| `'PENDING'`, `'APPROVED'`, or `'REJECTED'` |
| `admin_remark` | `TEXT` | `NULL` | Feedback entered by Administrator |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Application timestamp |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE` | Last status modification timestamp |

### Initial Demonstration Credentials:

| Role | Name | Email | Password | Pre-loaded Records |
| :--- | :--- | :--- | :--- | :--- |
| **Administrator** | System Administrator | `admin@company.com` | `Admin@123` | Full administrative review access |
| **Employee** | John Doe | `john.doe@company.com` | `Password@123` | 1 Approved, 1 Pending leave |
| **Employee** | Jane Smith | `jane.smith@company.com` | `Password@123` | 1 Approved, 1 Pending leave |
| **Employee** | Robert Brown | `robert.brown@company.com` | `Password@123` | 1 Rejected leave |

---

## 8. Configuration

Open `src/main/resources/db.properties` and verify your local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/employee_leave_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=YOUR_LOCAL_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

If your MySQL password is empty (common in default XAMPP setups), set `db.password=` with no value after the equal sign.

---

## 9. Build and Execution Instructions

### A. Run Automated Tests
Execute all unit and integration tests using Maven:
```bash
cd EmployeeLeaveManagement
mvn test
```
*Expected Result: `BUILD SUCCESS` with all 8 tests passing.*

### B. Package the Web Application (WAR)
Compile the source code and build the deployable `.war` archive:
```bash
mvn clean package
```
*This generates `EmployeeLeaveManagement.war` inside the `target/` directory.*

### C. Deploy to Apache Tomcat
1. Download and extract **Apache Tomcat 10.1.x** (e.g. from [tomcat.apache.org](https://tomcat.apache.org/download-10.cgi)).
2. Copy `target/EmployeeLeaveManagement.war` into your Tomcat `webapps/` folder:
   - On Windows: `C:\apache-tomcat-10.1.x\webapps\`
3. Start Tomcat:
   - On Windows: Open Command Prompt, navigate to `C:\apache-tomcat-10.1.x\bin\` and run `startup.bat`.
   - On Linux/macOS: Run `./bin/startup.sh`.
4. Open your web browser and navigate to:
   ```
   http://localhost:8080/EmployeeLeaveManagement/
   ```

---

## 10. Manual Testing & Demonstration Walkthrough

Follow these steps to demonstrate the project to your teacher or examiner:

1. **Test Login (Negative & Positive):**
   - Navigate to `http://localhost:8080/EmployeeLeaveManagement/login`.
   - Submit empty credentials &rarr; Verify error message: *"Email and Password are required."*
   - Enter `john.doe@company.com` with incorrect password `wrong123` &rarr; Verify error message: *"Invalid email or password."*
   - Enter valid employee credentials: `john.doe@company.com` / `Password@123` &rarr; Successfully logs in and redirects to `/dashboard`.

2. **Test Employee Dashboard:**
   - Verify welcome header: *"Welcome, John Doe! (Software Engineering)"*.
   - Check summary cards: Total (2), Pending (1), Approved (1), Rejected (0).
   - Check the recent leave requests table.

3. **Test Apply for Leave:**
   - Click **"Apply for Leave"** in the navigation bar.
   - Enter invalid date range (e.g., End Date earlier than Start Date) &rarr; Verify server validation message: *"End date cannot be earlier than start date."*
   - Select Leave Type: *Casual Leave*, Start Date: tomorrow, End Date: day after tomorrow, Reason: *"Personal family celebration."*
   - Click **"Submit Leave Application"**.
   - Verify redirection to `/leave-status` with the green success alert: *"Success! Your leave application was submitted successfully..."*
   - Observe the new request in the history table with an amber **Pending** badge.

4. **Test Logout & Security Restrictions:**
   - Click **"Sign Out"** &rarr; Verify session invalidation and redirect to login page with *"You have been safely logged out."*
   - Attempt to manually visit `http://localhost:8080/EmployeeLeaveManagement/dashboard` or `http://localhost:8080/EmployeeLeaveManagement/admin/dashboard` in the address bar &rarr; `AuthenticationFilter` intercepts and redirects back to `/login`.

5. **Test Administrator Console:**
   - Log in as Administrator: `admin@company.com` / `Admin@123`.
   - System recognizes admin role and redirects to `/admin/dashboard`.
   - Review organization metrics: Active Employees (3), Total Requests (6), Pending Decision (3), Approved (2), Rejected (1).
   - Locate the pending request submitted by John Doe.
   - Enter a remark: *"Approved. Please ensure sprint tasks are handed over to team."*
   - Click the green **"✓ Approve"** button.
   - Verify page refreshes with green flash alert: *"Leave Request #... successfully approved."*
   - Observe that the row immediately updates to a green **Approved** badge and displays the recorded remark. The approval buttons are removed, preventing duplicate action.

6. **Verify Real-Time Synchronization for Employee:**
   - Sign out from Admin.
   - Log back in as `john.doe@company.com` / `Password@123`.
   - Navigate to **"My Leave History"** &rarr; Verify that John Doe's request is now marked **Approved** with the exact admin remark visible.

---

## 11. Troubleshooting Common Issues

### Issue 1: `CommunicationsException: Communications link failure`
- **Cause:** MySQL server is not running or port 3306 is blocked.
- **Solution:** 
  1. Open Windows Services (`services.msc`) and ensure `MySQL` or `MySQL80` service status is **Running**.
  2. If using XAMPP, open XAMPP Control Panel and start MySQL.

### Issue 2: `Access denied for user 'root'@'localhost'`
- **Cause:** Incorrect MySQL password specified in configuration.
- **Solution:** Open `src/main/resources/db.properties` and verify `db.password` matches your local MySQL root password.

### Issue 3: Tomcat Port 8080 Already in Use
- **Cause:** Another program (e.g., Jenkins, Oracle XE) is already listening on port 8080.
- **Solution:** Open `conf/server.xml` in your Tomcat installation and change `<Connector port="8080" protocol="HTTP/1.1"` to `port="8081"`, then access `http://localhost:8081/EmployeeLeaveManagement/`.

### <a id="tomcat-9-compatibility-switch"></a>Tomcat 9 Compatibility Switch
If your college requires Apache Tomcat 9 instead of Tomcat 10:
1. In `pom.xml`, replace `jakarta.servlet:jakarta.servlet-api:6.0.0` with `javax.servlet:javax.servlet-api:4.0.1`.
2. In Java files, replace `import jakarta.servlet.*` with `import javax.servlet.*`.
3. In JSP files, replace `jakarta.tags.core` with `http://java.sun.com/jsp/jstl/core`.
*(The clean MVC structure and business logic remain 100% identical).*
