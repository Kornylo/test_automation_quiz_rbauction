<h1 align="center">Test Automation Framework</h1>

<p align="center">
  End-to-end search testing for <a href="https://www.rbauction.com">rbauction.com</a> built with Java, Selenium WebDriver, TestNG, and ExtentReports.
</p>

## Quick Start

```bash
# Clone & run (Chrome + ChromeDriver managed automatically)
mvn clean test
```

Two tests pass, one intentionally fails. See [Test Scenarios](#test-scenarios) for details.

---

## Features

| Feature | Details |
|---------|---------|
| **Page Object Model** | `BasePage`, `HomePage`, `SearchResultsPage`. Clean page abstraction |
| **DriverFactory** | Chrome, headless mode, WebDriverManager auto-setup |
| **Environment Aliases** | `-Denv=PROD` resolves URL from config (extensible) |
| **Parallel Execution** | 3 threads by default (`testng.xml`), configurable |
| **ExtentReports** | Step-by-step HTML report with screenshot-on-failure |
| **External Test Data** | Single JSON file + `@DataProvider` with type filtering and TestPlan/TestCase IDs |
| **CI Ready** | GitHub Actions workflow included (`.github/workflows/test.yml`) |

---

## Test Scenarios

| # | Report Name | Expects |
|---|-------------|---------|
| 1 | `TP-001 \| TC-001 \| Search Ford F-150 and verify first result` | Results > 0, first result contains "Ford F-150". **PASS** |
| 2 | `TP-001 \| TC-002 \| Search Chevrolet Colodrado and verify first result` | Results > 0, first result contains misspelling. **FAIL (expected)** |
| 3 | `TP-001 \| TC-003 \| Search F-150, apply year filter, and verify results count changes` | Result count changes after year filter. **PASS** |

> **Test 2** is a negative test. The site auto-corrects the misspelling, so `assertTrue` fails by design. This validates that the framework correctly reports real assertion failures.

---

## Usage

### Environment Selection

```bash
mvn clean test -Denv=PROD     # https://www.rbauction.com (default)
```

Direct URL override is also supported:

```bash
mvn clean test -DbaseUrl=https://www.rbauction.com
```

**Resolution order:** `-Denv` > `-DbaseUrl` > `base.url` from config file.

### Headless Mode

```bash
mvn clean test -Dheadless=true
```

### Parallel Execution

Tests run in parallel by default (3 threads). To run sequentially:

```bash
mvn clean test -Dparallel=none
```

### Combine Flags

```bash
mvn clean test -Denv=PROD -Dheadless=true
```

---

## Configuration

All settings live in `src/test/resources/config.properties`:

```properties
base.url=https://www.rbauction.com
headless=false
timeout.page.load=60
timeout.explicit.wait=30
timeout.dialog.wait=5

# Environment alias
env.prod=https://www.rbauction.com
```

Every property can be overridden from the command line: `-Dheadless=true`, `-Dtimeout.page.load=120`, etc.

---

## Test Reports

After running tests, reports are generated in `test-reports/`:

| File | Format | Description |
|------|--------|-------------|
| `ExtentReport.html` | HTML | Step-by-step report with TestPlan/TestCase IDs and failure screenshots |
| `testng-results.xml` | XML | Machine-readable results for CI pipelines |

Pre-generated reports are included in the repository for immediate review.

---

## Prerequisites

| Requirement | Check |
|-------------|-------|
| Java JDK 17+ | `java -version` |
| Maven 3.6+ | `mvn -version` |
| Google Chrome | Latest stable |

> ChromeDriver is downloaded automatically at runtime by [WebDriverManager](https://github.com/bonigarcia/webdrivermanager). No manual driver setup needed.

---

## Project Structure

```
├── .github/workflows/
│   └── test.yml                          # CI pipeline
├── pom.xml                               # Dependencies & build config
├── README.md
├── test-reports/
│   ├── ExtentReport.html                 # HTML report
│   └── testng-results.xml                # XML report
│
├── src/main/java/com/rbauction/
│   ├── base/
│   │   └── BaseTest.java                 # WebDriver lifecycle, openHomePage()
│   ├── config/
│   │   └── ConfigReader.java             # Singleton config + env alias resolution
│   ├── listeners/
│   │   └── TestListener.java             # ExtentReports + step logging + screenshots
│   ├── pages/
│   │   ├── BasePage.java                 # Shared driver, waits, dialog handling
│   │   ├── HomePage.java                 # open() + searchFor()
│   │   └── SearchResultsPage.java        # Result count, first result, year filter
│   └── utils/
│       ├── DriverFactory.java            # Chrome driver creation + headless
│       └── ScreenshotUtils.java          # Base64 screenshot capture
│
└── src/test/
    ├── java/com/rbauction/
    │   ├── data/
    │   │   └── TestData.java             # @DataProvider, reads test-data.json, filters by type
    │   └── tests/smoke/
    │       └── SearchTests.java          # 2 test methods, 3 executions
    └── resources/
        ├── config.properties             # Framework settings + env aliases
        ├── logback-test.xml              # SLF4J logging config
        ├── testng.xml                    # Suite config (parallel, listeners)
        └── testdata/
            └── test-data.json        # All test data (search + year filter)
```

---

## Tech Stack

| Library | Version | Purpose |
|---------|---------|---------|
| [Selenium WebDriver](https://www.selenium.dev/) | 4.27.0 | Browser automation |
| [TestNG](https://testng.org/) | 7.10.2 | Test framework + parallel execution |
| [ExtentReports](https://www.extentreports.com/) | 5.1.2 | HTML reporting |
| [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) | 5.9.2 | Automatic driver management |
| [Gson](https://github.com/google/gson) | 2.11.0 | JSON test data parsing |
| [SLF4J + Logback](https://www.slf4j.org/) | 2.0.16 | Logging |
