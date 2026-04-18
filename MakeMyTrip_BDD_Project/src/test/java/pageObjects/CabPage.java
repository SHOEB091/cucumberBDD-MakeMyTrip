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
 * CabPage - Page Object for the Cabs booking section on MakeMyTrip.
 */
public class CabPage extends BaseDriver {

    private final JavascriptExecutor js;

    public CabPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ── Popup / Modal ──────────────────────────────────────────────────────────

    @FindBy(className = "commonModal__close")
    private WebElement closePopup;

    @FindBy(xpath = "//img[@alt='minimize']")
    private WebElement minimizeFloat;

    // ── Cabs Tab ───────────────────────────────────────────────────────────────

    @FindBy(xpath = "//span[contains(@class,'chCabs')]")
    private WebElement cabsTab;

    // ── From / To City Fields ──────────────────────────────────────────────────

    @FindBy(css = "#fromCity")
    private WebElement fromCity;

    @FindBy(xpath = "//input[@autocomplete='off' and @title='From']")
    private WebElement fromInput;

    @FindBy(xpath = "//*[text()='Delhi']")
    private WebElement delhiOption;

    @FindBy(xpath = "//label[@for='toCity']/parent::div")
    private WebElement toCityContainer;

    @FindBy(xpath = "//input[@autocomplete='off' and @title='To']")
    private WebElement toInput;

    @FindBy(xpath = "//li[@role='option']/div")
    private WebElement firstSuggestion;

    // ── Date Picker ────────────────────────────────────────────────────────────

    @FindBy(id = "departure")
    private WebElement departureDateField;

    @FindBy(xpath = "//div[@class='DayPicker-Caption']")
    private WebElement monthCaption;

    @FindBy(xpath = "//span[contains(@class,'DayPicker-NavButton--next')]")
    private WebElement nextMonthBtn;

    // ── Search Button ──────────────────────────────────────────────────────────

    @FindBy(xpath = "//p[@data-cy='onewaySearch']/a")
    private WebElement searchBtn;

    // ── Filter: SUV ────────────────────────────────────────────────────────────

    @FindBy(xpath = "//div[@class='filterSection_contentWrapper___IFFB']//div[3][@role='checkbox']")
    private WebElement suvCheckbox;

    // ── Price Elements ─────────────────────────────────────────────────────────

    @FindBy(css = "span[class*='cabDetailsCard_price']")
    private List<WebElement> priceElements;

    // ── Actions ────────────────────────────────────────────────────────────────

    public void handlePopups() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(closePopup)).click();
        } catch (Exception ignored) {}
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(minimizeFloat)).click();
        } catch (Exception ignored) {}
    }

    public void clickCabsTab() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cabsTab)).click();
        } catch (Exception e) {
            System.out.println("[CabPage] Cabs tab click failed: " + e.getMessage());
        }
    }

    public void enterCities(String from, String to) throws InterruptedException {
        fromCity.click();
        Thread.sleep(500);
        fromInput.clear();
        fromInput.sendKeys(from.toLowerCase());
        Thread.sleep(1500);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(delhiOption)).click();
        } catch (Exception e) {
            driver.findElement(By.xpath("//*[contains(text(),'" + from + "')]")).click();
        }

        Thread.sleep(1000);
        toCityContainer.click();
        Thread.sleep(500);
        toInput.clear();
        toInput.sendKeys(to.split(",")[0].toLowerCase());
        Thread.sleep(2000);
        firstSuggestion.click();
    }

    public void selectDate(String targetMonth, String targetYear) throws InterruptedException {
        new Actions(driver).moveToElement(departureDateField).click().perform();
        Thread.sleep(500);

        int maxAttempts = 24;
        int attempts = 0;
        while (attempts < maxAttempts) {
            String text = wait.until(ExpectedConditions.visibilityOf(monthCaption)).getText();
            String[] parts = text.split(" ");
            if (parts[0].equalsIgnoreCase(targetMonth) && parts[1].equals(targetYear)) {
                break;
            }
            nextMonthBtn.click();
            Thread.sleep(400);
            attempts++;
        }
        driver.findElement(By.xpath(
                "//div[contains(@aria-label,'10 " + targetYear + "') or contains(@aria-label,'Jun 10')]"
        )).click();
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
        System.out.println("[CabPage] Search clicked.");
    }

    public void selectSUVFilter() {
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            longWait.until(ExpectedConditions.elementToBeClickable(suvCheckbox));
            new Actions(driver).moveToElement(suvCheckbox).click().perform();
            System.out.println("[CabPage] SUV filter applied.");
        } catch (Exception e) {
            System.out.println("[CabPage] SUV filter could not be applied: " + e.getMessage());
        }
    }

    public List<Integer> getAllPrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            longWait.until(ExpectedConditions.visibilityOfAllElements(priceElements));
            for (WebElement el : priceElements) {
                String raw = el.getText().replaceAll("[^0-9]", "");
                if (!raw.isEmpty()) {
                    prices.add(Integer.parseInt(raw));
                }
            }
        } catch (Exception e) {
            System.out.println("[CabPage] Could not retrieve prices: " + e.getMessage());
        }
        return prices;
    }

    public int getLowestPrice() {
        List<Integer> prices = getAllPrices();
        if (prices.isEmpty()) {
            System.out.println("[CabPage] No prices found.");
            return -1;
        }
        return prices.stream().min(Integer::compareTo).orElse(-1);
    }
}
