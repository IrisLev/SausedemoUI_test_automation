package com.saucedemo.tests;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for testing the cart functionality of SauceDemo website.
 */
public class CartTest extends BaseTest {
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
     * Test adding items to cart.
     * - Add the most expensive item to the cart
     * - Add the most cheap item to the cart
     * - Verify the items were added to the cart
     * - Assert correct items names and prices
     */
    @Test
    public void testAddItemsToCart() {
        // Get the most expensive item and add it to cart
        Map.Entry<String, Double> mostExpensiveItem = inventoryPage.getMostExpensiveItem();
        assertNotNull(mostExpensiveItem, "Most expensive item should not be null");

        expensiveItemName = mostExpensiveItem.getKey();
        expensiveItemPrice = mostExpensiveItem.getValue();

        inventoryPage.addItemToCartByName(expensiveItemName);

        // Get the cheapest item and add it to cart
        Map.Entry<String, Double> cheapestItem = inventoryPage.getCheapestItem();
        assertNotNull(cheapestItem, "Cheapest item should not be null");

        cheapItemName = cheapestItem.getKey();
        cheapItemPrice = cheapestItem.getValue();

        inventoryPage.addItemToCartByName(cheapItemName);

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
}
