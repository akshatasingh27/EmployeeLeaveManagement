<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Leave History - Employee Leave Management System</title>
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
                <li><a href="${pageContext.request.contextPath}/apply-leave">Apply for Leave</a></li>
                <li><a href="${pageContext.request.contextPath}/leave-status" class="active">My Leave History</a></li>
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
                <h1>My Leave Application History</h1>
                <p>Track the approval status and administrative remarks for all your requests.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/apply-leave" class="btn btn-primary">
                    <span>➕</span> Apply New Leave
                </a>
            </div>
        </div>

        <!-- Success Toast Alert after Submission -->
        <c:if test="${param.submitted eq 'true'}">
            <div class="alert alert-success" role="alert">
                <span>🎉</span>
                <div><strong>Success!</strong> Your leave application was submitted successfully and is currently pending administrator review.</div>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                <span>⚠️</span>
                <div><c:out value="${errorMessage}"/></div>
            </div>
        </c:if>

        <!-- Leave Requests Table Card -->
        <div class="card">
            <div class="card-header">
                <h2>Application Records (<c:out value="${leaveList != null ? leaveList.size() : 0}"/>)</h2>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty leaveList}">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Application ID</th>
                                        <th>Leave Category</th>
                                        <th>Period</th>
                                        <th>Duration</th>
                                        <th>Reason Provided</th>
                                        <th>Submission Date</th>
                                        <th>Status</th>
                                        <th>Manager / Admin Remarks</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${leaveList}">
                                        <tr>
                                            <td><strong>#<c:out value="${req.id}"/></strong></td>
                                            <td><c:out value="${req.leaveType}"/></td>
                                            <td>
                                                <strong><c:out value="${req.startDate}"/></strong> to 
                                                <strong><c:out value="${req.endDate}"/></strong>
                                            </td>
                                            <td><c:out value="${req.durationInDays}"/> day(s)</td>
                                            <td style="max-width: 250px;">
                                                <span title="<c:out value='${req.reason}'/>">
                                                    <c:out value="${req.reason}"/>
                                                </span>
                                            </td>
                                            <td style="font-size: 0.85rem; color: #64748b;">
                                                <c:out value="${req.createdAt}"/>
                                            </td>
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
                                                        <div style="background: #f1f5f9; padding: 0.4rem 0.6rem; border-radius: 4px; font-size: 0.85rem; border-left: 3px solid #3b82f6;">
                                                            <em><c:out value="${req.adminRemark}"/></em>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: #94a3b8; font-size: 0.85rem;">Awaiting review</span>
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
                        <div style="text-align: center; padding: 3rem 1rem; color: #64748b;">
                            <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🏖️</div>
                            <h3>No leave records to display</h3>
                            <p style="margin-top: 0.25rem;">You have not applied for any leave yet.</p>
                            <a href="${pageContext.request.contextPath}/apply-leave" class="btn btn-primary" style="margin-top: 1rem;">
                                Apply for Leave Now
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </main>

    <footer class="footer">
        <p>Employee Leave Management System &copy; College Project Submission</p>
    </footer>

</body>
</html>
