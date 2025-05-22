package com.saucedemo.utils;

import com.microsoft.playwright.ElementHandle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for validating inventory items and their prices.
 */
public class InventoryValidationUtils {
    
    /**
     * Validates if there are any items with missing prices.
     *
     * @param items List of inventory item elements
     * @param priceSelector CSS selector for price element
     * @return List of item names that have missing prices
     */
    public static List<String> findItemsWithMissingPrices(List<ElementHandle> items, String priceSelector) {
        return items.stream()
                .filter(item -> {
                    ElementHandle priceElement = item.querySelector(priceSelector);
                    return priceElement == null || priceElement.textContent().trim().isEmpty();
                })
                .map(item -> item.querySelector(".inventory_item_name").textContent())
                .collect(Collectors.toList());
    }

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
        
        // Check for missing prices
        List<String> itemsWithMissingPrices = findItemsWithMissingPrices(items, priceSelector);
        validationResults.put("hasMissingPrices", !itemsWithMissingPrices.isEmpty());
        validationResults.put("itemsWithMissingPrices", itemsWithMissingPrices);

        // Get all items with valid prices
        Map<String, Double> itemsWithPrices = new HashMap<>();
        items.stream()
                .filter(item -> item.querySelector(priceSelector) != null)
                .forEach(item -> {
                    String name = item.querySelector(".inventory_item_name").textContent();
                    String priceText = item.querySelector(priceSelector).textContent().replace("$", "");
                    try {
                        double price = Double.parseDouble(priceText);
                        itemsWithPrices.put(name, price);
                    } catch (NumberFormatException e) {
                        itemsWithMissingPrices.add(name);
                    }
                });

        // Check for items with lowest price
        List<String> itemsWithLowestPrice = findItemsWithLowestPrice(itemsWithPrices);
        validationResults.put("hasMultipleLowestPrice", itemsWithLowestPrice.size() > 1);
        validationResults.put("itemsWithLowestPrice", itemsWithLowestPrice);

        // Check for items with highest price
        List<String> itemsWithHighestPrice = findItemsWithHighestPrice(itemsWithPrices);
        validationResults.put("hasMultipleHighestPrice", itemsWithHighestPrice.size() > 1);
        validationResults.put("itemsWithHighestPrice", itemsWithHighestPrice);

        return validationResults;
    }
} 