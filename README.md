# SauceDemo Playwright Automation Project

This project contains automated tests for the SauceDemo website using Playwright framework with Java and JUnit 5.

## Overview

The automation suite covers the following test scenarios:

1. **Login Functionality**
    - Valid login with correct credentials
    - Invalid login with incorrect credentials

2. **Shopping Cart**
    - Add the most expensive item to the cart
    - Add the cheapest item to the cart
    - Verify items in the cart with correct names and prices

3. **Checkout Process**
    - Remove the most expensive item from the cart
    - Complete the checkout form with generated test data
    - Verify successful checkout with confirmation message

## Project Structure

```
├── src
│   └── test
│       └── java
│           └── com
│               └── saucedemo
│                   ├── pages      # Page Object Models
│                   │   ├── BasePage.java
│                   │   ├── CartPage.java
│                   │   ├── CheckoutCompletePage.java
│                   │   ├── CheckoutPage.java
│                   │   ├── InventoryPage.java
│                   │   └── LoginPage.java
│                   ├── utils      # Utility classes
│                   │   └── TestUtils.java
│                   └── tests      # Test classes
│                       ├── BaseTest.java
│                       ├── CartTest.java
│                       ├── CheckoutTest.java
│                       └── LoginTest.java
├── pom.xml
└── README.md
```

## Prerequisites

- Java JDK 11 or higher
- Maven 3.6 or higher

## Setup Instructions

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/saucedemo-playwright.git
   cd saucedemo-playwright
   ```

2. **Install dependencies and Playwright browsers**

   ```bash
   mvn clean install
   ```

   This will download all required dependencies including the Playwright browser binaries.

## Running Tests

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=LoginTest
```

```bash
mvn test -Dtest=CartTest
```

```bash
mvn test -Dtest=CheckoutTest
```

## Implementation Details

### Page Object Model (POM)

The project follows the Page Object Model design pattern to separate test logic from page interactions, improving maintainability and readability.

- **BasePage**: Contains common methods used across all pages
- **LoginPage**: Handles login form interactions
- **InventoryPage**: Manages product listing and adding items to cart
- **CartPage**: Handles cart operations like viewing and removing items
- **CheckoutPage**: Manages checkout form and confirmation

### Dynamic Element Handling

- The tests dynamically identify the most expensive and cheapest items in the inventory
- Locators are designed to work with dynamically changing content

### Test Data Generation

- The `TestUtils` class generates random data for checkout information
- This ensures tests are robust and don't rely on hard-coded values

### Assertions

- Each test includes appropriate assertions to verify expected behavior
- Assertions validate both UI state and functional behavior

## Reporting

The project uses JUnit's built-in reporting capabilities. After test execution, you can find the test reports in:

```
target/surefire-reports/
```

## Best Practices Implemented

1. **Selector Strategy**: Using reliable CSS and XPath selectors
2. **Wait Mechanisms**: Proper handling of page loads and element visibility
3. **Clean Code**: Well-organized code with meaningful names and comments
4. **Error Handling**: Appropriate error handling and meaningful assertions
5. **Maintainability**: Separation of concerns using POM pattern

## Troubleshooting

If you encounter issues running the tests:

1. Ensure Java and Maven are properly installed
2. Try reinstalling Playwright browsers: `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"`
3. Check for network connectivity to the SauceDemo website

## CI/CD Integration

This project uses GitHub Actions for continuous integration and deployment. The CI/CD pipeline:

1. **Builds and Tests**
   - Runs on every push to main and pull requests
   - Uses JDK 24
   - Installs required Playwright browsers
   - Executes all tests in parallel
   - Generates test reports

2. **Test Reporting**
   - Generates detailed test reports using Maven Surefire
   - Publishes test results as GitHub Actions artifacts
   - Creates a test summary in pull requests
   - Stores test reports for 30 days

3. **Failure Handling**
   - Creates GitHub issues for test failures
   - Provides detailed failure information
   - Links to the specific workflow run

### Running Tests in CI

The CI pipeline uses the following commands:
```bash
# Install Playwright browsers
mvn playwright install

# Run tests with reporting
mvn clean verify

# Generate test report
mvn surefire-report:report
```

### Viewing Test Results

Test results can be found in:
1. GitHub Actions artifacts (target/surefire-reports/)
2. Pull request checks
3. GitHub Actions workflow summary

### Local Development

To run tests locally with the same configuration as CI:
```bash
mvn clean verify
```

To generate test reports locally:
```bash
mvn surefire-report:report
```