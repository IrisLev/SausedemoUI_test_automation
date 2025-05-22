package com.saucedemo.pages;

import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;

/**
 /* Base page class that all page objects will inherit from.
 * Contains common methods and properties for all pages.
 */
public class BasePage {
    protected final Page page;
    protected final String baseUrl = TestConfig.getBaseUrl();


    public BasePage(Page page) {
        this.page = page;
    }

    public void navigateToBaseUrl() {
        page.navigate(baseUrl);
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public boolean elementExists(String selector) {
        return page.querySelector(selector) != null;
    }

    /**
     * Wait for navigation to complete.
     */
}
