package com.leave.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

/**
 * CSRF (Cross-Site Request Forgery) protection utility.
 * 
 * In a college viva, explain:
 * - CSRF is an attack where an unauthorized command is transmitted from a user that the web application trusts.
 * - We protect our application by generating a random, unpredictable token for each user session.
 * - When submitting sensitive POST forms (like leave requests or admin decisions), the client includes this token.
 * - The server compares the submitted token with the token stored in the user's session.
 */
public class CsrfUtil {

    public static final String CSRF_SESSION_KEY = "csrfToken";
    public static final String CSRF_PARAM_NAME = "csrfToken";

    /**
     * Retrieves the existing CSRF token from session or generates a new one.
     */
    public static String ensureToken(HttpSession session) {
        if (session == null) {
            return "";
        }
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null || token.trim().isEmpty()) {
            token = UUID.randomUUID().toString();
            session.setAttribute(CSRF_SESSION_KEY, token);
        }
        return token;
    }

    /**
     * Validates the submitted CSRF token against the one stored in the current session.
     */
    public static boolean isValidToken(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        String requestToken = request.getParameter(CSRF_PARAM_NAME);

        if (sessionToken == null || requestToken == null) {
            return false;
        }
        return sessionToken.equals(requestToken.trim());
    }
}
