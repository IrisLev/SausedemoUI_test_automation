package com.saucedemo.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.saucedemo.utils.InventoryValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Page object representing the inventory page of SauceDemo website.
 */
public class InventoryPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(InventoryPage.class);

    // Selectors
    private final String inventoryItemSelector = ".inventory_item";
    private final String itemNameSelector = ".inventory_item_name";
    private final String itemPriceSelector = ".inventory_item_price";
    private final String cartBadgeSelector = ".shopping_cart_badge";
    private final String cartLinkSelector = ".shopping_cart_link";

    public InventoryPage(Page page) {
        super(page);
    }

    /**
     * Get all inventory items as a list of ElementHandle objects.
     *
     * @return List of ElementHandle objects representing inventory items
     */
    private List<ElementHandle> getAllInventoryItems() {
        return page.querySelectorAll(inventoryItemSelector);
    }

    /**
     * Get item name from an inventory item element.
     *
     * @param item ElementHandle representing an inventory item
     * @return The name of the item
     */
    private String getItemName(ElementHandle item) {
        return item.querySelector(itemNameSelector).textContent();
    }

    /**
     * Get item price from an inventory item element.
     *
     * @param item ElementHandle representing an inventory item
     * @return The price of the item as a double
     */
    private double getItemPrice(ElementHandle item) {
        String priceText = item.querySelector(itemPriceSelector).textContent().replace("$", "");
        return Double.parseDouble(priceText);
    }

    /**
     * Get all inventory items with their names and prices.
     *
     * @return Map of item names and their prices
     */
    public Map<String, Double> getAllItemsWithPrices() {
        Map<String, Double> itemPrices = new HashMap<>();
        List<ElementHandle> items = getAllInventoryItems();

        for (ElementHandle item : items) {
            String name = getItemName(item);
            double price = getItemPrice(item);
            itemPrices.put(name, price);
        }

        return itemPrices;
    }

    /**
     * Validates the inventory prices and logs any issues found.
     *
     * @throws IllegalStateException if critical price validation issues are found
     */
    public void validateInventoryPrices() {
        List<ElementHandle> items = getAllInventoryItems();
        Map<String, Object> validationResults = InventoryValidationUtils.validateInventoryPrices(items, itemPriceSelector);

        // Log validation results
        if ((Boolean) validationResults.get("hasMissingPrices")) {
            List<String> itemsWithMissingPrices = (List<String>) validationResults.get("itemsWithMissingPrices");
            logger.error("Found items with missing prices: {}", itemsWithMissingPrices);
            throw new IllegalStateException("Items with missing prices found: " + itemsWithMissingPrices);
        }

        if ((Boolean) validationResults.get("hasMultipleLowestPrice")) {
            List<String> itemsWithLowestPrice = (List<String>) validationResults.get("itemsWithLowestPrice");
            logger.warn("Multiple items found with lowest price: {}", itemsWithLowestPrice);
        }

        if ((Boolean) validationResults.get("hasMultipleHighestPrice")) {
            List<String> itemsWithHighestPrice = (List<String>) validationResults.get("itemsWithHighestPrice");
            logger.warn("Multiple items found with highest price: {}", itemsWithHighestPrice);
        }
    }

    /**
     * Get the most expensive item with validation.
     *
     * @return Map.Entry containing the name and price of the most expensive item
     * @throws IllegalStateException if no valid items are found or if there are price validation issues
     */
    public Map.Entry<String, Double> getMostExpensiveItem() {
        validateInventoryPrices();
        Map<String, Double> itemPrices = getAllItemsWithPrices();
        if (itemPrices.isEmpty()) {
            throw new IllegalStateException("No valid items found in inventory");
        }
        return itemPrices.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalStateException("Failed to find most expensive item"));
    }

    /**
     * Get the cheapest item with validation.
     *
     * @return Map.Entry containing the name and price of the cheapest item
     * @throws IllegalStateException if no valid items are found or if there are price validation issues
     */
    public Map.Entry<String, Double> getCheapestItem() {
        validateInventoryPrices();
        Map<String, Double> itemPrices = getAllItemsWithPrices();
        if (itemPrices.isEmpty()) {
            throw new IllegalStateException("No valid items found in inventory");
        }
        return itemPrices.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalStateException("Failed to find cheapest item"));
    }

    /**
     * Add an item to the cart by item name.
     *
     * @param itemName Name of the item to add
     */
    public void addItemToCartByName(String itemName) {
        String addButtonSelector = String.format("//*[text()='%s']/ancestor::div[contains(@class,'inventory_item')]//button[contains(@id,'add-to-cart')]", itemName);
        page.locator(addButtonSelector).click();
    }

    /**
     * Get the number of items in the cart.
     *
     * @return Number of items in the cart or 0 if cart is empty
     */
    public int getCartItemCount() {
        if (elementExists(cartBadgeSelector)) {
            return Integer.parseInt(page.textContent(cartBadgeSelector));
        }
        return 0;
    }

    /**
     * Navigate to the cart page.
     *
     * @return CartPage instance
     */
    public CartPage navigateToCart() {
        page.click(cartLinkSelector);
        return new CartPage(page);
    }

    /**
     * Add the most expensive item to the cart.
     *
     * @return Name of the added item
     */
    public String addMostExpensiveItemToCart() {
        Map.Entry<String, Double> mostExpensiveItem = getMostExpensiveItem();
        if (mostExpensiveItem != null) {
            addItemToCartByName(mostExpensiveItem.getKey());
            return mostExpensiveItem.getKey();
        }
        return null;
    }

    /**
     * Add the cheapest item to the cart.
     *
     * @return Name of the added item
     */
    public String addCheapestItemToCart() {
        Map.Entry<String, Double> cheapestItem = getCheapestItem();
        if (cheapestItem != null) {
            addItemToCartByName(cheapestItem.getKey());
            return cheapestItem.getKey();
        }
        return null;
    }
}
