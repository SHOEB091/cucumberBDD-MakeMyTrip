# MakeMyTrip BDD Automation Framework

## Overview
A complete end-to-end Selenium automation project built with Cucumber BDD, TestNG, parallel execution, Extent Reports, and screenshot capture — targeting MakeMyTrip.com.

## Project Location
All source code lives in: `MakeMyTrip_BDD_Project/`

## Technology Stack
- **Language**: Java 21
- **Build Tool**: Apache Maven 3.9.x
- **Test Framework**: TestNG 7.10.x
- **BDD Framework**: Cucumber 7.18 (cucumber-testng)
- **Browser Automation**: Selenium 4.20 + WebDriverManager 5.8
- **Reports**: Extent Reports 5 + extentreports-cucumber7-adapter
- **Utilities**: Apache Commons IO

## Project Structure
```
MakeMyTrip_BDD_Project/
├── pom.xml
└── src/test/
    ├── java/
    │   ├── runners/
    │   │   ├── CabBookingRunner.java       # TestNG runner for Cab feature (parallel)
    │   │   ├── GiftCardRunner.java         # TestNG runner for Gift Card feature (parallel)
    │   │   └── HotelBookingRunner.java     # TestNG runner for Hotel feature (parallel)
    │   ├── cucumberOptions/
    │   │   └── TestRunnerTest.java         # Combined runner (all features, sequential)
    │   ├── hooks/
    │   │   └── Hooks.java                  # @Before/@After with ThreadLocal WebDriver
    │   ├── pageObjects/
    │   │   ├── BaseDriver.java             # Base class with driver/wait fields
    │   │   ├── HomePage.java               # Home page (popup handling, navigation)
    │   │   ├── CabPage.java                # Cab search, date picker, SUV filter, prices
    │   │   ├── GiftCardPage.java           # Gift card selection, form, error capture
    │   │   └── HotelPage.java              # Hotel guest selector, adult number extraction
    │   ├── stepDefinitions/
    │   │   ├── CabBookingSteps.java        # Steps for CabBooking.feature
    │   │   ├── GiftCardSteps.java          # Steps for GiftCard.feature
    │   │   ├── HotelBookingSteps.java      # Steps for HotelBooking.feature
    │   │   └── TravelSteps.java            # Legacy combined steps for TravelBooking.feature
    │   └── utils/
    │       ├── ConfigReader.java           # Reads config.properties
    │       ├── ScreenshotUtil.java         # Thread-safe screenshot capture
    │       └── ExtentReportManager.java    # Singleton Extent Reports manager
    └── resources/
        ├── features/
        │   ├── CabBooking.feature          # Scenario: Delhi→Manali SUV lowest price
        │   ├── GiftCard.feature            # Scenario: Invalid email error capture
        │   ├── HotelBooking.feature        # Scenario: Extract adult numbers into List
        │   └── TravelBooking.feature       # Legacy: all scenarios combined
        ├── testng.xml                      # Parallel suite: 3 tests × 3 threads
        ├── extent.properties               # Extent Spark reporter config
        └── config.properties              # Browser, waits, screenshot flags
```

## Key Design Decisions

### Parallel Testing
- `testng.xml` uses `parallel="tests" thread-count="3"` so each feature file runs in a separate thread simultaneously
- Each runner class (`CabBookingRunner`, `GiftCardRunner`, `HotelBookingRunner`) also enables `@DataProvider(parallel=true)` for scenario-level parallelism within each feature
- `Hooks.java` uses `ThreadLocal<WebDriver>` so every thread gets its own isolated browser instance — no shared state

### Extent Reports
- Auto-integrated via `extentreports-cucumber7-adapter` — no manual wiring needed
- Config: `src/test/resources/extent.properties`
- Output: `target/ExtentReport/SparkReport.html`

### Screenshots
- Captured on PASS and FAIL in `Hooks.tearDown()` and attached directly to the Cucumber scenario (appears in Extent report automatically)
- Also captured at specific test steps via `ScreenshotUtil.capture()` → saved to `target/Screenshots/`
- Screenshot filenames include thread ID and timestamp for uniqueness across parallel runs

### Config-Driven
- `config.properties` controls browser type, waits, headless mode, and screenshot flags
- Set `headless=true` for CI/server environments

## How to Run

### Compile only (verify no build errors)
```bash
cd MakeMyTrip_BDD_Project
mvn test-compile -q
```

### Run all tests in parallel (recommended)
```bash
cd MakeMyTrip_BDD_Project
mvn test
```
Uses `testng.xml` → runs 3 features in parallel → generates Extent HTML report

### Run a specific feature tag
```bash
cd MakeMyTrip_BDD_Project
mvn test -Dcucumber.filter.tags="@CabBooking"
```

### Run with headless Chrome (for CI)
Edit `src/test/resources/config.properties`:
```
headless=true
```
Then run `mvn test`

## Test Outputs
After `mvn test`:
- `target/ExtentReport/SparkReport.html` — Full Extent HTML report with screenshots
- `target/cucumber-reports/CabBooking_Report.html` — Cucumber HTML per feature
- `target/cucumber-reports/GiftCard_Report.html`
- `target/cucumber-reports/HotelBooking_Report.html`
- `target/Screenshots/` — Individual PNG screenshots (timestamped, thread-identified)
- `target/surefire-reports/` — TestNG XML/TXT surefire reports

## Automation Scope Covered
1. **Handling alerts** — Popup/modal/coachmark dismissal in `HomePage.handlePopups()`
2. **Filling simple form** — Sender details in `GiftCardPage`
3. **Capture warning message** — Invalid email error capture in `GiftCardPage.captureErrorMessage()`
4. **Scrolling down in web page** — `js.executeScript("window.scrollBy(...)`)` / `scrollIntoView`
5. **Extract dropdown items & store in collections** — `HotelPage.extractAllAdultNumbers()` returns `List<Integer>`
6. **Navigation from Menus** — `HomePage.goToMenu()` / `goToCabs()` / `goToHotels()`
7. **Navigating back to home page** — `driver.get(baseUrl)` in TravelSteps
