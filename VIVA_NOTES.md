# Comprehensive Viva Preparation & Code Defense Guide
## Project: Employee Leave Management System (ELMS)

This guide is designed to help you confidently present your project and answer any question your teacher or external viva examiner might ask. Everything is explained in simple, clear English with direct references to the actual code.

---

## TABLE OF CONTENTS
1. [Section A: Project Introduction](#section-a-project-introduction)
2. [Section B: Technology Explanations](#section-b-technology-explanations)
3. [Section C: End-to-End Workflow & Code Explanations](#section-c-end-to-end-workflow--code-explanations)
4. [Section D: Core Database Concepts](#section-d-core-database-concepts)
5. [Section E: Core Web Development Concepts](#section-e-core-web-development-concepts)
6. [Section F: 38 Common Viva Questions & Short Answers](#section-f-38-common-viva-questions--short-answers)
7. [Section G: Code-Based Viva (File-by-File Breakdown)](#section-g-code-based-viva-file-by-file-breakdown)

---

## SECTION A: PROJECT INTRODUCTION

### 1. What does this project do?
The **Employee Leave Management System (ELMS)** is a web-based enterprise application that automates the complete lifecycle of leave applications within an organization. It allows employees to apply for different types of leave online, monitor the real-time status of their requests, and review manager remarks. At the same time, it allows administrators and HR managers to view all organization requests, assess staffing levels, and approve or reject applications with custom feedback.

### 2. Why did you select this project?
In many traditional offices and institutions, leave management is done using paper application forms or informal emails. This causes delays, lack of transparency, lost records, and difficulty calculating leave metrics. I selected this project because it solves a real-world Human Resources (HR) problem while allowing me to demonstrate complete full-stack Java skills: frontend UI design, backend request processing with Servlets, dynamic presentation with JSP, and persistent storage with MySQL via JDBC.

### 3. What is the objective?
- **Objective:** Streamline and automate leave request processing.
- To eliminate manual paper-based leave processing.
- To provide employees with self-service leave tracking.
- To provide management with a centralized dashboard to process requests transparently.

### 4. What is the targeted outcome?
- **Targeted Outcome:** An efficient and organized leave management system.
- Zero lost applications.
- Immediate email-free communication of approval or rejection.
- Secure, role-segregated access ensuring employee privacy and managerial oversight.

### 5. What are the main features?
1. **Role-Based Authentication:** Separate permissions for regular employees and administrators.
2. **Password Security:** SHA-256 cryptographic hashing with salt to protect user passwords.
3. **Interactive Employee Dashboard:** Summary cards for total, pending, approved, and rejected leaves.
4. **Leave Application Form:** Server-validated form with category selection, date constraints, and reason field.
5. **Leave Tracking & History:** Personal records table showing request progress, dates, and administrative remarks.
6. **Administrator Console:** Central review panel with one-click approval and rejection actions.
7. **Security Controls:** CSRF tokens, session fixation prevention, session timeout, and SQL injection prevention using `PreparedStatement`.

---

## SECTION B: TECHNOLOGY EXPLANATIONS

### 1. Java
Java is a class-based, object-oriented programming language designed to have as few implementation dependencies as possible. It follows the principle **"Write Once, Run Anywhere" (WORA)** because compiled Java bytecode executes on any platform that has a Java Virtual Machine (JVM).

### 2. HTML (HyperText Markup Language)
HTML is the standard markup language used to structure web pages. It defines elements like headings (`<h1>`), paragraphs (`<p>`), tables (`<table>`), and input forms (`<form>`, `<input>`, `<select>`).

### 3. CSS (Cascading Style Sheets)
CSS describes how HTML elements should be visually displayed. It handles styling, layout, typography, colors, borders, shadows, and responsiveness across desktop and mobile screens.

### 4. JSP (JavaServer Pages)
JSP is a server-side technology that allows developers to create dynamic web pages by embedding Java code and expression language into HTML templates. The server compiles each JSP into a Java Servlet the first time it is requested. In this project, JSP files serve as the **View** layer in MVC.

### 5. Java Servlets
A Java Servlet is a Java class that runs on a web server (like Tomcat) and handles incoming HTTP network requests (GET, POST), interacts with databases and business logic, and generates HTTP responses. Servlets act as the **Controller** layer in MVC.

### 6. JDBC (Java Database Connectivity)
JDBC is an official Java API that allows Java programs to interact with relational databases. It provides classes and interfaces like `DriverManager`, `Connection`, `PreparedStatement`, and `ResultSet` to send SQL statements and retrieve tabular data.

### 7. MySQL
MySQL is an open-source Relational Database Management System (RDBMS) that stores structured application data in tables consisting of rows and columns. It enforces ACID (Atomicity, Consistency, Isolation, Durability) properties.

### 8. Apache Tomcat
Apache Tomcat is an open-source HTTP web server and Java Servlet container that implements the Jakarta Servlet and JSP specifications. It receives client browser requests, invokes the matching Servlet or JSP, and returns the generated HTML back to the browser.

### 9. JDK vs. JRE
- **JDK (Java Development Kit):** Contains everything needed to **develop** Java programs, including the compiler (`javac`), archive tool (`jar`), debugger, and development tools, plus the JRE.
- **JRE (Java Runtime Environment):** Contains only the components needed to **run** an already compiled Java program, primarily the Java Virtual Machine (JVM) and core class libraries.

### 10. Apache Maven
Maven is a build automation and dependency management tool. It reads a `pom.xml` configuration file to automatically download required library JARs (e.g. MySQL Connector, Jakarta Servlet API, JUnit), compile Java source code, run tests, and package the final project into a `.war` file.

### 11. MVC Architecture
MVC stands for **Model-View-Controller**. It is an architectural design pattern that separates an application into three interconnected components:
- **Model:** Represents application data and business entities (`Employee.java`, `LeaveRequest.java`).
- **View:** Presents data to the user (`dashboard.jsp`, `login.jsp`).
- **Controller:** Accepts user input, coordinates models, and selects the view to render (`LoginServlet.java`, `AdminServlet.java`).

---

## SECTION C: END-TO-END WORKFLOW & CODE EXPLANATIONS

### 1. How the Login Process Works
1. The user enters their email and password on `login.jsp` and clicks "Sign In".
2. The browser sends an HTTP `POST` request to `/login` containing `email`, `password`, and `csrfToken`.
3. `LoginServlet.java` extracts parameters and validates that fields are not empty.
4. It calls `EmployeeDAO.findByEmail(email)` to query the `employees` table using a `PreparedStatement`.
5. If an employee record exists, `PasswordUtil.verifyPassword(rawPassword, storedHash)` hashes the submitted password with the application salt using SHA-256 and compares it with the hash stored in MySQL.
6. If credentials match:
   - The old session is invalidated to prevent session fixation attacks.
   - A fresh `HttpSession` is created, and the `Employee` object is stored as `session.setAttribute("user", employee)`.
   - A secure CSRF token is placed in the session.
   - If the user's role is `ADMIN`, they are redirected to `/admin/dashboard`.
   - If the user's role is `EMPLOYEE`, they are redirected to `/dashboard`.
7. If credentials do not match, an error message is set in request scope and forwarded back to `login.jsp`.

### 2. How a Leave Request is Submitted
1. An authenticated employee visits `/apply-leave` (handled by `LeaveRequestServlet.java` via `doGet()`), which forwards to `apply-leave.jsp`.
2. The employee fills in leave type, start date, end date, and reason, and clicks "Submit".
3. The browser submits an HTTP `POST` request to `/apply-leave`.
4. `LeaveRequestServlet.java`:
   - Checks that the user is logged in.
   - Verifies the submitted CSRF token using `CsrfUtil.isValidToken(request)`.
   - Reads form fields: `leaveType`, `startDate`, `endDate`, `reason`.
   - Performs server-side validation: ensures all fields are populated, validates date format (`LocalDate.parse`), verifies that `endDate >= startDate`, and ensures reason is at least 5 characters.
   - **Critical Security Step:** Retrieves the employee's ID directly from the session `(Employee) session.getAttribute("user")`. It never reads `employee_id` from client request parameters.
   - Populates a `LeaveRequest` object with `status = "PENDING"`.
   - Calls `LeaveRequestDAO.createLeaveRequest(newRequest)`.
5. MySQL executes the `INSERT` statement and returns the generated primary key `id`.
6. `LeaveRequestServlet` redirects the employee to `/leave-status?submitted=true`.

### 3. How the Admin Approves or Rejects a Request
1. The administrator visits `/admin/dashboard` (handled by `AdminServlet.java`).
2. `AdminServlet` verifies that `user != null` and `user.isAdmin() == true`.
3. It fetches all requests via `LeaveRequestDAO.getAllRequestsWithEmployeeDetails()` using an `INNER JOIN` between `leave_requests` and `employees`.
4. For any request with status `PENDING`, `admin-dashboard.jsp` renders an inline decision form with a remark text box and two buttons: "✓ Approve" and "✕ Reject".
5. When the admin clicks either button, an HTTP `POST` is sent to `/admin/leave-action`.
6. `AdminServlet`:
   - Validates the CSRF token.
   - Verifies admin role.
   - Parses `requestId`, target action (`APPROVE` or `REJECT`), and optional `adminRemark`.
   - Calls `LeaveRequestDAO.updateStatus(requestId, status, remark)`.
   - The DAO executes: `UPDATE leave_requests SET status = ?, admin_remark = ? WHERE id = ? AND status = 'PENDING'`.
   - Enforcing `WHERE status = 'PENDING'` guarantees that an already processed request cannot be accidentally or maliciously overwritten.
7. `AdminServlet` sets a flash notification in the session and redirects back to `/admin/dashboard`.

### 4. How the Employee Sees the Updated Status
1. The employee clicks "My Leave History" (`/leave-status`).
2. `LeaveStatusServlet.java` queries `LeaveRequestDAO.getRequestsByEmployeeId(user.getId())`.
3. The SQL query retrieves only records where `employee_id = ?`, sorted by `created_at DESC`.
4. The records list is passed to `leave-status.jsp` as a request attribute.
5. In `leave-status.jsp`, JSTL `<c:forEach>` iterates over the list:
   - Status `APPROVED` displays a green badge.
   - Status `REJECTED` displays a red badge.
   - Status `PENDING` displays an amber badge.
   - If an admin remark was provided, it is displayed in an alert callout box.

---

## SECTION D: CORE DATABASE CONCEPTS

### 1. What is a Table?
A table is a collection of related data held in a structured format within a database. It consists of vertical columns (attributes/fields) and horizontal rows (records/tuples).

### 2. Row vs. Column
- **Column (Attribute):** Represents a specific data property (e.g., `email`, `leave_type`, `start_date`) with a defined data type.
- **Row (Record):** A single instance or entry containing data for all columns (e.g., John Doe's specific leave request on 2026-11-10).

### 3. Primary Key
A Primary Key is a column or set of columns that uniquely identifies each row in a table. It cannot contain `NULL` values and must be unique.
- *In our project:* `employees.id` and `leave_requests.id` are auto-incrementing primary keys.

### 4. Foreign Key
A Foreign Key is a column that creates a link between two tables by referencing the primary key of another table. It enforces referential integrity.
- *In our project:* `leave_requests.employee_id` references `employees.id`. The constraint `ON DELETE CASCADE` ensures that if an employee is deleted, all their associated leave requests are automatically removed.

### 5. Common SQL Queries
- `INSERT INTO`: Adds new rows into a table.
- `SELECT`: Retrieves data from one or more tables.
- `UPDATE`: Modifies existing data in a table.
- `DELETE`: Removes rows from a table.
- `JOIN`: Combines rows from two or more tables based on a related column (e.g. joining `leave_requests` and `employees` on `employee_id = employees.id`).

### 6. What is a PreparedStatement and why is it used?
A `PreparedStatement` is a pre-compiled SQL statement object in JDBC. Instead of concatenating user input directly into a query string:
```java
// Vulnerable to SQL Injection:
String query = "SELECT * FROM employees WHERE email = '" + email + "'";

// Secure with PreparedStatement:
String query = "SELECT * FROM employees WHERE email = ?";
PreparedStatement ps = conn.prepareStatement(query);
ps.setString(1, email);
```
**Benefits:**
1. **Prevents SQL Injection:** The database treats parameters strictly as data values, never as executable SQL commands.
2. **Performance:** The SQL structure is compiled once by the database engine and can be re-executed with different parameters efficiently.

### 7. What are the steps to connect Java to MySQL using JDBC?
1. **Load Driver Class:** `Class.forName("com.mysql.cj.jdbc.Driver");`
2. **Establish Connection:** `Connection conn = DriverManager.getConnection(url, username, password);`
3. **Create Statement:** `PreparedStatement ps = conn.prepareStatement(sql);`
4. **Execute Query / Update:** `ResultSet rs = ps.executeQuery();` or `int rows = ps.executeUpdate();`
5. **Process Result:** Process rows using `while (rs.next()) { ... }`.
6. **Close Resources:** Close `ResultSet`, `PreparedStatement`, and `Connection` (automatically handled using Java's `try-with-resources`).

---

## SECTION E: CORE WEB DEVELOPMENT CONCEPTS

### 1. Frontend vs. Backend
- **Frontend (Client-Side):** Everything the user sees and interacts with in the browser (HTML, CSS, JavaScript, JSP markup).
- **Backend (Server-Side):** Code running on the web server that processes business logic, verifies authentication, calculates results, and communicates with the database (Java Servlets, DAOs, JDBC).

### 2. Client vs. Server
- **Client:** The requesting device/program (e.g., Google Chrome browser on a student's laptop).
- **Server:** The computer/process listening on a network port that receives requests, executes logic, and sends back responses (e.g., Apache Tomcat on port 8080).

### 3. HTTP GET vs. HTTP POST
- **HTTP GET:** Used to retrieve data from the server. Request parameters are appended to the URL in the query string (`/leave-status?submitted=true`). It should be idempotent (safe to repeat without changing server state) and should never carry sensitive data like passwords.
- **HTTP POST:** Used to send data to the server to create or modify state (e.g., submitting login credentials, applying for leave, approving a request). Parameters are carried inside the HTTP request body, not exposed in the browser URL bar.

### 4. What is an HTTP Session?
HTTP is a **stateless** protocol, meaning the server forgets each client between requests. An `HttpSession` allows the server to remember state across multiple requests from the same client.
- The server generates a unique session ID (e.g. stored in a cookie named `JSESSIONID`).
- Server-side objects (like the logged-in `Employee`) are stored in memory associated with that session ID.

### 5. Authentication vs. Authorization
- **Authentication:** Verifying **who** the user is (e.g., checking email and password during login).
- **Authorization:** Verifying **what** permissions the authenticated user has (e.g., checking if `user.isAdmin()` before allowing leave approval).

### 6. Client-Side vs. Server-Side Validation
- **Client-Side Validation:** Performed in the browser using HTML5 attributes (`required`, `type="date"`) and JavaScript. It provides fast user feedback, but can easily be bypassed by disabling JavaScript or using tools like Postman/cURL.
- **Server-Side Validation:** Performed in Java Servlets on the server. It is **mandatory** for security because it cannot be bypassed by an attacker.

---

## SECTION F: 38 COMMON VIVA QUESTIONS & SHORT ANSWERS

#### Q1: What is the main objective of this project?
**Answer:** To automate and streamline employee leave application, approval, and record-keeping in an organization using Java Servlets, JSP, and MySQL.

#### Q2: What architecture did you use in this application?
**Answer:** The Model-View-Controller (MVC) architecture. Servlets act as Controllers, JSP pages act as Views, and Java Beans/DAOs act as the Model.

#### Q3: Which server did you use to host this application?
**Answer:** Apache Tomcat 10.1.x, which serves as a Servlet container and JSP web server.

#### Q4: What is a Java Servlet?
**Answer:** A Java class that handles HTTP requests on a server, executes business logic, and generates dynamic web responses.

#### Q5: What is JSP and how is it different from a Servlet?
**Answer:** JSP is an HTML-centric template language where dynamic Java expressions can be inserted. Servlets are pure Java code with embedded HTML strings. Tomcat automatically compiles JSP files into Servlets at runtime.

#### Q6: Why did you use `PreparedStatement` instead of `Statement`?
**Answer:** `PreparedStatement` prevents SQL Injection attacks by parameterizing queries and offers better performance through pre-compilation.

#### Q7: How are user passwords stored in the database?
**Answer:** Passwords are never stored in plaintext. They are hashed using the SHA-256 algorithm with a secret application salt via `PasswordUtil.java`.

#### Q8: What is a Salt in password hashing?
**Answer:** A salt is an extra secret string appended to the password before hashing. It prevents attackers from using precomputed rainbow tables or dictionary attacks to crack passwords.

#### Q9: What happens if an employee tries to access an administrator page?
**Answer:** `AuthenticationFilter.java` and `AdminServlet.java` inspect `session.getAttribute("user")`. If `user.isAdmin()` is false, the server returns an HTTP 403 Forbidden error and blocks access.

#### Q10: How do you prevent an employee from submitting a leave request for another employee?
**Answer:** The servlet does not read the `employee_id` from client form parameters. It extracts the ID directly from the authenticated server-side `HttpSession` (`user.getId()`).

#### Q11: How do you prevent leave start dates being after end dates?
**Answer:** We validate on both the client (HTML5 date synchronizer) and the server in `LeaveRequestServlet` using `LocalDate.isBefore()`. In addition, MySQL enforces a `CHECK (end_date >= start_date)` table constraint.

#### Q12: What is the role of `DBConnection.java`?
**Answer:** It is a centralized connection factory that loads database credentials from `db.properties`, loads the MySQL JDBC driver, and creates active `Connection` objects.

#### Q13: What is the DAO design pattern?
**Answer:** Data Access Object (DAO) pattern isolates database access logic (SQL queries, connections) from business logic (servlets), making code modular, maintainable, and testable.

#### Q14: Which HTTP methods do your servlets implement?
**Answer:** `doGet()` for reading and displaying pages, and `doPost()` for submitting forms (login, leave applications, approvals).

#### Q15: How does the application maintain user login status?
**Answer:** Using `HttpSession`. Upon successful login, the `Employee` object is stored in the session, and Tomcat tracks the client using a session cookie (`JSESSIONID`).

#### Q16: What happens during logout?
**Answer:** `LogoutServlet` calls `session.invalidate()`, which wipes all session attributes on the server, and redirects the user to `/login?loggedOut=true`.

#### Q17: What is Session Fixation and how did you prevent it?
**Answer:** Session Fixation is an attack where an attacker hijacks a user's session by fixing their session ID beforehand. We prevent it by calling `oldSession.invalidate()` and creating a fresh session immediately after successful authentication.

#### Q18: What is CSRF and how did you protect against it?
**Answer:** Cross-Site Request Forgery (CSRF) tricks a logged-in user's browser into executing unauthorized actions. We protect against it using a random CSRF token generated in the session (`CsrfUtil.java`), which must be submitted with all state-changing POST forms.

#### Q19: What is XSS and how did you prevent it in JSP?
**Answer:** Cross-Site Scripting (XSS) occurs when malicious scripts are injected into web pages. We prevent it by escaping dynamic data with `<c:out value="${...}" />` tags instead of raw scriptlet output.

#### Q20: What is the purpose of `pom.xml`?
**Answer:** It is the Project Object Model configuration file for Maven. It defines project dependencies (Servlet API, MySQL Connector, JUnit, H2), compiler version, and build plugins.

#### Q21: What is a WAR file?
**Answer:** Web Application Archive (`.war`) is a packaged ZIP archive containing compiled Java classes, JSP files, HTML, CSS, libraries, and `web.xml` ready for deployment on a servlet container like Tomcat.

#### Q22: What is the difference between `sendRedirect()` and `forward()`?
**Answer:**
- `forward()` happens entirely on the server within the same HTTP request; the browser URL does not change.
- `sendRedirect()` tells the browser to make a new HTTP GET request to a different URL; the browser URL changes.

#### Q23: Why do we use `try-with-resources` in database queries?
**Answer:** It automatically closes database resources (`Connection`, `PreparedStatement`, `ResultSet`) when the block exits, preventing connection leaks even if an exception occurs.

#### Q24: What are the valid leave statuses in the system?
**Answer:** `PENDING`, `APPROVED`, and `REJECTED`. The MySQL table enforces this with a `CHECK` constraint.

#### Q25: Can an administrator change an already approved request?
**Answer:** No. The update query specifically requires `WHERE id = ? AND status = 'PENDING'`, which prevents re-processing or conflicting updates.

#### Q26: How does the employee dashboard display summary counts?
**Answer:** `LeaveRequestDAO.getEmployeeStats()` executes a SQL aggregation query: `SELECT status, COUNT(*) FROM leave_requests WHERE employee_id = ? GROUP BY status`.

#### Q27: How does the administrator see the employee name for each request?
**Answer:** By using an `INNER JOIN` in SQL: `SELECT ... FROM leave_requests lr JOIN employees e ON lr.employee_id = e.id`.

#### Q28: What is JSTL?
**Answer:** JavaServer Pages Standard Tag Library. It provides standard tags like `<c:forEach>`, `<c:if>`, and `<c:choose>` to eliminate raw Java scriptlets from JSP pages.

#### Q29: What is the default port for Apache Tomcat?
**Answer:** Port 8080 (can be configured in `conf/server.xml`).

#### Q30: What is the default port for MySQL?
**Answer:** Port 3306.

#### Q31: What is an auto-increment column?
**Answer:** A column that automatically generates a unique sequential integer whenever a new row is inserted (e.g. `id INT AUTO_INCREMENT PRIMARY KEY`).

#### Q32: What is the role of `web.xml`?
**Answer:** It is the Web Deployment Descriptor. It configures the welcome file list, session timeout settings, cookie security flags, and servlet mappings.

#### Q33: How does the application prevent direct URL access to internal JSP pages?
**Answer:** `AuthenticationFilter.java` intercepts requests ending in `.jsp` (except `index.jsp` and `login.jsp`) and redirects users to `/login`.

#### Q34: What is `git` or version control?
**Answer:** A distributed version control system that tracks source code changes over time and allows collaboration.

#### Q35: What happens if the MySQL server is stopped when running the app?
**Answer:** `DBConnection.getConnection()` throws a `SQLException`. The servlets catch this exception, log it to the console, and display a user-friendly error message on the page without crashing the server.

#### Q36: What is a `Filter` in Java Servlets?
**Answer:** A Java class that intercepts requests and responses dynamically before they reach a servlet or after a servlet completes. Used for authentication, logging, and security checks.

#### Q37: How do unit tests verify this project without running MySQL?
**Answer:** Using an embedded in-memory H2 database configured in MySQL compatibility mode. `mvn test` executes 8 automated tests in 3 seconds.

#### Q38: What would you improve in future versions of this project?
**Answer:** Add email notifications via JavaMail API, multi-level manager approvals, document attachments (e.g., medical certificates), and an annual leave balance allowance system.

---

## SECTION G: CODE-BASED VIVA (FILE-BY-FILE BREAKDOWN)

### 1. `DBConnection.java`
- **Location:** `src/main/java/com/leave/DBConnection.java`
- **Purpose:** Acts as a centralized factory providing JDBC connections to the MySQL database.
- **Key Methods:**
  - `loadConfiguration()`: Reads `db.properties` from the classpath using `ClassLoader.getResourceAsStream()`.
  - `getConnection()`: Invokes `DriverManager.getConnection(url, username, password)` to establish a live TCP connection.
  - `setCustomConnectionDetails(...)`: Allows JUnit tests to switch to an in-memory database without modifying production code.
- **Possible Viva Question:** *"Why not hardcode database credentials inside Java code?"*
- **Answer:** *"Hardcoding credentials is a security risk and makes deployment inflexible. Storing them in `db.properties` allows configuration changes across development, testing, and production without recompiling code."*

### 2. `PasswordUtil.java`
- **Location:** `src/main/java/com/leave/util/PasswordUtil.java`
- **Purpose:** Handles cryptographic password hashing and verification.
- **Key Methods:**
  - `hashPassword(String plain)`: Hashes plaintext with a secret salt using `MessageDigest.getInstance("SHA-256")` and formats bytes into a 64-character hex string.
  - `verifyPassword(String plain, String hash)`: Computes the hash of the entered password and compares it with the stored hash using `equalsIgnoreCase()`.
- **Possible Viva Question:** *"Why can't you decrypt an SHA-256 password hash?"*
- **Answer:** *"SHA-256 is a one-way mathematical hash function, not encryption. It compresses arbitrary data into a fixed-length digest. There is no mathematical key to reverse the operation."*

### 3. `EmployeeDAO.java`
- **Location:** `src/main/java/com/leave/dao/EmployeeDAO.java`
- **Purpose:** Executes SQL queries related to the `employees` table.
- **Key Methods:**
  - `findByEmail(String email)`: `SELECT ... FROM employees WHERE email = ?`.
  - `getTotalEmployeesCount()`: `SELECT COUNT(*) FROM employees WHERE role = 'EMPLOYEE'`.
  - `createEmployee(Employee emp)`: `INSERT INTO employees (...) VALUES (...)`.
- **Possible Viva Question:** *"Why did you use `Statement.RETURN_GENERATED_KEYS`?"*
- **Answer:** *"To capture the auto-generated primary key ID created by MySQL and set it back on the Java model object immediately."*

### 4. `LeaveRequestDAO.java`
- **Location:** `src/main/java/com/leave/dao/LeaveRequestDAO.java`
- **Purpose:** Executes SQL queries for leave submission, retrieval, statistics, and status updates.
- **Key Methods:**
  - `createLeaveRequest(...)`: Inserts leave record with default status `'PENDING'`.
  - `getRequestsByEmployeeId(int empId)`: Selects leave applications belonging to a single employee.
  - `getAllRequestsWithEmployeeDetails()`: Executes an `INNER JOIN` with the `employees` table to retrieve all organization applications.
  - `updateStatus(int id, String status, String remark)`: Executes `UPDATE leave_requests SET status = ?, admin_remark = ? WHERE id = ? AND status = 'PENDING'`.
  - `getEmployeeStats(int empId)`: Aggregates counts grouped by status.
- **Possible Viva Question:** *"Why is `AND status = 'PENDING'` included in the UPDATE query?"*
- **Answer:** *"To guarantee state integrity. It ensures that only pending requests can be approved or rejected, preventing an admin from accidentally overwriting an already processed application."*

### 5. `LoginServlet.java`
- **Location:** `src/main/java/com/leave/LoginServlet.java`
- **Purpose:** Handles user sign-in authentication and role-based dispatching.
- **Workflow:**
  - `doGet()`: Checks if user already has an active session; forwards to `login.jsp`.
  - `doPost()`: Validates inputs, fetches employee from database, verifies password hash, destroys old session (preventing session fixation), creates fresh session, stores `user` attribute, and redirects to `/admin/dashboard` or `/dashboard`.
- **Possible Viva Question:** *"Why do you invalidate the session before creating a new one on login?"*
- **Answer:** *"To prevent Session Fixation attacks, where an attacker tricks a user into authenticating using a pre-determined session identifier."*

### 6. `LeaveRequestServlet.java`
- **Location:** `src/main/java/com/leave/LeaveRequestServlet.java`
- **Purpose:** Manages the leave application form and submission.
- **Workflow:**
  - Verifies session authentication and validates CSRF token.
  - Parses start and end dates into `LocalDate`.
  - Validates `endDate.isBefore(startDate)`.
  - Extracts employee ID directly from `user.getId()` in the session.
  - Inserts request into database and redirects with `?submitted=true`.
- **Possible Viva Question:** *"What happens if a user disables JavaScript and submits an invalid date?"*
- **Answer:** *"The servlet performs complete server-side validation. If the dates are invalid or reversed, it rejects the submission and displays an error message on the page."*

### 7. `AdminServlet.java`
- **Location:** `src/main/java/com/leave/AdminServlet.java`
- **Purpose:** Manages the administrator console and handles approval/rejection actions.
- **Workflow:**
  - Enforces `user.isAdmin()` check; returns HTTP 403 Forbidden if violated.
  - `doGet()`: Fetches metrics and all employee requests, forwarding to `admin-dashboard.jsp`.
  - `doPost()`: Validates CSRF token, parses action (`APPROVE`/`REJECT`), updates database via DAO, and redirects with flash message.
- **Possible Viva Question:** *"What is the purpose of the PRG (Post-Redirect-Get) pattern here?"*
- **Answer:** *"After processing the POST action, the servlet redirects to the GET dashboard endpoint. This prevents the browser from prompting 'Resubmit form?' if the user refreshes the page."*
