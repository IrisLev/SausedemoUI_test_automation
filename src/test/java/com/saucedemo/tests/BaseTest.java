package com.saucedemo.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

/**
 * Base test class that all test classes will inherit from.
 * Sets up and tears down Playwright resources.
 */
public class BaseTest {
    // Playwright components
    protected static Playwright playwright;
    protected static Browser browser;

    // Test-specific components
    protected BrowserContext context;
    protected Page page;

    // Test credentials
    protected static final String VALID_USERNAME = "standard_user";
    protected static final String VALID_PASSWORD = "secret_sauce";
    protected static final String INVALID_USERNAME = "invalid_user";
    protected static final String INVALID_PASSWORD = "invalid_password";

    /**
     * Set up browser before all tests.
     */
    @BeforeAll
    public static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setSlowMo(50));
    }

    /**
     * Set up context and page before each test.
     */
    @BeforeEach
    public void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    /**
     * Close context after each test.
     */
    @AfterEach
    public void closeContext() {
        context.close();
    }

    /**
     * Close browser and Playwright after all tests.
     */
    @AfterAll
    public static void closeBrowser() {
        browser.close();
        playwright.close();
    }
}