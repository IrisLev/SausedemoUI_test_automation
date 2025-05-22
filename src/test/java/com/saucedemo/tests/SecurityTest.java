package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.security.CredentialManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Security test scenarios for the SauceDemo application.
 * Tests various security aspects including authentication, input validation, and XSS protection.
 */
public class SecurityTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage(page);
        navigateWithRetry(TestConfig.getBaseUrl());
    }

    @Test
    @DisplayName("Test locked out user cannot login")
    public void testLockedOutUser() {
        loginPage.login("locked_out_user", CredentialManager.getPassword("locked_out_user"));
        assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Locked out user should not be able to login");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<script>alert('xss')</script>",
            "'; DROP TABLE users; --",
            "' OR '1'='1",
            "admin' --",
            "admin' #",
            "admin'/*",
            "' UNION SELECT * FROM users; --"
    })
    @DisplayName("Test SQL injection and XSS attempts")
    public void testInjectionAttempts(String maliciousInput) {
        loginPage.login(maliciousInput, maliciousInput);
        // Should not crash or expose sensitive information
        assertFalse(loginPage.getErrorMessage().toLowerCase().contains("sql"),
                "Error message should not expose SQL details");
        assertFalse(loginPage.getErrorMessage().toLowerCase().contains("syntax"),
                "Error message should not expose syntax details");
    }

    @Test
    @DisplayName("Test empty credentials")
    public void testEmptyCredentials() {
        loginPage.login("", "");
        assertTrue(loginPage.getErrorMessage().contains("required"),
                "Empty credentials should be rejected");
    }

    @Test
    @DisplayName("Test very long input")
    public void testLongInput() {
        String longInput = "a".repeat(1000);
        loginPage.login(longInput, longInput);
        // Should handle long input gracefully
        assertTrue(loginPage.getErrorMessage().contains("invalid"),
                "Long input should be rejected with appropriate message");
    }

    @Test
    @DisplayName("Test special characters in credentials")
    public void testSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        loginPage.login(specialChars, specialChars);
        // Should handle special characters appropriately
        assertTrue(loginPage.getErrorMessage().contains("invalid"),
                "Special characters should be handled appropriately");
    }

    @Test
    @DisplayName("Test multiple failed login attempts")
    public void testMultipleFailedAttempts() {
        for (int i = 0; i < 5; i++) {
            loginPage.login("invalid_user", "wrong_password");
            assertTrue(loginPage.getErrorMessage().contains("invalid"),
                    "Failed login attempt should show appropriate message");
        }
        // Should not lock out after multiple attempts (unless specifically configured)
        loginPage.login("standard_user", CredentialManager.getPassword("standard_user"));
        assertTrue(loginPage.isLoggedIn(), "Valid user should still be able to login after multiple failed attempts");
    }

    @Test
    @DisplayName("Test session handling")
    public void testSessionHandling() {
        // Login with valid credentials
        loginPage.login("standard_user", CredentialManager.getPassword("standard_user"));
        assertTrue(loginPage.isLoggedIn(), "Should be able to login with valid credentials");

        // Clear cookies and try to access inventory
        page.context().clearCookies();
        page.reload();
        
        // Should be redirected to login
        assertTrue(loginPage.isOnLoginPage(), "Should be redirected to login page after clearing cookies");
    }

    @Test
    @DisplayName("Test password masking")
    public void testPasswordMasking() {
        String password = CredentialManager.getPassword("standard_user");
        loginPage.fillUsername("standard_user");
        loginPage.fillPassword(password);
        
        // Check if password is masked in the DOM
        String passwordFieldType = page.locator("#password").getAttribute("type");
        assertEquals("password", passwordFieldType,
                "Password field should be of type 'password'");
    }
} 