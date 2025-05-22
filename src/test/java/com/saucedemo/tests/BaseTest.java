package com.saucedemo.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base test class that all test classes will inherit from.
 * Sets up and tears down Playwright resources with comprehensive logging.
 * Supports parallel test execution.
 */
@Execution(ExecutionMode.CONCURRENT)
public class BaseTest {
    // Logger instance
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    // Playwright components
    protected static Playwright playwright;
    protected static Browser browser;

    // Test-specific components
    protected BrowserContext context;
    protected Page page;

    // Network handling configuration
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int DEFAULT_TIMEOUT_MS = 30000;
    private static final List<String> IGNORED_ERROR_PATTERNS = List.of(
            ".*401.*",  // Ignore unauthorized errors
            ".*favicon.ico.*",  // Ignore favicon errors
            ".*analytics.*"  // Ignore analytics errors
    );

    // Test credentials
    protected static final String VALID_USERNAME = "standard_user";
    protected static final String VALID_PASSWORD = "secret_sauce";
    protected static final String INVALID_USERNAME = "invalid_user";
    protected static final String INVALID_PASSWORD = "invalid_password";

    // Track failed requests for reporting
    private final List<FailedRequest> failedRequests = new ArrayList<>();

    /**
     * Class to track failed network requests
     */
    private static class FailedRequest {
        final String url;
        final int status;
        final String error;

        FailedRequest(String url, int status, String error) {
            this.url = url;
            this.status = status;
            this.error = error;
        }

        @Override
        public String toString() {
            return String.format("URL: %s, Status: %d, Error: %s", url, status, error);
        }
    }

    /**
     * Set up browser before all tests.
     * Browser instance is shared across all test threads
     */
    @BeforeAll
    public static void launchBrowser() {
        logger.info("=== Starting Test Suite ===");
        logger.info("Launching Chromium browser...");

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setSlowMo(50));

        logger.info("Browser launched successfully");
    }

    /**
     * Set up context and page before each test.
     * Each test gets its own isolated browser context
     */
    @BeforeEach
    public void createContextAndPage(TestInfo testInfo) {
        logger.info("--- Starting Test: {} ---", testInfo.getDisplayName());
        logger.debug("Creating new browser context and page");

        try {
            // Create context with network handling
            context = browser.newContext(new Browser.NewContextOptions()
                    .setIgnoreHTTPSErrors(true));

            // Create page first
            page = context.newPage();

            // Then set timeout and monitoring
            page.setDefaultTimeout(DEFAULT_TIMEOUT_MS);
            setupNetworkMonitoring();

            logger.debug("Browser context and page created successfully");
        } catch (Exception e) {
            logger.error("Failed to create browser context and page: {}", e.getMessage());
            // Clean up if something went wrong
            if (context != null) {
                context.close();
            }
            throw e;
        }
    }

    /**
     * Set up network request monitoring and handling.
     */
    private void setupNetworkMonitoring() {
        // Monitor all requests
        page.onRequest(request -> {
            String url = request.url();
            if (shouldIgnoreRequest(url)) {
                logger.debug("Ignoring request to: {}", url);
                return;
            }
            logger.debug("Request: {} {}", request.method(), url);
        });

        // Monitor all responses
        page.onResponse(response -> {
            String url = response.url();
            if (shouldIgnoreRequest(url)) {
                return;
            }

            int status = response.status();
            if (status >= 400) {
                String error = String.format("Request failed: %s %s", response.request().method(), url);
                logger.warn("Response error: {} - Status: {}", error, status);
                failedRequests.add(new FailedRequest(url, status, error));
            }
        });

        // Monitor console messages
        page.onConsoleMessage(msg -> {
            if (!shouldIgnoreConsoleMessage(msg.text())) {
                logger.debug("Browser Console [{}]: {}", msg.type(), msg.text());
            }
        });

        // Monitor page errors
        page.onPageError(error -> {
            logger.error("Page Error: {}", error);
            failedRequests.add(new FailedRequest(page.url(), 0, error));
        });
    }

    /**
     * Check if a request should be ignored based on URL patterns.
     */
    private boolean shouldIgnoreRequest(String url) {
        return IGNORED_ERROR_PATTERNS.stream()
                .anyMatch(pattern -> url.matches(pattern));
    }

    /**
     * Check if a console message should be ignored.
     */
    private boolean shouldIgnoreConsoleMessage(String message) {
        return IGNORED_ERROR_PATTERNS.stream()
                .anyMatch(pattern -> message.matches(pattern));
    }

    /**
     * Retry a page action with network error handling.
     *
     * @param action The action to retry
     * @param description Description of the action for logging
     */
    protected void retryOnFailure(Runnable action, String description) {
        AtomicInteger attempts = new AtomicInteger(0);
        while (attempts.get() < MAX_RETRIES) {
            try {
                action.run();
                // Wait for network to be idle
                page.waitForLoadState(LoadState.NETWORKIDLE);
                return;
            } catch (PlaywrightException e) {
                attempts.incrementAndGet();
                if (attempts.get() == MAX_RETRIES) {
                    logger.error("Failed to execute '{}' after {} attempts: {}",
                            description, MAX_RETRIES, e.getMessage());
                    throw e;
                }
                logger.warn("Attempt {} failed for '{}', retrying...", attempts.get(), description);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PlaywrightException("Retry interrupted", ie);
                }
            }
        }
    }

    /**
     * Close context after each test and report any network issues.
     */
    @AfterEach
    public void closeContext(TestInfo testInfo) {
        logger.info("--- Test Completed: {} ---", testInfo.getDisplayName());

        // Report any network issues
        if (!failedRequests.isEmpty()) {
            logger.warn("Network issues encountered during test:");
            failedRequests.forEach(request ->
                    logger.warn("  - {}", request));
        }

        logger.debug("Closing browser context");
        context.close();
        failedRequests.clear();
    }

    /**
     * Close browser and Playwright after all tests.
     */
    @AfterAll
    public static void closeBrowser() {
        logger.info("Closing browser and Playwright");
        browser.close();
        playwright.close();
        logger.info("=== Test Suite Completed ===");
    }

    /**
     * Log page navigation for debugging purposes.
     *
     * @param url URL being navigated to
     */
    protected void logNavigation(String url) {
        logger.info("Navigating to: {}", url);
    }

    /**
     * Log user actions for debugging purposes.
     *
     * @param action Description of the action being performed
     */
    protected void logAction(String action) {
        logger.info("Action: {}", action);
    }

    /**
     * Log assertions for debugging purposes.
     *
     * @param assertion Description of what is being asserted
     */
    protected void logAssertion(String assertion) {
        logger.info("Asserting: {}", assertion);
    }

    /**
     * Navigate to a URL with retry mechanism.
     *
     * @param url URL to navigate to
     */
    protected void navigateWithRetry(String url) {
        retryOnFailure(() -> {
            logNavigation(url);
            page.navigate(url);
        }, "Navigate to " + url);
    }

    /**
     * Click an element with retry mechanism.
     *
     * @param selector Element selector
     * @param description Description of the action
     */
    protected void clickWithRetry(String selector, String description) {
        retryOnFailure(() -> {
            logAction("Clicking " + description);
            page.click(selector);
        }, "Click " + description);
    }

    /**
     * Fill a form field with retry mechanism.
     *
     * @param selector Field selector
     * @param value Value to fill
     * @param description Description of the field
     */
    protected void fillWithRetry(String selector, String value, String description) {
        retryOnFailure(() -> {
            logAction("Filling " + description);
            page.fill(selector, value);
        }, "Fill " + description);
    }
}
