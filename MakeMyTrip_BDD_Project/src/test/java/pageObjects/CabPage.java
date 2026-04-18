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
 * CabPage - Page Object for MakeMyTrip Cabs booking.
 *
 * XPaths validated against the live DOM structure visible in
 * the screenshots (data-cy attributes are stable selectors on MMT).
 *
 * Flow:
 *   1. Click Cabs tab
 *   2. Enter From city (Delhi) → select from autocomplete
 *   3. Enter To city   (Manali) → select from autocomplete
 *   4. Select departure date (June 10)
 *   5. Open Pickup-Time picker → select 10 Hr, 30 min → click APPLY
 *   6. Click SEARCH
 *   7. Apply SUV filter → capture lowest price
 */
public class CabPage extends BaseDriver {

    private final JavascriptExecutor js;
    private final WebDriverWait      longWait;  // 20 s – for results page

    public CabPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js       = (JavascriptExecutor) driver;
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Locators  (from DevTools visible in screenshots)
    // ═══════════════════════════════════════════════════════════════════════

    // ── Popups ──────────────────────────────────────────────────────────────
    @FindBy(className = "commonModal__close")
    private WebElement closePopup;

    @FindBy(xpath = "//img[@alt='minimize']")
    private WebElement minimizeFloat;

    // ── Cabs tab (top nav) ───────────────────────────────────────────────────
    @FindBy(xpath = "//span[contains(@class,'chCabs')] | //a[contains(@href,'cab')]//li | //li[.//span[contains(text(),'Cabs')]]")
    private WebElement cabsTab;

    // ── FROM field ────────────────────────────────────────────────────────────
    // The container div (data-cy from screenshot 1)
    @FindBy(xpath = "//div[@data-cy='OutstationOneWayWidget_57'] | //div[contains(@class,'searchCity')]")
    private WebElement fromCityContainer;

    // The readonly text input that acts as trigger
    @FindBy(xpath = "//input[@data-cy='fromCity'] | //input[@id='fromCity']")
    private WebElement fromCityInput;

    // ── TO field ──────────────────────────────────────────────────────────────
    // Container (data-cy from screenshot 2)
    @FindBy(xpath = "//div[@data-cy='OutstationOneWayWidget_59'] | //div[contains(@class,'searchToCity')]")
    private WebElement toCityContainer;

    // ── Date Picker ───────────────────────────────────────────────────────────
    @FindBy(id = "departure")
    private WebElement departureDateField;

    @FindBy(xpath = "//div[@class='DayPicker-Caption'] | //div[contains(@class,'DayPicker-Caption')]")
    private WebElement monthCaption;

    @FindBy(xpath = "//span[contains(@class,'DayPicker-NavButton--next')]")
    private WebElement nextMonthBtn;

    // ── Pickup Time ───────────────────────────────────────────────────────────
    @FindBy(xpath = "//label[@for='pickupTime'] | //div[contains(@class,'pickupTime')]")
    private WebElement pickupTimeLabel;

    // ── Search Button ─────────────────────────────────────────────────────────
    @FindBy(xpath = "//p[@data-cy='onewaySearch']/a | //a[@data-cy='search-btn'] | //button[contains(@class,'search') and contains(text(),'SEARCH')]")
    private WebElement searchBtn;

    // ── SUV Filter ────────────────────────────────────────────────────────────
    // 3rd checkbox in the filter sidebar (SUV is typically 3rd car type)
    @FindBy(xpath = "//div[contains(@class,'filterSection')]//div[@role='checkbox'][3] | //label[contains(text(),'SUV')]")
    private WebElement suvCheckbox;

    // ── Price List ────────────────────────────────────────────────────────────
    @FindBy(xpath = "//*[contains(@class,'cabDetailsCard_price') or contains(@class,'price__')]")
    private List<WebElement> priceElements;

    // ═══════════════════════════════════════════════════════════════════════
    //  Actions
    // ═══════════════════════════════════════════════════════════════════════

    public void handlePopups() {
        WebDriverWait short3 = new WebDriverWait(driver, Duration.ofSeconds(4));
        try { short3.until(ExpectedConditions.elementToBeClickable(closePopup)).click(); } catch (Exception ignored) {}
        try { short3.until(ExpectedConditions.elementToBeClickable(minimizeFloat)).click(); } catch (Exception ignored) {}
    }

