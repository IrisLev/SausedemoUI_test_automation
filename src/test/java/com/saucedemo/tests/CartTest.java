package com.saucedemo.tests;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for testing the cart functionality of SauceDemo website.
 */
public class CartTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(CartTest.class);
    
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private String expensiveItemName;
    private String cheapItemName;
    private double expensiveItemPrice;
    private double cheapItemPrice;

    /**
     * Set up by logging in before each test.
     */
    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage(page);
        loginPage.navigateToLoginPage()
                .login(VALID_USERNAME, VALID_PASSWORD);

        inventoryPage = new InventoryPage(page);
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
     * Test adding items to cart with price validation.
     * - Validates inventory prices
     * - Add the most expensive item to the cart
     * - Add the most cheap item to the cart
     * - Verify the items were added to the cart
     * - Assert correct items names and prices
     */
    @Test
    public void testAddItemsToCart() {
        // Validate inventory prices first
        try {
            inventoryPage.validateInventoryPrices();
        } catch (IllegalStateException e) {
            logger.error("Cannot proceed with test due to inventory validation failure: {}", e.getMessage());
            fail("Inventory validation failed: " + e.getMessage());
            return;
        }

        // Get the most expensive item and add it to cart
        try {
            Map.Entry<String, Double> mostExpensiveItem = inventoryPage.getMostExpensiveItem();
            expensiveItemName = mostExpensiveItem.getKey();
            expensiveItemPrice = mostExpensiveItem.getValue();
            logger.info("Adding most expensive item to cart: {} (${})", expensiveItemName, expensiveItemPrice);
            inventoryPage.addItemToCartByName(expensiveItemName);
        } catch (IllegalStateException e) {
            logger.error("Failed to add most expensive item: {}", e.getMessage());
            fail("Failed to add most expensive item: " + e.getMessage());
            return;
        }

        // Get the cheapest item and add it to cart
        try {
            Map.Entry<String, Double> cheapestItem = inventoryPage.getCheapestItem();
            cheapItemName = cheapestItem.getKey();
            cheapItemPrice = cheapestItem.getValue();
            logger.info("Adding cheapest item to cart: {} (${})", cheapItemName, cheapItemPrice);
            inventoryPage.addItemToCartByName(cheapItemName);
        } catch (IllegalStateException e) {
            logger.error("Failed to add cheapest item: {}", e.getMessage());
            fail("Failed to add cheapest item: " + e.getMessage());
            return;
        }

        // Verify cart count is 2
        assertEquals(2, inventoryPage.getCartItemCount(), "Cart should contain 2 items");

        // Navigate to cart and verify items
        CartPage cartPage = inventoryPage.navigateToCart();
        Map<String, Double> cartItems = cartPage.getCartItems();

        // Assert items are in cart
        assertTrue(cartItems.containsKey(expensiveItemName),
                "Cart should contain the expensive item: " + expensiveItemName);
        assertTrue(cartItems.containsKey(cheapItemName),
                "Cart should contain the cheap item: " + cheapItemName);

        // Assert correct prices
        assertEquals(expensiveItemPrice, cartItems.get(expensiveItemName), 0.01,
                "Expensive item price should match");
        assertEquals(cheapItemPrice, cartItems.get(cheapItemName), 0.01,
                "Cheap item price should match");
    }

    /**
     * Test removing most expensive item and proceeding to checkout.
     * - Adds most expensive and cheapest items to cart
     * - Navigates to cart page
     * - Removes the most expensive item
     * - Verifies only cheapest item remains
     * - Proceeds to checkout
     */
    @Test
    public void testRemoveMostExpensiveAndCheckout() {
        // First add both items to cart
        try {
            Map.Entry<String, Double> mostExpensiveItem = inventoryPage.getMostExpensiveItem();
            expensiveItemName = mostExpensiveItem.getKey();
            expensiveItemPrice = mostExpensiveItem.getValue();
            logger.info("Adding most expensive item to cart: {} (${})", expensiveItemName, expensiveItemPrice);
            inventoryPage.addItemToCartByName(expensiveItemName);

            Map.Entry<String, Double> cheapestItem = inventoryPage.getCheapestItem();
            cheapItemName = cheapestItem.getKey();
            cheapItemPrice = cheapestItem.getValue();
            logger.info("Adding cheapest item to cart: {} (${})", cheapItemName, cheapItemPrice);
            inventoryPage.addItemToCartByName(cheapItemName);
        } catch (IllegalStateException e) {
            logger.error("Failed to add items to cart: {}", e.getMessage());
            fail("Failed to add items to cart: " + e.getMessage());
            return;
        }

        // Navigate to cart
        CartPage cartPage = inventoryPage.navigateToCart();
        
        // Verify initial cart state
        assertEquals(2, cartPage.getCartItemCount(), "Cart should initially contain 2 items");
        
        // Remove most expensive item
        String removedItem = cartPage.removeMostExpensiveItem();
        logger.info("Removed most expensive item: {}", removedItem);
        
        // Verify correct item was removed
        assertEquals(expensiveItemName, removedItem, "Should have removed the most expensive item");
        
        // Verify cart now only contains cheapest item
        assertEquals(1, cartPage.getCartItemCount(), "Cart should now contain only 1 item");
        assertTrue(cartPage.isItemInCart(cheapItemName), "Cheapest item should still be in cart");
        assertFalse(cartPage.isItemInCart(expensiveItemName), "Most expensive item should be removed");
        
        // Proceed to checkout
        cartPage.proceedToCheckout();
        logger.info("Proceeded to checkout page");
    }
}
