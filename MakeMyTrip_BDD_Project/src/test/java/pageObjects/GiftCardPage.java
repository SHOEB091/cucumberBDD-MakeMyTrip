package pageObjects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ScreenshotUtil;

import java.time.Duration;

/**
 * GiftCardPage - Page Object for the Gift Cards section on MakeMyTrip.
 *
 * Scenario: Find Group Gifting → fill card details with invalid email
 *           → click Buy Now → capture error message + screenshot.
 */
public class GiftCardPage extends BaseDriver {

    private final JavascriptExecutor js;
    private final WebDriverWait shortWait;

    public GiftCardPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js        = (JavascriptExecutor) driver;
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    @FindBy(xpath = "//span[text()='Gift Cards']")
    private WebElement giftCardMenuLink;

    @FindBy(xpath = "//img[@alt='minimize']")
    private WebElement minimizeWidget;

    // ── Gift Card Selection ────────────────────────────────────────────────────

    @FindBy(xpath = "//div[@class='all__card__wrap']/ul/li[1]/div")
    private WebElement weddingCard;

    // ── Sender Details Form ────────────────────────────────────────────────────

    @FindBy(xpath = "//div[@class='deliver__content']")
    private WebElement deliverContent;

    @FindBy(xpath = "//input[@name='senderName']")
    private WebElement senderNameInput;

    @FindBy(xpath = "//input[@name='senderMobileNo']")
    private WebElement senderMobileInput;

    @FindBy(xpath = "//input[@name='senderEmailId']")
    private WebElement senderEmailInput;

    // ── Buy Now Button ─────────────────────────────────────────────────────────

    @FindBy(xpath = "//button[@data-cy='BookingDetails_440']")
    private WebElement buyNowBtn;

    // ── Error Message ──────────────────────────────────────────────────────────

    @FindBy(xpath = "//span[contains(@class,'error') or contains(@class,'Error') or contains(@class,'invalid')]")
    private WebElement errorMsg;

    // ── Actions ────────────────────────────────────────────────────────────────

    public void clickGiftCardsMenu() throws InterruptedException {
        js.executeScript("window.scrollBy(0, 250)");
        Thread.sleep(500);
        shortWait.until(ExpectedConditions.elementToBeClickable(giftCardMenuLink)).click();
        System.out.println("[GiftCardPage] Clicked Gift Cards menu.");
    }

    public void switchToNewTab() {
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }
        System.out.println("[GiftCardPage] Switched to latest tab.");
    }

    public void clickWeddingCard() throws InterruptedException {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(minimizeWidget)).click();
        } catch (Exception ignored) {}
        js.executeScript("window.scrollBy(0, 150)");
        Thread.sleep(500);
        wait.until(ExpectedConditions.elementToBeClickable(weddingCard)).click();
        System.out.println("[GiftCardPage] Selected Wedding Card.");
    }

    public void scrollToForm() throws InterruptedException {
        js.executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", deliverContent);
        Thread.sleep(1500);
    }

    public void enterName(String name) {
        wait.until(ExpectedConditions.visibilityOf(senderNameInput));
        senderNameInput.clear();
        senderNameInput.sendKeys(name);
    }

    public void enterMobile(String mobile) {
        wait.until(ExpectedConditions.visibilityOf(senderMobileInput));
        senderMobileInput.clear();
        senderMobileInput.sendKeys(mobile);
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(senderEmailInput));
        senderEmailInput.clear();
        senderEmailInput.sendKeys(email);
    }

    public void clickBuyNow() {
        js.executeScript("arguments[0].click();", buyNowBtn);
        System.out.println("[GiftCardPage] Clicked Buy Now.");
    }

    public String captureErrorMessage() {
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            String msg = longWait.until(ExpectedConditions.visibilityOf(errorMsg)).getText();
            System.out.println("[GiftCardPage] Error message captured: " + msg);
            return msg;
        } catch (Exception e) {
            System.out.println("[GiftCardPage] Could not find explicit error element; reading page text.");
            return driver.getPageSource().contains("invalid") ? "Invalid email detected in page." : "No error found.";
        }
    }

    public String takeErrorScreenshot() {
        String path = ScreenshotUtil.capture(driver, "GiftCard_InvalidEmail_Error");
        System.out.println("[GiftCardPage] Error screenshot saved: " + path);
        return path;
    }
}
