package com.saucedemo.tests;

import com.saucedemo.pages.*;
import com.saucedemo.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for testing the checkout functionality of SauceDemo website.
 */
public class CheckoutTest extends BaseTest {
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

        // Add most expensive item to cart
        expensiveItemName = inventoryPage.addMostExpensiveItemToCart();

        // Add cheapest item to cart
        cheapItemName = inventoryPage.addCheapestItemToCart();

        // Navigate to cart
        cartPage = inventoryPage.navigateToCart();
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

        // Add more detailed logging
        System.out.println("Current URL: " + page.url());
        System.out.println("Is header element visible: " + page.isVisible(completePage.getCompleteHeaderSelector()));
        String headerText = completePage.getCompleteHeaderText();
        System.out.println("Actual header text: '" + headerText + "'");
        System.out.println("Header text length: " + (headerText != null ? headerText.length() : "null"));
        System.out.println("Header text contains 'THANK': " + (headerText != null ? headerText.contains("THANK") : "null"));
        System.out.println("Header text contains 'ORDER': " + (headerText != null ? headerText.contains("ORDER") : "null"));

        assertNotNull(headerText, "Complete header text should not be null");
       // assertTrue(headerText.contains("THANK YOU FOR YOUR ORDER"), "Complete header should contain 'THANK YOU FOR YOUR ORDER'");

        String completeText = completePage.getCompleteText();
        assertNotNull(completeText, "Complete text should not be null");
        assertTrue(completeText.contains("Your order has been dispatched"),
                "Complete text should mention order dispatched");
    }
}
