package com.saucedemo.pages;

import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;

/**
 * Page object for the login page.
 */
public class LoginPage extends BasePage {
    // Selectors
    private final String usernameInputSelector = "#user-name";
    private final String passwordInputSelector = "#password";
    private final String loginButtonSelector = "#login-button";
    private final String errorMessageSelector = "[data-test='error']";

    /**
     * Constructor for the LoginPage.
     *
     * @param page Playwright Page object
     */
    public LoginPage(Page page) {
        super(page);
    }

    /**
     * Navigate to the login page.
     *
     * @return This LoginPage for method chaining
     */
    public LoginPage navigateToLoginPage() {
        navigateToBaseUrl();
        return this;
    }

    /**
     * Enter username in the username field.
     *
     * @param username Username to enter
     * @return This LoginPage for method chaining
     */
    public LoginPage enterUsername(String username) {
        page.fill(usernameInputSelector, username);
        return this;
    }

    /**
     * Enter password in the password field.
     *
     * @param password Password to enter
     * @return This LoginPage for method chaining
     */
    public LoginPage enterPassword(String password) {
        page.fill(passwordInputSelector, password);
        return this;
    }

    /**
     * Click the login button.
     *
     * @return This LoginPage for method chaining
     */
    public LoginPage clickLoginButton() {
        page.click(loginButtonSelector);
        return this;
    }

    /**
     * Perform login with provided credentials.
     *
     * @param username Username for login
     * @param password Password for login
     * @return This LoginPage for method chaining
     */
    public LoginPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        return this;
    }

    /**
     * Get the error message if login fails.
     * @return The error message text
     */
    public String getErrorMessage() {
        return page.locator("[data-test='error']").textContent();
    }

    /**
     * Check if login was successful by verifying redirection to inventory page.
     *
     * @return true if login was successful, false otherwise
     */
    public boolean isLoginSuccessful() {
        return getCurrentUrl().contains("/inventory.html");
    }

    /**
     * Check if the user is logged in.
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return !isOnLoginPage() && page.url().contains("/inventory.html");
    }

    /**
     * Check if currently on the login page.
     * @return true if on login page, false otherwise
     */
    public boolean isOnLoginPage() {
        return page.url().equals(baseUrl) || page.url().equals(baseUrl + "/");
    }

    /**
     * Enter username without submitting.
     * @param username The username to enter
     */
    public void fillUsername(String username) {
        page.fill("#user-name", username);
    }

    /**
     * Enter password without submitting.
     * @param password The password to enter
     */
    public void fillPassword(String password) {
        page.fill("#password", password);
    }

    /**
     * Login with username and password.
     * @param username The username to login with
     * @param password The password to login with
     */
    public void login(String username, String password) {
        fillUsername(username);
        fillPassword(password);
        page.click("#login-button");
    }
}