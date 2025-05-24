package com.saucedemo.utils;

import com.microsoft.playwright.ElementHandle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for validating inventory items and their prices.
 */
public class InventoryValidationUtils {
    
    /**
     * Finds items that share the lowest price.
     *
     * @param itemsWithPrices Map of item names and their prices
     * @return List of item names that share the lowest price
     */
    public static List<String> findItemsWithLowestPrice(Map<String, Double> itemsWithPrices) {
        if (itemsWithPrices.isEmpty()) {
            return Collections.emptyList();
        }

        double lowestPrice = itemsWithPrices.values().stream()
                .min(Double::compareTo)
                .orElse(0.0);

        return itemsWithPrices.entrySet().stream()
                .filter(entry -> Math.abs(entry.getValue() - lowestPrice) < 0.001)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Finds items that share the highest price.
     *
     * @param itemsWithPrices Map of item names and their prices
     * @return List of item names that share the highest price
     */
    public static List<String> findItemsWithHighestPrice(Map<String, Double> itemsWithPrices) {
        if (itemsWithPrices.isEmpty()) {
            return Collections.emptyList();
        }

        double highestPrice = itemsWithPrices.values().stream()
                .max(Double::compareTo)
                .orElse(0.0);

        return itemsWithPrices.entrySet().stream()
                .filter(entry -> Math.abs(entry.getValue() - highestPrice) < 0.001)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Validates all price scenarios in the inventory.
     *
     * @param items List of inventory item elements
     * @param priceSelector CSS selector for price element
     * @return Map containing validation results for different scenarios
     */
    public static Map<String, Object> validateInventoryPrices(List<ElementHandle> items, String priceSelector) {
        Map<String, Object> validationResults = new HashMap<>();
        Map<String, Double> itemsWithPrices = new HashMap<>();
        List<String> itemsWithMissingPrices = new ArrayList<>();
        
        // Single pass through items to collect valid prices and identify invalid ones
        items.forEach(item -> {
            String name = item.querySelector(".inventory_item_name").textContent();
            ElementHandle priceElement = item.querySelector(priceSelector);
            
            // Check if price is missing or empty
            if (priceElement == null || priceElement.textContent().trim().isEmpty()) {
                itemsWithMissingPrices.add(name);
                return;
            }
            
            // Try to parse price
            try {
                String priceText = priceElement.textContent().replace("$", "");
                double price = Double.parseDouble(priceText);
                itemsWithPrices.put(name, price);
            } catch (NumberFormatException e) {
                itemsWithMissingPrices.add(name);
            }
        });

        // Store validation results for missing prices
        validationResults.put("hasMissingPrices", !itemsWithMissingPrices.isEmpty());
        validationResults.put("itemsWithMissingPrices", itemsWithMissingPrices);
        
        // Only perform price comparisons if we have valid prices
        if (!itemsWithPrices.isEmpty()) {
            // Check for items with lowest price
            List<String> itemsWithLowestPrice = findItemsWithLowestPrice(itemsWithPrices);
            validationResults.put("hasMultipleLowestPrice", itemsWithLowestPrice.size() > 1);
            validationResults.put("itemsWithLowestPrice", itemsWithLowestPrice);

            // Check for items with highest price
            List<String> itemsWithHighestPrice = findItemsWithHighestPrice(itemsWithPrices);
            validationResults.put("hasMultipleHighestPrice", itemsWithHighestPrice.size() > 1);
            validationResults.put("itemsWithHighestPrice", itemsWithHighestPrice);
        } else {
            // If no valid prices found, set default values
            validationResults.put("hasMultipleLowestPrice", false);
            validationResults.put("itemsWithLowestPrice", Collections.emptyList());
            validationResults.put("hasMultipleHighestPrice", false);
            validationResults.put("itemsWithHighestPrice", Collections.emptyList());
        }

        return validationResults;
    }
}
