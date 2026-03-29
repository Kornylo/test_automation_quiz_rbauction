# RB Auction Search Tests

Selenium WebDriver automation for [rbauction.com](https://www.rbauction.com) search functionality.

**Stack:** Java 17, Selenium 4, TestNG, ExtentReports, Maven, WebDriverManager, SLF4J, Logback

## Prerequisites

- Java JDK 17+ → `java -version`
- Maven 3.6+ → `mvn -version`
- Google Chrome (latest stable)

ChromeDriver downloads automatically via WebDriverManager.

## Run

```bash
mvn clean test
```

Headless mode (no browser window):

```bash
mvn clean test -Dheadless=true
```

Custom base URL (e.g. dev/staging environment):

```bash
mvn clean test -DbaseUrl=https://staging.rbauction.com
```

Tests run in parallel by default (3 threads). To run sequentially:

```bash
mvn clean test -Dparallel=none
```

## Configuration

Framework settings are controlled via `src/test/resources/config.properties`:

```properties
base.url=https://www.rbauction.com
browser=chrome
headless=false
timeout.page.load=60
timeout.explicit.wait=30
timeout.dialog.wait=5
```

Command-line system properties take priority over file values (e.g. `-DbaseUrl=...`, `-Dheadless=true`).

## Results

Pre-generated test reports are in `test-reports/`:
- `ExtentReport.html` — ExtentReports HTML report with step-by-step logging
- `testng-results.xml` — XML for CI

After running `mvn clean test`, fresh reports generate in `test-reports/`.

Screenshots are captured automatically on test failure and embedded in the ExtentReport.

## Test Scenarios

| # | Test | Verifies |
|---|------|----------|
| 1 | Search "Ford F-150" | Result count > 0, first result contains "Ford F-150" |
| 2 | Search "Chevrolet Colodrado" (negative) | Result count > 0, first result does NOT contain misspelled "Chevrolet Colodrado" — **test fails as expected** |
| 3 | Search "F-150" + year filter | Result count changes after applying 2010–current year filter |

## Architecture

- **Page Object Model** — `BasePage` → `HomePage`, `SearchResultsPage`
- **DriverFactory** — Centralized browser creation (Chrome/Firefox, headless support)
- **ConfigReader** — Singleton config with CLI override (`-DbaseUrl`, `-Dheadless`, `-Dbrowser`)
- **TestListener** — ExtentReports integration with step-by-step logging and screenshot-on-failure
- **RetryAnalyzer** — Automatic retry (up to 2x) for flaky tests (available but not actively applied — enable per-test via `retryAnalyzer = RetryAnalyzer.class`)
- **DataProvider** — External JSON test data (`search-terms.json`, `year-filter.json`)

## Project Structure

```
├── .github/workflows/
│   └── test.yml
├── .gitignore
├── pom.xml
├── README.md
├── test-reports/
│   ├── ExtentReport.html
│   └── testng-results.xml
├── src/main/java/com/rbauction/
│   ├── base/
│   │   └── BaseTest.java
│   ├── config/
│   │   └── ConfigReader.java
│   ├── listeners/
│   │   ├── RetryAnalyzer.java
│   │   └── TestListener.java
│   ├── pages/
│   │   ├── BasePage.java
│   │   ├── HomePage.java
│   │   └── SearchResultsPage.java
│   └── utils/
│       ├── DriverFactory.java
│       └── ScreenshotUtils.java
└── src/test/
    ├── java/com/rbauction/
    │   ├── data/
    │   │   └── TestData.java
    │   └── tests/smoke/
    │       └── SearchTests.java
    └── resources/
        ├── config.properties
        ├── logback-test.xml
        ├── testng.xml
        └── testdata/
            ├── search-terms.json
            └── year-filter.json
```
