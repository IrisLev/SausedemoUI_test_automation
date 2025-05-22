package com.saucedemo.pages;

import com.microsoft.playwright.Page;

/**
 * Page object representing the checkout pages of SauceDemo website.
 */
public class CheckoutPage extends BasePage {
    // Selectors for Checkout Step One
    private final String firstNameInputSelector = "#first-name";
    private final String lastNameInputSelector = "#last-name";
    private final String postalCodeInputSelector = "#postal-code";
    private final String continueButtonSelector = "#continue";
    private final String errorMessageSelector = "[data-test='error']";

    // Selectors for Checkout Step Two
    private final String finishButtonSelector = "#finish";
    private final String cancelButtonSelector = "#cancel";
    private final String summaryInfoSelector = ".summary_info";
    private final String summaryTotalSelector = ".summary_total_label";

    /**
     * Constructor for the CheckoutPage.
     *
     * @param page Playwright Page object
     */
    public CheckoutPage(Page page) {
        super(page);
    }

    /**
     * Enter customer information on the checkout form.
     *
     * @param firstName First name of the customer
     * @param lastName Last name of the customer
     * @param postalCode Postal code of the customer
     * @return This CheckoutPage for method chaining
     */
    public CheckoutPage enterCustomerInfo(String firstName, String lastName, String postalCode) {
        page.fill(firstNameInputSelector, firstName);
        page.fill(lastNameInputSelector, lastName);
        page.fill(postalCodeInputSelector, postalCode);
        return this;
    }

    /**
     * Click the continue button on the checkout form.
     *
     * @return This CheckoutPage for method chaining
     */
    public CheckoutPage clickContinue() {
        page.click(continueButtonSelector);
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
     * Check if the checkout overview is displayed.
     *
     * @return true if the checkout overview is displayed, false otherwise
     */
    public boolean isCheckoutOverviewDisplayed() {
        return elementExists(finishButtonSelector) && elementExists(summaryInfoSelector);
    }

    /**
     * Complete the checkout process by clicking the finish button.
     *
     * @return CheckoutCompletePage instance
     */
    public CheckoutCompletePage finishCheckout() {
        page.click(finishButtonSelector);
        return new CheckoutCompletePage(page);
    }

    /**
     * Cancel the checkout process by clicking the cancel button.
     *
     * @return InventoryPage instance
     */
    public InventoryPage cancelCheckout() {
        page.click(cancelButtonSelector);
        return new InventoryPage(page);
    }
}
