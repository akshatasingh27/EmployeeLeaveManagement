<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Dashboard - Leave Management System</title>
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
                <li><a href="${pageContext.request.contextPath}/dashboard" class="active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/apply-leave">Apply for Leave</a></li>
                <li><a href="${pageContext.request.contextPath}/leave-status">My Leave History</a></li>
            </ul>
            <div class="nav-user">
                <span class="user-badge">
                    👋 <strong><c:out value="${sessionScope.user.name}"/></strong> (<c:out value="${sessionScope.user.department}"/>)
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a>
            </div>
        </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">
        
        <!-- Page Header & Welcome Banner -->
        <div class="page-header">
            <div>
                <h1>Welcome, <c:out value="${sessionScope.user.name}"/>!</h1>
                <p>Track your leave balances, monitor pending requests, or apply for new time off.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/apply-leave" class="btn btn-primary">
                    <span>➕</span> Apply for Leave
                </a>
            </div>
        </div>

        <!-- Metric Summary Cards -->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Total Applications</div>
                    <div class="stat-value"><c:out value="${stats.total != null ? stats.total : 0}"/></div>
                </div>
                <div class="stat-icon blue">📁</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Pending Approval</div>
                    <div class="stat-value"><c:out value="${stats.pending != null ? stats.pending : 0}"/></div>
                </div>
                <div class="stat-icon amber">⏳</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Approved Requests</div>
                    <div class="stat-value"><c:out value="${stats.approved != null ? stats.approved : 0}"/></div>
                </div>
                <div class="stat-icon green">✅</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Rejected Requests</div>
                    <div class="stat-value"><c:out value="${stats.rejected != null ? stats.rejected : 0}"/></div>
                </div>
                <div class="stat-icon red">❌</div>
            </div>
        </section>

        <!-- Recent Leave Requests Section -->
        <section class="card">
            <div class="card-header">
                <h2>Recent Leave Requests</h2>
                <a href="${pageContext.request.contextPath}/leave-status" class="btn btn-secondary btn-sm">
                    View Full History &rarr;
                </a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty recentRequests}">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Application ID</th>
                                        <th>Leave Type</th>
                                        <th>Start Date</th>
                                        <th>End Date</th>
                                        <th>Duration</th>
                                        <th>Status</th>
                                        <th>Admin Remarks</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${recentRequests}">
                                        <tr>
                                            <td><strong>#<c:out value="${req.id}"/></strong></td>
                                            <td><c:out value="${req.leaveType}"/></td>
                                            <td><c:out value="${req.startDate}"/></td>
                                            <td><c:out value="${req.endDate}"/></td>
                                            <td><c:out value="${req.durationInDays}"/> day(s)</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${req.status eq 'APPROVED'}">
                                                        <span class="badge badge-approved">Approved</span>
                                                    </c:when>
                                                    <c:when test="${req.status eq 'REJECTED'}">
                                                        <span class="badge badge-rejected">Rejected</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-pending">Pending</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty req.adminRemark}">
                                                        <em><c:out value="${req.adminRemark}"/></em>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: #94a3b8;">&mdash;</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="text-align: center; padding: 2.5rem 1rem; color: #64748b;">
                            <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📝</div>
                            <h3>No leave requests found</h3>
                            <p style="margin-top: 0.25rem;">You haven't submitted any leave applications yet.</p>
                            <a href="${pageContext.request.contextPath}/apply-leave" class="btn btn-primary" style="margin-top: 1rem;">
                                Submit Your First Application
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

    </main>

    <footer class="footer">
        <p>Employee Leave Management System &copy; College Project Submission</p>
    </footer>

</body>
</html>
