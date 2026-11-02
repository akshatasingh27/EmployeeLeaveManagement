package com.leave;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Security Filter that intercepts incoming HTTP requests to ensure:
 * 1. Unauthenticated users cannot access protected pages.
 * 2. Regular employees cannot access admin pages.
 * 3. Direct access to internal JSP views is prevented.
 * 
 * In a college viva, explain:
 * - A Servlet Filter acts as a pre-processing and post-processing gatekeeper for requests.
 * - If the filter finds no active session, it redirects to the login page immediately.
 * - If an employee attempts to access an administrator route, it blocks the request.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/dashboard", "/apply-leave", "/leave-status", "/admin/*", "*.jsp"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Whitelisted public resources
        boolean isPublic = path.equals("/") ||
                           path.equals("/index.jsp") ||
                           path.equals("/login") ||
                           path.equals("/login.jsp") ||
                           path.equals("/logout") ||
                           path.startsWith("/css/") ||
                           path.startsWith("/js/") ||
                           path.startsWith("/images/");

        if (isPublic) {
            chain.doFilter(req, res);
            return;
        }

        // Prevent direct browser visits to protected internal JSP pages (Enforce MVC pattern)
        if (path.endsWith(".jsp") && !path.equals("/index.jsp") && !path.equals("/login.jsp")) {
            HttpSession session = request.getSession(false);
            Employee user = (session != null) ? (Employee) session.getAttribute("user") : null;
            if (user != null) {
                if (user.isAdmin()) {
                    response.sendRedirect(contextPath + "/admin/dashboard");
                } else {
                    response.sendRedirect(contextPath + "/dashboard");
                }
            } else {
                response.sendRedirect(contextPath + "/login");
            }
            return;
        }

        // Check authentication
        HttpSession session = request.getSession(false);
        Employee user = (session != null) ? (Employee) session.getAttribute("user") : null;

        if (user == null) {
            // Not logged in: redirect to login
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // Check Admin route authorization
        if (path.startsWith("/admin/")) {
            if (!user.isAdmin()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Administrator role required.");
                return;
            }
        }

        // Pass control to next filter or servlet in the chain
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
