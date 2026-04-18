package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ScreenshotUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HotelPage - Page Object for the Hotel booking section on MakeMyTrip.
 *
 * Scenario: Open guest selector → extract all adult numbers → store in List → display.
 */
public class HotelPage extends BaseDriver {

    private final WebDriverWait shortWait;
    private final JavascriptExecutor js;

    public HotelPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        PageFactory.initElements(driver, this);
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        this.js        = (JavascriptExecutor) driver;
    }

    // ── Hotels Tab ─────────────────────────────────────────────────────────────

    @FindBy(xpath = "//span[contains(@class,'chHotels')]")
    private WebElement hotelsTab;

    // ── Guest / Adult Selector ─────────────────────────────────────────────────

    @FindBy(xpath = "//label[@for='guest'] | //div[contains(@class,'guestInfoSection')]")
    private WebElement guestLabel;

    @FindBy(xpath = "//div[@class='rmsGst']//span[contains(@class,'count') or @class='count']")
    private List<WebElement> adultCountSpans;

    @FindBy(xpath = "//div[@class='rmsGst']/div[1]/div[2]/div[2]/button[2]")
    private WebElement addAdultBtn;

    @FindBy(xpath = "//div[@class='rmsGst']/div[1]/div[2]/div[2]/span")
    private WebElement adultCountSpan;

    // ── Actions ────────────────────────────────────────────────────────────────

    public void clickHotelsTab() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(hotelsTab)).click();
            System.out.println("[HotelPage] Navigated to Hotels tab.");
        } catch (Exception e) {
            System.out.println("[HotelPage] Hotels tab click issue: " + e.getMessage());
        }
    }

    public void openGuestDropdown() throws InterruptedException {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[@for='guest'] | //*[@data-cy='guestLabel']")
            )).click();
            Thread.sleep(1500);
            System.out.println("[HotelPage] Guest dropdown opened.");
        } catch (Exception e) {
            System.out.println("[HotelPage] Could not open guest dropdown: " + e.getMessage());
        }
    }

    /**
     * Extracts all displayed adult-count numbers visible inside the guest selector.
     * Clicks + repeatedly until disabled to reveal all valid adult numbers (1 through max).
     *
     * @return List of integers representing each adult count that was shown (1..maxAdults)
     */
    public List<Integer> extractAllAdultNumbers() throws InterruptedException {
        List<Integer> adultNumbers = new ArrayList<>();

        try {
            WebElement addBtn = driver.findElement(
                    By.xpath("//div[@class='rmsGst']/div[1]/div[2]/div[2]/button[2]"));
            WebElement countEl = driver.findElement(
                    By.xpath("//div[@class='rmsGst']/div[1]/div[2]/div[2]/span"));

            // start from current count
            int currentVal = parseNumber(countEl.getText());
            if (currentVal > 0) adultNumbers.add(currentVal);

            // click + until disabled, collecting each number shown
            while (addBtn.isEnabled()) {
                addBtn.click();
                Thread.sleep(300);
                currentVal = parseNumber(countEl.getText());
                if (currentVal > 0 && !adultNumbers.contains(currentVal)) {
                    adultNumbers.add(currentVal);
                }
            }

            System.out.println("[HotelPage] Adult numbers extracted: " + adultNumbers);

        } catch (Exception e) {
            System.out.println("[HotelPage] Error extracting adult numbers: " + e.getMessage());

            // Fallback: scan full page source for numbers near the word "adult"
            adultNumbers = extractNumbersFromPageSource();
        }

        return adultNumbers;
    }

    /**
     * Fallback: scan the entire visible page text and extract numbers adjacent to "adult".
     */
    private List<Integer> extractNumbersFromPageSource() {
        List<Integer> found = new ArrayList<>();
        String pageText = driver.findElement(By.tagName("body")).getText();
        Pattern pattern = Pattern.compile("(\\d+)\\s*(?:Adult|adult|ADULT)");
        Matcher matcher = pattern.matcher(pageText);
        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            if (!found.contains(num)) found.add(num);
        }
        return found;
    }

    private int parseNumber(String text) {
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String captureHotelScreenshot() {
        return ScreenshotUtil.capture(driver, "Hotel_GuestSelection");
    }
}
