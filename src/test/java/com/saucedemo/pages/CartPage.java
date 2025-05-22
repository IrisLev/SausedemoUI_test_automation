package com.saucedemo.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page object representing the cart page of SauceDemo website.
 */
public class CartPage extends BasePage {
    // Selectors
    private final String cartItemSelector = ".cart_item";
    private final String itemNameSelector = ".inventory_item_name";
    private final String itemPriceSelector = ".inventory_item_price";
    private final String removeButtonSelector = "button[id^='remove-']";
    private final String checkoutButtonSelector = "#checkout";

    /**
     * Constructor for the CartPage.
     *
     * @param page Playwright Page object
     */
    public CartPage(Page page) {
        super(page);
    }

    /**
     * Get all items in the cart with their names and prices.
     *
     * @return Map of item names and their prices
     */
    public Map<String, Double> getCartItems() {
        Map<String, Double> cartItems = new HashMap<>();
        List<ElementHandle> items = page.querySelectorAll(cartItemSelector);

        for (ElementHandle item : items) {
            String name = item.querySelector(itemNameSelector).textContent();
            String priceText = item.querySelector(itemPriceSelector).textContent().replace("$", "");
            double price = Double.parseDouble(priceText);
            cartItems.put(name, price);
        }

        return cartItems;
    }

    /**
     * Check if an item is in the cart by name.
     *
     * @param itemName Name of the item to check
     * @return true if the item is in the cart, false otherwise
     */
    public boolean isItemInCart(String itemName) {
        return getCartItems().containsKey(itemName);
    }

    /**
     * Get the price of an item in the cart by name.
     *
     * @param itemName Name of the item
     * @return Price of the item or null if item is not in the cart
     */
    public Double getItemPrice(String itemName) {
        return getCartItems().get(itemName);
    }

    /**
     * Remove an item from the cart by name.
     *
     * @param itemName Name of the item to remove
     */
    public void removeItemByName(String itemName) {
        String removeButtonXPath = String.format("//*[text()='%s']/ancestor::div[contains(@class,'cart_item')]//button[contains(@id,'remove-')]", itemName);
        page.locator(removeButtonXPath).click();
    }

    /**
     * Remove the most expensive item from the cart.
     *
     * @return Name of the removed item or null if cart is empty
     */
    public String removeMostExpensiveItem() {
        Map<String, Double> items = getCartItems();
        if (items.isEmpty()) {
            return null;
        }

        String mostExpensiveItemName = items.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostExpensiveItemName != null) {
            removeItemByName(mostExpensiveItemName);
        }

        return mostExpensiveItemName;
    }

    /**
     * Get the number of items in the cart.
     *
     * @return Number of items in the cart
     */
    public int getCartItemCount() {
        return page.querySelectorAll(cartItemSelector).size();
    }

    /**
     * Proceed to checkout.
     *
     * @return CheckoutPage instance
     */
    public CheckoutPage proceedToCheckout() {
        page.click(checkoutButtonSelector);
        return new CheckoutPage(page);
    }
}
