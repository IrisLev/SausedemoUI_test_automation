package com.saucedemo.utils;

import java.util.Random;

/**
 * Utility class for common test operations.
 */
public class TestUtils {
    private static final Random random = new Random();

    /**
     * Generate a random string of specified length.
     *
     * @param length Length of the string to generate
     * @return Random string
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generate a random postal code.
     *
     * @return Random postal code
     */
    public static String generateRandomPostalCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Generate random user data for checkout form.
     *
     * @return Array containing [firstName, lastName, postalCode]
     */
    public static String[] generateRandomUserData() {
        String firstName = generateRandomString(8);
        String lastName = generateRandomString(10);
        String postalCode = generateRandomPostalCode();
        return new String[]{firstName, lastName, postalCode};
    }
}
