package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for testing the login functionality of SauceDemo website.
 */
public class LoginTest extends BaseTest {

    /**
     * Test valid login with correct credentials.
     * - Login with valid credentials
     * - Assert successful login to the inventory page
     */
    @Test
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.navigateToLoginPage()
                .login(VALID_USERNAME, VALID_PASSWORD);

        // Assert that login was successful and we're redirected to inventory page
        assertTrue(loginPage.isLoginSuccessful(), "Login should redirect to inventory page");
        assertTrue(page.url().contains("/inventory.html"), "URL should contain inventory.html after login");
    }

    /**
     * Test invalid login with incorrect credentials.
     * - Login with invalid credentials
     * - Assert the error message
     */
    @Test
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.navigateToLoginPage()
                .login(INVALID_USERNAME, INVALID_PASSWORD);

        // Assert that login failed and we see an error message
        assertFalse(loginPage.isLoginSuccessful(), "Login should fail with invalid credentials");

        String errorMessage = loginPage.getErrorMessage();
        assertNotNull(errorMessage, "Error message should be displayed");
        assertFalse(errorMessage.isEmpty(), "Error message should not be empty");
        assertTrue(errorMessage.contains("Epic sadface"), "Error message should contain 'Epic sadface'");
        assertTrue(errorMessage.contains("Username and password do not match"),
                "Error message should mention username and password not matching");
    }
}
