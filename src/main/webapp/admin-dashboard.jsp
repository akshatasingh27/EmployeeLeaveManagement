<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrator Dashboard - Leave Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Administrator Top Navigation -->
    <header class="navbar" style="background-color: #1e1b4b; border-bottom: 2px solid #4338ca;">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-brand">
                <span class="brand-badge" style="background: linear-gradient(135deg, #7c3aed, #4f46e5);">ADMIN</span>
                <span>Leave Management Console</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Overview & Requests</a></li>
            </ul>
            <div class="nav-user">
                <span class="user-badge" style="background-color: #312e81; border-color: #4338ca; color: #e0e7ff;">
                    🛡️ <strong><c:out value="${sessionScope.user.name}"/></strong> (Administrator)
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a>
            </div>
        </div>
    </header>

    <main class="main-content">
        
        <div class="page-header">
            <div>
                <h1>Organization Leave Overview</h1>
                <p>Manage, review, approve, or reject employee leave requests across all departments.</p>
            </div>
            <div>
                <span class="badge badge-role-admin" style="font-size: 0.85rem; padding: 0.5rem 1rem;">
                    Role: System Administrator
                </span>
            </div>
        </div>

        <!-- Flash Status Alerts -->
        <c:if test="${not empty flashMessage}">
            <div class="alert alert-${flashType != null ? flashType : 'info'}" role="alert">
                <span>
                    <c:choose>
                        <c:when test="${flashType eq 'success'}">✓</c:when>
                        <c:when test="${flashType eq 'danger'}">⚠️</c:when>
                        <c:otherwise>ℹ️</c:otherwise>
                    </c:choose>
                </span>
                <div><strong><c:out value="${flashMessage}"/></strong></div>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                <span>⚠️</span>
                <div><c:out value="${errorMessage}"/></div>
            </div>
        </c:if>

        <!-- Metric Summary Cards -->
        <section class="stats-grid" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Active Employees</div>
                    <div class="stat-value"><c:out value="${totalEmployees}"/></div>
                </div>
                <div class="stat-icon purple">👥</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Total Leave Requests</div>
                    <div class="stat-value"><c:out value="${stats.total != null ? stats.total : 0}"/></div>
                </div>
                <div class="stat-icon blue">📋</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Pending Decision</div>
                    <div class="stat-value"><c:out value="${stats.pending != null ? stats.pending : 0}"/></div>
                </div>
                <div class="stat-icon amber">⏳</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Approved</div>
                    <div class="stat-value"><c:out value="${stats.approved != null ? stats.approved : 0}"/></div>
                </div>
                <div class="stat-icon green">✅</div>
            </div>

            <div class="stat-card">
                <div class="stat-info">
                    <div class="stat-label">Rejected</div>
                    <div class="stat-value"><c:out value="${stats.rejected != null ? stats.rejected : 0}"/></div>
                </div>
                <div class="stat-icon red">❌</div>
            </div>
        </section>

        <!-- All Leave Requests Management Table -->
        <section class="card">
            <div class="card-header">
                <h2>All Employee Leave Applications (<c:out value="${leaveRequests != null ? leaveRequests.size() : 0}"/>)</h2>
                <div style="font-size: 0.85rem; color: #64748b;">
                    Showing real-time records from MySQL database
                </div>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty leaveRequests}">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Employee Details</th>
                                        <th>Leave Type</th>
                                        <th>Dates & Duration</th>
                                        <th>Reason for Leave</th>
                                        <th>Status</th>
                                        <th>Decision & Remarks</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${leaveRequests}">
                                        <tr style="${req.pending ? 'background-color: #fffdf5;' : ''}">
                                            <td><strong>#<c:out value="${req.id}"/></strong></td>
                                            <td>
                                                <div style="font-weight: 700; color: #0f172a;"><c:out value="${req.employeeName}"/></div>
                                                <div style="font-size: 0.8rem; color: #64748b;"><c:out value="${req.employeeEmail}"/></div>
                                                <div style="font-size: 0.75rem; color: #475569; margin-top: 2px;">
                                                    <span class="badge badge-role-employee" style="font-size: 0.7rem; padding: 0.15rem 0.45rem;">
                                                        <c:out value="${req.department}"/>
                                                    </span>
                                                </div>
                                            </td>
                                            <td><c:out value="${req.leaveType}"/></td>
                                            <td>
                                                <div><strong><c:out value="${req.startDate}"/></strong> to <strong><c:out value="${req.endDate}"/></strong></div>
                                                <div style="font-size: 0.8rem; color: #64748b;"><c:out value="${req.durationInDays}"/> day(s)</div>
                                            </td>
                                            <td style="max-width: 220px;">
                                                <span title="<c:out value='${req.reason}'/>">
                                                    <c:out value="${req.reason}"/>
                                                </span>
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
                                                        <span class="badge badge-pending">Pending Review</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="min-width: 280px;">
                                                <c:choose>
                                                    <%-- If request is PENDING, show Approval / Rejection form with remark input --%>
                                                    <c:when test="${req.status eq 'PENDING'}">
                                                        <form action="${pageContext.request.contextPath}/admin/leave-action" method="POST" style="display: flex; flex-direction: column; gap: 0.4rem;">
                                                            <!-- Security Tokens -->
                                                            <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>" />
                                                            <input type="hidden" name="requestId" value="<c:out value='${req.id}'/>" />

                                                            <!-- Optional Admin Remark -->
                                                            <input type="text" 
                                                                   name="adminRemark" 
                                                                   class="form-control" 
                                                                   placeholder="Add remark (optional)" 
                                                                   style="padding: 0.35rem 0.5rem; font-size: 0.82rem;" />

                                                            <div style="display: flex; gap: 0.4rem;">
                                                                <button type="submit" 
                                                                        name="action" 
                                                                        value="APPROVE" 
                                                                        class="btn btn-success btn-sm" 
                                                                        title="Approve this leave request">
                                                                    ✓ Approve
                                                                </button>
                                                                <button type="submit" 
                                                                        name="action" 
                                                                        value="REJECT" 
                                                                        class="btn btn-danger btn-sm" 
                                                                        title="Reject this leave request">
                                                                    ✕ Reject
                                                                </button>
                                                            </div>
                                                        </form>
                                                    </c:when>
                                                    <%-- If request has already been processed --%>
                                                    <c:otherwise>
                                                        <div style="font-size: 0.85rem;">
                                                            <c:if test="${not empty req.adminRemark}">
                                                                <div style="background: #f8fafc; padding: 0.35rem 0.55rem; border-radius: 4px; border: 1px solid #e2e8f0; color: #334155;">
                                                                    <strong>Remark:</strong> <em><c:out value="${req.adminRemark}"/></em>
                                                                </div>
                                                            </c:if>
                                                            <div style="color: #94a3b8; font-size: 0.75rem; margin-top: 0.25rem;">
                                                                Processed on <c:out value="${req.updatedAt != null ? req.updatedAt : req.createdAt}"/>
                                                            </div>
                                                        </div>
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
                            <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📂</div>
                            <h3>No leave requests found</h3>
                            <p style="margin-top: 0.25rem;">No employees have submitted leave requests yet.</p>
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
