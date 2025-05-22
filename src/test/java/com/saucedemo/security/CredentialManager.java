package com.saucedemo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages test credentials securely.
 * Loads credentials from a properties file that is not committed to version control.
 */
public class CredentialManager {
    private static final Logger logger = LoggerFactory.getLogger(CredentialManager.class);
    private static final Properties credentials = new Properties();
    private static final String CREDENTIALS_FILE = "credentials.properties";
    private static final String EXAMPLE_CREDENTIALS_FILE = "credentials.example.properties";

    static {
        loadCredentials();
    }

    private static void loadCredentials() {
        try (InputStream input = CredentialManager.class.getClassLoader()
                .getResourceAsStream(CREDENTIALS_FILE)) {
            if (input == null) {
                logger.error("Credentials file not found. Please copy {} to {} and fill in the values.",
                        EXAMPLE_CREDENTIALS_FILE, CREDENTIALS_FILE);
                throw new RuntimeException("Credentials file not found. Please check the setup instructions.");
            }
            credentials.load(input);
            logger.info("Credentials loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading credentials: {}", e.getMessage());
            throw new RuntimeException("Failed to load credentials", e);
        }
    }

    /**
     * Get password for a specific user.
     * @param username The username to get the password for
     * @return The password for the user
     */
    public static String getPassword(String username) {
        String key = username + ".password";
        String password = credentials.getProperty(key);
        if (password == null) {
            logger.error("No password found for user: {}", username);
            throw new RuntimeException("No password found for user: " + username);
        }
        return password;
    }

    /**
     * Check if a user exists in the credentials file.
     * @param username The username to check
     * @return true if the user exists, false otherwise
     */
    public static boolean userExists(String username) {
        return credentials.containsKey(username + ".password");
    }

    /**
     * Get all available usernames.
     * @return Array of usernames
     */
    public static String[] getAvailableUsers() {
        return credentials.stringPropertyNames().stream()
                .map(key -> key.replace(".password", ""))
                .toArray(String[]::new);
    }
} 