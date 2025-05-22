package com.saucedemo.tests;

import com.saucedemo.pages.*;
import com.saucedemo.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for testing the checkout functionality of SauceDemo website.
 */
public class CheckoutTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutTest.class);

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private String expensiveItemName;
    private String cheapItemName;

    /**
     * Set up by logging in and adding items to cart before each test.
     */
    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage(page);
        loginPage.navigateToLoginPage()
                .login(VALID_USERNAME, VALID_PASSWORD);

        inventoryPage = new InventoryPage(page);

        // Validate inventory prices before proceeding
        try {
            inventoryPage.validateInventoryPrices();
        } catch (IllegalStateException e) {
            logger.error("Cannot proceed with test setup due to inventory validation failure: {}", e.getMessage());
            fail("Inventory validation failed: " + e.getMessage());
            return;
        }

        // Add most expensive item to cart
        try {
            expensiveItemName = inventoryPage.addMostExpensiveItemToCart();
            logger.info("Added most expensive item to cart: {}", expensiveItemName);
        } catch (IllegalStateException e) {
            logger.error("Failed to add most expensive item: {}", e.getMessage());
            fail("Failed to add most expensive item: " + e.getMessage());
            return;
        }

        // Add cheapest item to cart
        try {
            cheapItemName = inventoryPage.addCheapestItemToCart();
            logger.info("Added cheapest item to cart: {}", cheapItemName);
        } catch (IllegalStateException e) {
            logger.error("Failed to add cheapest item: {}", e.getMessage());
            fail("Failed to add cheapest item: " + e.getMessage());
            return;
        }

        // Navigate to cart
        cartPage = inventoryPage.navigateToCart();
    }

    /**
     * Test inventory price validation.
     * Verifies that the inventory has valid prices and handles edge cases.
     */
    @Test
    public void testInventoryPriceValidation() {
        try {
            inventoryPage.validateInventoryPrices();
            logger.info("Inventory price validation passed");
        } catch (IllegalStateException e) {
            logger.error("Inventory price validation failed: {}", e.getMessage());
            fail("Inventory price validation failed: " + e.getMessage());
        }
    }

    /**
     * Test checkout process.
     * - Go to Checkout page
     * - Remove the most expensive item from the cart
     * - Complete the checkout form with dummy data
     * - Verify the checkout completes and shows the confirmation message
     */
    @Test
    public void testCheckoutProcess() {
        // Verify both items are in cart
        Map<String, Double> cartItems = cartPage.getCartItems();
        assertEquals(2, cartItems.size(), "Cart should contain 2 items");
        assertTrue(cartItems.containsKey(expensiveItemName), "Cart should contain expensive item");
        assertTrue(cartItems.containsKey(cheapItemName), "Cart should contain cheap item");

        // Start checkout process
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        // Go back to cart to remove the most expensive item
        cartPage = checkoutPage.cancelCheckout().navigateToCart();

        // Remove the most expensive item
        cartPage.removeItemByName(expensiveItemName);

        // Verify only cheap item remains
        cartItems = cartPage.getCartItems();
        assertEquals(1, cartItems.size(), "Cart should contain 1 item after removal");
        assertFalse(cartItems.containsKey(expensiveItemName), "Expensive item should be removed");
        assertTrue(cartItems.containsKey(cheapItemName), "Cheap item should still be in cart");

        // Proceed to checkout again
        checkoutPage = cartPage.proceedToCheckout();

        // Generate random customer data for checkout
        String[] customerData = TestUtils.generateRandomUserData();

        // Complete checkout form
        checkoutPage.enterCustomerInfo(customerData[0], customerData[1], customerData[2])
                .clickContinue();

        // Verify checkout overview is displayed
        assertTrue(checkoutPage.isCheckoutOverviewDisplayed(), "Checkout overview should be displayed");

        // Complete checkout
        CheckoutCompletePage completePage = checkoutPage.finishCheckout();

        // Verify successful checkout
        assertTrue(completePage.isOrderConfirmationDisplayed(), "Order confirmation should be displayed");

        // Get and verify header text
        String headerText = completePage.getCompleteHeaderText();
        logger.info("Order confirmation header: '{}'", headerText);

        assertNotNull(headerText, "Complete header text should not be null");
        assertEquals("Thank you for your order!", headerText,
                "Complete header should match expected text exactly");

        // Verify header text contains expected phrases (case-insensitive)
        String headerTextLower = headerText.toLowerCase();
        assertTrue(headerTextLower.contains("thank"),
                "Complete header should contain 'thank' (case-insensitive)");
        assertTrue(headerTextLower.contains("order"),
                "Complete header should contain 'order' (case-insensitive)");

        // Get and verify complete text
        String completeText = completePage.getCompleteText();
        logger.info("Order confirmation text: '{}'", completeText);

        assertNotNull(completeText, "Complete text should not be null");
        assertTrue(completeText.contains("Your order has been dispatched"),
                "Complete text should mention order dispatched");
    }
}
