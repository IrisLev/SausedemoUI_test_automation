package com.saucedemo.pages;

import com.microsoft.playwright.Page;

/**
 * Page object representing the login page of SauceDemo website.
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
     * Get error message text if present.
     *
     * @return Error message text or empty string if no error
     */
    public String getErrorMessage() {
        if (elementExists(errorMessageSelector)) {
            return page.textContent(errorMessageSelector);
        }
        return "";
    }

    /**
     * Check if login was successful by verifying redirection to inventory page.
     *
     * @return true if login was successful, false otherwise
     */
    public boolean isLoginSuccessful() {
        return getCurrentUrl().contains("/inventory.html");
    }
}