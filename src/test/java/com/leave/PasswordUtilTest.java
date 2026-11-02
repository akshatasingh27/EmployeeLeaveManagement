package com.leave;

import com.leave.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    @DisplayName("Password hashing produces consistent, non-empty 64-char SHA-256 hex string")
    void testHashPasswordFormat() {
        String hash = PasswordUtil.hashPassword("Password@123");
        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hex output must be exactly 64 characters");
        
        // Hashing the same input should yield identical hash (deterministic)
        String hashAgain = PasswordUtil.hashPassword("Password@123");
        assertEquals(hash, hashAgain);
    }

    @Test
    @DisplayName("Password verification succeeds for correct password and fails for incorrect")
    void testPasswordVerification() {
        String raw = "Admin@123";
        String hash = PasswordUtil.hashPassword(raw);

        assertTrue(PasswordUtil.verifyPassword("Admin@123", hash), "Correct password must verify successfully");
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hash), "Incorrect password must fail verification");
        assertFalse(PasswordUtil.verifyPassword("", hash), "Empty password must fail verification");
        assertFalse(PasswordUtil.verifyPassword(null, hash), "Null password must fail verification");
    }

    @Test
    @DisplayName("Seed admin hash matches pre-computed database.sql hash")
    void testSeedAdminHash() {
        String computedAdminHash = PasswordUtil.hashPassword("Admin@123");
        assertEquals("7b02aec37eaaa1e0cb000068cde59fdc2c61e3a6436735839ba77b77a830cb0b", computedAdminHash,
                "Admin seed hash in database.sql must match PasswordUtil computation");
    }
}
