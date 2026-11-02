<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Employee Leave Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <header class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/" class="nav-brand">
                <span class="brand-badge">ELMS</span>
                <span>Employee Leave Management System</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/login" class="active">Sign In</a></li>
            </ul>
        </div>
    </header>

    <main class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-header">
                <div class="logo-symbol">🏢</div>
                <h1>Welcome Back</h1>
                <p>Sign in to access your leave portal</p>
            </div>

            <!-- Notification Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    <span>⚠️</span>
                    <div><c:out value="${errorMessage}" /></div>
                </div>
            </c:if>

            <c:if test="${param.loggedOut eq 'true'}">
                <div class="alert alert-success" role="alert">
                    <span>✓</span>
                    <div>You have been safely logged out.</div>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="off">
                <!-- CSRF Protection Token -->
                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>" />

                <div class="form-group">
                    <label for="email">Work Email Address</label>
                    <input type="email" 
                           id="email" 
                           name="email" 
                           class="form-control" 
                           placeholder="e.g. john.doe@company.com" 
                           value="<c:out value='${enteredEmail}'/>" 
                           required 
                           autofocus />
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" 
                           id="password" 
                           name="password" 
                           class="form-control" 
                           placeholder="Enter your password" 
                           required />
                </div>

                <button type="submit" class="btn btn-primary btn-block" style="margin-top: 1.5rem;">
                    Sign In to Portal
                </button>
            </form>

            <!-- Demo Credentials for College Examiner & Viva Demonstration -->
            <div class="demo-credentials">
                <h4>🔑 Viva Demo Credentials</h4>
                <p><strong>Admin:</strong> <code>admin@company.com</code> / <code>Admin@123</code></p>
                <p><strong>Employee:</strong> <code>john.doe@company.com</code> / <code>Password@123</code></p>
                <p><strong>Employee:</strong> <code>jane.smith@company.com</code> / <code>Password@123</code></p>
            </div>
        </div>
    </main>

    <footer class="footer">
        <p>Employee Leave Management System &copy; College Project Submission</p>
    </footer>

</body>
</html>
