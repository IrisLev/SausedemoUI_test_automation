package com.saucedemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Manages test configuration properties.
 * Loads properties from config.properties file and provides access to them.
 */
public class TestConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
            logger.info("Configuration loaded successfully from {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Error loading configuration: {}", e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    // Base URL
    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    // Credentials
    public static String getValidUsername() {
        return getProperty("valid.username");
    }

    public static String getValidPassword() {
        return getProperty("valid.password");
    }

    public static String getInvalidUsername() {
        return getProperty("invalid.username");
    }

    public static String getInvalidPassword() {
        return getProperty("invalid.password");
    }

    // Browser Settings
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("browser.headless"));
    }

    public static int getSlowMo() {
        return Integer.parseInt(getProperty("browser.slow.mo"));
    }

    public static int getTimeout() {
        return Integer.parseInt(getProperty("browser.timeout"));
    }

    // Test Data
    public static int getRetryCount() {
        return Integer.parseInt(getProperty("retry.count"));
    }

    public static int getRetryDelay() {
        return Integer.parseInt(getProperty("retry.delay"));
    }

    // Network Settings
    public static List<String> getIgnoredErrorPatterns() {
        String patterns = getProperty("ignored.error.patterns");
        return Arrays.asList(patterns.split(","));
    }

    // Generic property getter with logging
    private static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Configuration property not found: {}", key);
            throw new RuntimeException("Configuration property not found: " + key);
        }
        return value;
    }
} 