    public void clickCabsTab() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cabsTab)).click();
            System.out.println("[CabPage] Cabs tab clicked.");
        } catch (Exception e) {
            System.out.println("[CabPage] Cabs tab fallback: " + e.getMessage());
            driver.findElement(By.xpath("//a[contains(@href,'cab')]")).click();
        }
        handlePopups();
    }

    // ── City Entry ──────────────────────────────────────────────────────────

    public void enterCities(String from, String to) throws InterruptedException {

        // ── FROM ──
        System.out.println("[CabPage] Clicking FROM field...");
        wait.until(ExpectedConditions.elementToBeClickable(fromCityInput)).click();
        Thread.sleep(800);

        // The autocomplete search input appears inside .hsw_autocomplePopup
        WebElement fromSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
            "//div[contains(@class,'hsw_autocomplePopup')]//input"
            + " | //input[@autocomplete='off' and @title='From']"
            + " | //input[@placeholder='Search city']"
        )));
        fromSearch.clear();
        fromSearch.sendKeys(from);
        System.out.println("[CabPage] Typed FROM: " + from);
        Thread.sleep(1800);

        // Click the first suggestion (Delhi city entry)
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
            "(//ul[@role='listbox']/li"
            + " | //li[@role='option']"
            + " | //ul[contains(@class,'autoSuggest')]//li"
            + " | //div[contains(@class,'autoSuggest')]//li)[1]"
        ))).click();
        System.out.println("[CabPage] Delhi suggestion selected.");
        Thread.sleep(1200);

        // ── TO ──
        System.out.println("[CabPage] Clicking TO field...");
        wait.until(ExpectedConditions.elementToBeClickable(toCityContainer)).click();
        Thread.sleep(800);

        WebElement toSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
            "//div[contains(@class,'hsw_autocomplePopup')]//input"
            + " | //input[@autocomplete='off' and @title='To']"
            + " | //input[@placeholder='Search city']"
        )));
        toSearch.clear();
        toSearch.sendKeys(to.contains(",") ? to.split(",")[0].trim() : to);
        System.out.println("[CabPage] Typed TO: " + to);
        Thread.sleep(1800);

        // Click Manali specifically from the dropdown
        WebElement manaliOption;
        try {
            manaliOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//li[contains(.,'Manali, Himachal Pradesh')]"
                + " | //li[@role='option'][contains(.,'Manali')]"
                + " | //p[contains(text(),'Manali')]"
            )));
        } catch (Exception e) {
            // Fallback: click first suggestion
            manaliOption = driver.findElement(By.xpath(
                "(//ul[@role='listbox']/li | //li[@role='option'] | //div[contains(@class,'autoSuggest')]//li)[1]"
            ));
        }
        manaliOption.click();
        System.out.println("[CabPage] Manali suggestion selected.");
        Thread.sleep(1000);
    }

    // ── Date Selection ─────────────────────────────────────────────────────

    /**
     * Opens the date picker, navigates to targetMonth targetYear, then clicks day 10 (June 10).
     */
    public void selectDate(String targetMonth, String targetYear) throws InterruptedException {
        System.out.println("[CabPage] Opening date picker...");
        new Actions(driver).moveToElement(departureDateField).click().perform();
        Thread.sleep(600);

        // Navigate to the correct month
        int attempts = 0;
        while (attempts < 24) {
            String text = wait.until(ExpectedConditions.visibilityOf(monthCaption)).getText().trim();
            String[] parts = text.split("\\s+");
            if (parts[0].equalsIgnoreCase(targetMonth) && parts[1].equals(targetYear)) {
                System.out.println("[CabPage] Month found: " + text);
                break;
            }
            nextMonthBtn.click();
            Thread.sleep(400);
            attempts++;
        }

        // Click June 10 – try multiple aria-label formats used by MMT
        boolean clicked = false;
        String[] ariaVariants = {
            "Wed Jun 10 2026",
            "Jun 10 2026",
            "June 10, 2026",
            "June 10 2026"
        };
        for (String label : ariaVariants) {
            try {
                driver.findElement(By.xpath("//div[@aria-label='" + label + "']")).click();
                System.out.println("[CabPage] Date clicked with aria-label: " + label);
                clicked = true;
                break;
            } catch (Exception ignored) {}
        }

        // Generic fallback: any non-disabled day cell showing "10"
        if (!clicked) {
            try {
                driver.findElement(By.xpath(
                    "//div[contains(@aria-label,'Jun 10') or contains(@aria-label,'June 10')]"
                )).click();
                System.out.println("[CabPage] Date clicked via contains aria-label.");
                clicked = true;
            } catch (Exception ignored) {}
        }
        if (!clicked) {
            driver.findElement(By.xpath(
                "//div[@class='DayPicker-Day'][not(contains(@class,'outside'))][not(contains(@class,'disabled'))][text()='10']"
            )).click();
            System.out.println("[CabPage] Date clicked via day-number text.");
        }
        Thread.sleep(500);
    }

    // ── Pickup Time ────────────────────────────────────────────────────────

    /**
     * Opens the Pickup-Time picker, selects the given hour and minute, then clicks APPLY.
     * Based on screenshot 3: hours shown as "10 Hr", minutes as "30 min".
     *
     * @param hourLabel   e.g. "10"  → clicks "10 Hr"
     * @param minuteLabel e.g. "30"  → clicks "30 min"
     */
    public void selectPickupTimeAndApply(String hourLabel, String minuteLabel) {
        System.out.println("[CabPage] Opening time picker...");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(pickupTimeLabel)).click();
            Thread.sleep(800);
        } catch (Exception e) {
            System.out.println("[CabPage] Pickup-Time click: " + e.getMessage());
        }

        // Select hour  (e.g. "10 Hr")
        try {
            driver.findElement(By.xpath(
                "//*[normalize-space(text())='" + hourLabel + " Hr']"
                + " | //*[contains(@class,'hrSlot') and contains(text(),'" + hourLabel + "')]"
            )).click();
            System.out.println("[CabPage] Hour selected: " + hourLabel);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("[CabPage] Hour select failed: " + e.getMessage());
        }

        // Select minute (e.g. "30 min")
        try {
            driver.findElement(By.xpath(
                "//*[normalize-space(text())='" + minuteLabel + " min']"
                + " | //*[contains(@class,'minSlot') and contains(text(),'" + minuteLabel + "')]"
            )).click();
            System.out.println("[CabPage] Minute selected: " + minuteLabel);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("[CabPage] Minute select failed: " + e.getMessage());
        }

        // Click APPLY button (visible in screenshot 3 as a blue "APPLY" button)
        try {
            driver.findElement(By.xpath(
                "//div[contains(@class,'applyBtn')]//span"
                + " | //span[text()='APPLY']"
                + " | //button[normalize-space(text())='APPLY']"
                + " | //div[normalize-space(text())='APPLY']"
            )).click();
            System.out.println("[CabPage] APPLY clicked.");
        } catch (Exception e) {
            System.out.println("[CabPage] APPLY click failed: " + e.getMessage());
        }

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    // ── Search ─────────────────────────────────────────────────────────────

    public void clickSearch() {
        System.out.println("[CabPage] Clicking SEARCH...");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
        } catch (Exception e) {
            // Fallback by text
            driver.findElement(By.xpath("//a[contains(text(),'SEARCH')] | //button[contains(text(),'SEARCH')]")).click();
        }
    }

    // ── SUV Filter ─────────────────────────────────────────────────────────

    public void selectSUVFilter() {
        System.out.println("[CabPage] Applying SUV filter...");
        try {
            longWait.until(ExpectedConditions.elementToBeClickable(suvCheckbox));
            new Actions(driver).moveToElement(suvCheckbox).click().perform();
            System.out.println("[CabPage] SUV filter applied.");
        } catch (Exception e) {
            System.out.println("[CabPage] SUV filter failed: " + e.getMessage());
        }
    }

    // ── Price Capture ──────────────────────────────────────────────────────

    public List<Integer> getAllPrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            longWait.until(ExpectedConditions.visibilityOfAllElements(priceElements));
            for (WebElement el : priceElements) {
                String raw = el.getText().replaceAll("[^0-9]", "");
                if (!raw.isEmpty()) prices.add(Integer.parseInt(raw));
            }
        } catch (Exception e) {
            System.out.println("[CabPage] Price extraction: " + e.getMessage());
        }
        return prices;
    }

    public int getLowestPrice() {
        List<Integer> prices = getAllPrices();
        if (prices.isEmpty()) { System.out.println("[CabPage] No prices found."); return -1; }
        return prices.stream().min(Integer::compareTo).orElse(-1);
    }
}
