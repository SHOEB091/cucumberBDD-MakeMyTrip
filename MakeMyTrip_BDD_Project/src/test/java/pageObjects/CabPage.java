package pageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * CabPage – Page Object for the MakeMyTrip Cabs widget.
 *
 * XPaths are based on the DOM structure visible in the user-provided screenshots:
 *   Screenshot 1 → FROM field  (data-cy="OutstationOneWayWidget_57")
 *   Screenshot 2 → TO field    (data-cy="OutstationOneWayWidget_59")
 *   Screenshot 3 → Time picker (hour rows, minute rows, APPLY button)
 */
public class CabPage extends BaseDriver {

    private final JavascriptExecutor js;
    private final WebDriverWait      longWait;   // 25 s – results page

    public CabPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js       = (JavascriptExecutor) driver;
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(25));
        PageFactory.initElements(driver, this);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Locators
    // ═══════════════════════════════════════════════════════════════════════

    @FindBy(className = "commonModal__close")
    private WebElement closePopup;

    @FindBy(xpath = "//img[@alt='minimize']")
    private WebElement minimizeFloat;

    // Cabs tab in the top navigation
    @FindBy(xpath = "//span[contains(@class,'chCabs')]")
    private WebElement cabsTab;

    // FROM readonly input (screenshot 1: data-cy="fromCity")
    @FindBy(xpath = "//input[@data-cy='fromCity'] | //input[@id='fromCity']")
    private WebElement fromCityInput;

    // TO container (screenshot 2: data-cy="OutstationOneWayWidget_59")
    @FindBy(xpath = "//div[@data-cy='OutstationOneWayWidget_59'] | //div[contains(@class,'searchToCity')]")
    private WebElement toCityContainer;

    // Departure date field
    @FindBy(id = "departure")
    private WebElement departureDateField;

    // Month caption inside the DayPicker calendar
    @FindBy(xpath = "//div[contains(@class,'DayPicker-Caption')]")
    private WebElement monthCaption;

    // Next-month arrow in the calendar
    @FindBy(xpath = "//span[contains(@class,'DayPicker-NavButton--next')]")
    private WebElement nextMonthBtn;

    // Pickup-Time label (opens the time-picker panel)
    @FindBy(xpath = "//label[@for='pickupTime'] | //div[contains(@class,'pickupTime')]")
    private WebElement pickupTimeLabel;

    // SEARCH button (data-cy from the original project)
    @FindBy(xpath = "//p[@data-cy='onewaySearch']/a")
    private WebElement searchBtn;

    // SUV filter – MakeMyTrip cabs listing uses a label/checkbox per cab type
    // Try: a label or div whose text contains "SUV"
    @FindBy(xpath =
        "//label[contains(normalize-space(.),'SUV')]"
        + " | //span[normalize-space(text())='SUV']"
        + " | //div[contains(@class,'carTypeFilter') and contains(.,'SUV')]"
        + " | //div[@role='checkbox' and contains(.,'SUV')]"
        + " | //li[contains(.,'SUV')]//input[@type='checkbox']/..")
    private WebElement suvFilterItem;

    // Price elements – any element visibly showing ₹ on the listing page
    @FindBy(xpath =
        "//*[contains(@class,'price') and contains(text(),'₹')]"
        + " | //span[starts-with(normalize-space(text()),'₹')]"
        + " | //*[contains(@class,'fare') and contains(text(),'₹')]"
        + " | //*[contains(@class,'Price') and not(contains(@class,'perKm'))]")
    private List<WebElement> priceElements;

    // ═══════════════════════════════════════════════════════════════════════
    //  Popup handling
    // ═══════════════════════════════════════════════════════════════════════

    public void handlePopups() {
        WebDriverWait s = new WebDriverWait(driver, Duration.ofSeconds(4));
        try { s.until(ExpectedConditions.elementToBeClickable(closePopup)).click();   } catch (Exception ignored) {}
        try { s.until(ExpectedConditions.elementToBeClickable(minimizeFloat)).click(); } catch (Exception ignored) {}
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Cabs tab
    // ═══════════════════════════════════════════════════════════════════════

    public void clickCabsTab() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cabsTab)).click();
            System.out.println("[CabPage] Cabs tab clicked.");
        } catch (Exception e) {
            System.out.println("[CabPage] Cabs tab fallback: " + e.getMessage());
        }
        handlePopups();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  City entry
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Enters FROM (Delhi) and TO (Manali) cities in the cab search widget.
     * After clicking the readonly FROM input a live-search popup appears;
     * we type into that popup's input, wait for suggestions, then click the right one.
     */
    public void enterCities(String from, String to) throws InterruptedException {

        // ── FROM ──────────────────────────────────────────────────────────────
        System.out.println("[CabPage] Clicking FROM field...");
        wait.until(ExpectedConditions.elementToBeClickable(fromCityInput)).click();
        Thread.sleep(800);

        // The popup autocomplete input that appears after clicking the readonly field
        WebElement fromSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
            "//div[contains(@class,'hsw_autocomplePopup')]//input"
            + " | //input[@autocomplete='off' and @title='From']"
            + " | //input[contains(@placeholder,'From') or contains(@placeholder,'Search')]"
        )));
        fromSearch.clear();
        fromSearch.sendKeys(from);
        System.out.println("[CabPage] Typed FROM: " + from);
        Thread.sleep(1800);

        // Click first suggestion (Delhi)
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
            "(//ul[@role='listbox']/li"
            + " | //li[@role='option']"
            + " | //ul[contains(@class,'autoSuggest')]//li"
            + " | //div[contains(@class,'autoSuggest')]//li)[1]"
        ))).click();
        System.out.println("[CabPage] Delhi suggestion clicked.");
        Thread.sleep(1000);

        // ── TO ────────────────────────────────────────────────────────────────
        System.out.println("[CabPage] Clicking TO field...");
        wait.until(ExpectedConditions.elementToBeClickable(toCityContainer)).click();
        Thread.sleep(800);

        WebElement toSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
            "//div[contains(@class,'hsw_autocomplePopup')]//input"
            + " | //input[@autocomplete='off' and @title='To']"
            + " | //input[contains(@placeholder,'To') or contains(@placeholder,'Search')]"
        )));
        // Only type the city name (strip ", Himachal Pradesh" etc.)
        String toCity = to.contains(",") ? to.split(",")[0].trim() : to;
        toSearch.clear();
        toSearch.sendKeys(toCity);
        System.out.println("[CabPage] Typed TO: " + toCity);
        Thread.sleep(1800);

        // Click Manali option specifically
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//li[contains(.,'Manali, Himachal Pradesh')]"
                + " | //li[@role='option'][contains(.,'Manali')]"
                + " | //p[contains(text(),'Manali')]"
            ))).click();
        } catch (Exception e) {
            // Fallback: first suggestion
            driver.findElement(By.xpath(
                "(//ul[@role='listbox']/li | //li[@role='option'] | //div[contains(@class,'autoSuggest')]//li)[1]"
            )).click();
        }
        System.out.println("[CabPage] Manali suggestion clicked.");
        Thread.sleep(1000);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Date picker – navigate to June 2026 and click day 10
    // ═══════════════════════════════════════════════════════════════════════

    public void selectDate(String targetMonth, String targetYear) throws InterruptedException {
        System.out.println("[CabPage] Opening date picker...");
        new Actions(driver).moveToElement(departureDateField).click().perform();
        Thread.sleep(600);

        for (int i = 0; i < 24; i++) {
            String text = wait.until(ExpectedConditions.visibilityOf(monthCaption)).getText().trim();
            String[] parts = text.split("\\s+");
            if (parts[0].equalsIgnoreCase(targetMonth) && parts[1].equals(targetYear)) {
                System.out.println("[CabPage] Month reached: " + text);
                break;
            }
            nextMonthBtn.click();
            Thread.sleep(400);
        }

        // Try multiple aria-label formats MakeMyTrip uses for day cells
        boolean clicked = false;
        String[] variants = {
            "Wed Jun 10 2026", "Jun 10 2026", "June 10 2026", "June 10, 2026"
        };
        for (String label : variants) {
            try {
                driver.findElement(By.xpath("//div[@aria-label='" + label + "']")).click();
                System.out.println("[CabPage] Date clicked → " + label);
                clicked = true;
                break;
            } catch (Exception ignored) {}
        }
        if (!clicked) {
            // Generic: any non-disabled day showing "10"
            try {
                driver.findElement(By.xpath(
                    "//div[contains(@aria-label,'Jun 10') or contains(@aria-label,'June 10')]"
                )).click();
                clicked = true;
                System.out.println("[CabPage] Date clicked via contains().");
            } catch (Exception ignored) {}
        }
        if (!clicked) {
            driver.findElement(By.xpath(
                "//div[contains(@class,'DayPicker-Day')]"
                + "[not(contains(@class,'outside'))][not(contains(@class,'disabled'))][text()='10']"
            )).click();
            System.out.println("[CabPage] Date clicked via day number.");
        }
        Thread.sleep(500);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Pickup-Time picker  (screenshot 3: "10 Hr", "30 min", APPLY)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * @param hourLabel   the hour digit string, e.g. "10"
     * @param minuteLabel the minute digit string, e.g. "30"
     */
    public void selectPickupTimeAndApply(String hourLabel, String minuteLabel) {
        System.out.println("[CabPage] Opening Pickup-Time picker...");

        // Open the time picker panel
        try {
            wait.until(ExpectedConditions.elementToBeClickable(pickupTimeLabel)).click();
            Thread.sleep(800);
        } catch (Exception e) {
            System.out.println("[CabPage] Time picker open: " + e.getMessage());
        }

        // Select the hour  (e.g. text = "10 Hr")
        try {
            driver.findElement(By.xpath(
                "//*[normalize-space(text())='" + hourLabel + " Hr']"
                + " | //*[contains(@class,'hrSlot') and contains(.,'" + hourLabel + "')]"
            )).click();
            System.out.println("[CabPage] Hour clicked: " + hourLabel);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("[CabPage] Hour click skipped: " + e.getMessage());
        }

        // Select the minute (e.g. text = "30 min")
        try {
            driver.findElement(By.xpath(
                "//*[normalize-space(text())='" + minuteLabel + " min']"
                + " | //*[contains(@class,'minSlot') and contains(.,'" + minuteLabel + "')]"
            )).click();
            System.out.println("[CabPage] Minute clicked: " + minuteLabel);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("[CabPage] Minute click skipped: " + e.getMessage());
        }

        // Click APPLY  (screenshot 3 shows a blue "APPLY" button in the time picker)
        try {
            driver.findElement(By.xpath(
                "//div[contains(@class,'applyBtn')]//span"
                + " | //span[normalize-space(text())='APPLY']"
                + " | //button[normalize-space(text())='APPLY']"
                + " | //a[normalize-space(text())='APPLY']"
            )).click();
            System.out.println("[CabPage] APPLY clicked.");
        } catch (Exception e) {
            System.out.println("[CabPage] APPLY click failed: " + e.getMessage());
        }

        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Search
    // ═══════════════════════════════════════════════════════════════════════

    public void clickSearch() {
        System.out.println("[CabPage] Clicking SEARCH button...");
        try {
            // Use JavaScript click to avoid any intercepted-click issues
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(searchBtn));
            js.executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            System.out.println("[CabPage] Primary search click failed, trying fallback: " + e.getMessage());
            try {
                WebElement fallback = driver.findElement(By.xpath(
                    "//a[contains(@class,'search') and not(contains(@href,'json'))]"
                    + " | //button[contains(@class,'search')]"
                ));
                js.executeScript("arguments[0].click();", fallback);
            } catch (Exception e2) {
                System.out.println("[CabPage] All search click attempts failed: " + e2.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Results page – wait for it, apply SUV filter, extract prices
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Waits until the cab listing page is loaded.
     * Success = URL contains "listing" or "cab-booking" or "outstation".
     * We do NOT require a specific card class because that changes with
     * MakeMyTrip deploys — URL change is the reliable signal.
     */
    public boolean waitForResultsPage() {
        System.out.println("[CabPage] Waiting for results page to load...");
        try {
            longWait.until(d -> {
                String url = d.getCurrentUrl();
                return url.contains("listing") || url.contains("cab-booking")
                       || url.contains("outstation") || url.contains("/cabs/");
            });
            // Give the JS-rendered cards a moment to paint
            Thread.sleep(3000);
            System.out.println("[CabPage] Results page loaded. URL: " + driver.getCurrentUrl());
            return true;
        } catch (Exception e) {
            System.out.println("[CabPage] Results page check failed: " + e.getMessage()
                + " | URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    public void selectSUVFilter() {
        System.out.println("[CabPage] Applying SUV filter...");
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//label[contains(normalize-space(.),'SUV')]"
                + " | //span[normalize-space(text())='SUV']"
                + " | //div[@role='checkbox' and contains(.,'SUV')]"
                + " | //li[contains(.,'SUV')]"
            )));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", el);
            System.out.println("[CabPage] SUV filter applied.");
            Thread.sleep(2500);
        } catch (Exception e) {
            System.out.println("[CabPage] SUV filter not applied (element not found): " + e.getMessage());
        }
    }

    public List<Integer> getAllPrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            // Look for any element that shows a ₹ price on the page
            List<WebElement> found = driver.findElements(By.xpath(
                "//span[contains(text(),'₹') and string-length(text()) < 12]"
                + " | //*[contains(@class,'price') and contains(text(),'₹')]"
                + " | //*[contains(@class,'fare') and contains(text(),'₹')]"
            ));
            System.out.println("[CabPage] Price elements found: " + found.size());
            for (WebElement el : found) {
                String raw = el.getText().replaceAll("[^0-9]", "");
                if (!raw.isEmpty() && raw.length() <= 7) {
                    prices.add(Integer.parseInt(raw));
                }
            }
        } catch (Exception e) {
            System.out.println("[CabPage] Price extraction error: " + e.getMessage());
        }
        return prices;
    }

    public int getLowestPrice() {
        List<Integer> p = getAllPrices();
        if (p.isEmpty()) { System.out.println("[CabPage] No prices found."); return -1; }
        return p.stream().min(Integer::compareTo).orElse(-1);
    }
}
