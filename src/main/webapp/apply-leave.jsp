<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply for Leave - Employee Leave Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">
                <span class="brand-badge">ELMS</span>
                <span>Employee Portal</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/apply-leave" class="active">Apply for Leave</a></li>
                <li><a href="${pageContext.request.contextPath}/leave-status">My Leave History</a></li>
            </ul>
            <div class="nav-user">
                <span class="user-badge">
                    👋 <strong><c:out value="${sessionScope.user.name}"/></strong>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a>
            </div>
        </div>
    </header>

    <main class="main-content">
        
        <div class="page-header">
            <div>
                <h1>Apply for Leave</h1>
                <p>Submit your formal leave request for managerial approval.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
                    &larr; Back to Dashboard
                </a>
            </div>
        </div>

        <!-- Form Card Container -->
        <div class="card" style="max-width: 720px; margin: 0 auto 3rem;">
            <div class="card-header">
                <h2>Leave Application Form</h2>
            </div>
            <div class="card-body">
                
                <!-- Server-Side Error Alert -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" role="alert">
                        <span>⚠️</span>
                        <div><c:out value="${errorMessage}"/></div>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/apply-leave" method="POST" id="leaveForm">
                    <!-- CSRF Protection Token -->
                    <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>" />

                    <!-- Leave Type Dropdown -->
                    <div class="form-group">
                        <label for="leaveType">Leave Type <span style="color: var(--danger);">*</span></label>
                        <select id="leaveType" name="leaveType" class="form-control" required>
                            <option value="">-- Select Leave Category --</option>
                            <option value="Casual Leave" ${selectedLeaveType eq 'Casual Leave' ? 'selected' : ''}>Casual Leave</option>
                            <option value="Sick Leave" ${selectedLeaveType eq 'Sick Leave' ? 'selected' : ''}>Sick Leave</option>
                            <option value="Medical Leave" ${selectedLeaveType eq 'Medical Leave' ? 'selected' : ''}>Medical Leave</option>
                            <option value="Earned Leave" ${selectedLeaveType eq 'Earned Leave' ? 'selected' : ''}>Earned Leave / Vacation</option>
                            <option value="Maternity / Paternity Leave" ${selectedLeaveType eq 'Maternity / Paternity Leave' ? 'selected' : ''}>Maternity / Paternity Leave</option>
                        </select>
                    </div>

                    <!-- Date Range Fields -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="startDate">Start Date <span style="color: var(--danger);">*</span></label>
                            <input type="date" 
                                   id="startDate" 
                                   name="startDate" 
                                   class="form-control" 
                                   value="<c:out value='${enteredStartDate}'/>" 
                                   required />
                        </div>
                        <div class="form-group">
                            <label for="endDate">End Date <span style="color: var(--danger);">*</span></label>
                            <input type="date" 
                                   id="endDate" 
                                   name="endDate" 
                                   class="form-control" 
                                   value="<c:out value='${enteredEndDate}'/>" 
                                   required />
                        </div>
                    </div>

                    <!-- Reason Textarea -->
                    <div class="form-group">
                        <label for="reason">Reason for Leave <span style="color: var(--danger);">*</span></label>
                        <textarea id="reason" 
                                  name="reason" 
                                  class="form-control" 
                                  rows="4" 
                                  placeholder="Provide clear details explaining the need for leave..." 
                                  required><c:out value='${enteredReason}'/></textarea>
                    </div>

                    <!-- Form Action Buttons -->
                    <div style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 1.5rem;">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-primary">
                            Submit Leave Application
                        </button>
                    </div>
                </form>

            </div>
        </div>

    </main>

    <footer class="footer">
        <p>Employee Leave Management System &copy; College Project Submission</p>
    </footer>

    <!-- Client-side UX Date Synchronizer -->
    <script>
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');

        startDateInput.addEventListener('change', function() {
            if (startDateInput.value) {
                // Ensure end date picker cannot pick a date earlier than start date
                endDateInput.min = startDateInput.value;
                if (endDateInput.value && endDateInput.value < startDateInput.value) {
                    endDateInput.value = startDateInput.value;
                }
            }
        });
    </script>
</body>
</html>
