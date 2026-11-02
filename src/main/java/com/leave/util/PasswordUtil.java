package com.leave.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing and verifying passwords.
 * Uses SHA-256 with a pre-configured application salt.
 * 
 * In a college viva, explain:
 * 1. Plaintext passwords are never stored in the database for security reasons.
 * 2. Hashing is a one-way mathematical function: password -> hash.
 * 3. Adding a "salt" prevents dictionary attacks and pre-computed rainbow table lookups.
 */
public class PasswordUtil {

    // Application salt to strengthen password hashes
    private static final String SALT = "ELMS_SECURE_SALT_2026";

    /**
     * Hashes a plaintext password using SHA-256 and the application salt.
     * 
     * @param plainPassword Raw user password
     * @return 64-character hexadecimal SHA-256 hash
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Add salt first
            md.update(SALT.getBytes(StandardCharsets.UTF_8));
            // Digest the password bytes
            byte[] hashedBytes = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm is not available in JVM", e);
        }
    }

    /**
     * Compares a raw password against an existing database hash.
     * 
     * @param rawPassword User-entered plaintext password
     * @param storedHash Hash stored in the database
     * @return true if matches, false otherwise
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || rawPassword.trim().isEmpty() || storedHash == null) {
            return false;
        }
        String computedHash = hashPassword(rawPassword);
        return computedHash.equalsIgnoreCase(storedHash.trim());
    }

    /**
     * Small helper main method to generate hashes for initial database seeds.
     */
    public static void main(String[] args) {
        String[] passwords = {"Admin@123", "Password@123", "john@123", "jane@123"};
        for (String p : passwords) {
            System.out.println(p + " => " + hashPassword(p));
        }
    }
}
