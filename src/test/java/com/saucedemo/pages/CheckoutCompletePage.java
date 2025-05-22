package com.saucedemo.pages;

import com.microsoft.playwright.Page;

/**
 * Page object representing the checkout complete page of SauceDemo website.
 */
public class CheckoutCompletePage extends BasePage {
    // Selectors
    private final String completeHeaderSelector = ".complete-header";
    private final String completeTextSelector = ".complete-text";
    private final String backHomeButtonSelector = "#back-to-products";

    /**
     * Get the complete header selector.
     *
     * @return The CSS selector for the complete header
     */
    public String getCompleteHeaderSelector() {
        return completeHeaderSelector;
    }

    /**
     * Constructor for the CheckoutCompletePage.
     *
     * @param page Playwright Page object
     */
    public CheckoutCompletePage(Page page) {
        super(page);
    }

    /**
     * Get the header text of the checkout complete page.
     *
     * @return Header text
     */
    public String getCompleteHeaderText() {
        page.waitForSelector(completeHeaderSelector);
        return page.textContent(completeHeaderSelector);
    }

    /**
     * Get the complete text of the checkout complete page.
     *
     * @return Complete text
     */
    public String getCompleteText() {
        return page.textContent(completeTextSelector);
    }

    /**
     * Check if the order confirmation is displayed.
     *
     * @return true if the order confirmation is displayed, false otherwise
     */
    public boolean isOrderConfirmationDisplayed() {
        return elementExists(completeHeaderSelector) && elementExists(completeTextSelector);
    }
}
