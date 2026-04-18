package pageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ScreenshotUtil;

import java.time.Duration;

/**
 * GiftCardPage – Repurposed for automationexercise.com Login Error flow.
 *
 * Original role : MakeMyTrip Gift Card purchase with invalid email
 * Current role  : Attempt login with wrong credentials, capture error message + screenshot
 *
 * Key selectors (automationexercise.com /login page):
 *   [data-qa='login-email']    – email input in the LOGIN section
 *   [data-qa='login-password'] – password input
 *   [data-qa='login-button']   – "Login" button
 *   p[style*='red']            – error paragraph "Your email or password is incorrect!"
 */
public class GiftCardPage extends BaseDriver {

    private final JavascriptExecutor js;
    private final WebDriverWait shortWait;

    public GiftCardPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js        = (JavascriptExecutor) driver;
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }

    // ── Locators ─────────────────────────────────────────────────────────────

    // Login section email input
    @FindBy(css = "[data-qa='login-email']")
    private WebElement loginEmail;

    // Login section password input
    @FindBy(css = "[data-qa='login-password']")
    private WebElement loginPassword;

    // Login button
    @FindBy(css = "[data-qa='login-button']")
    private WebElement loginButton;

    // Error paragraph shown for wrong credentials
    @FindBy(xpath = "//p[contains(@style,'red') or contains(@class,'error')]")
    private WebElement errorParagraph;

    // ── Actions ──────────────────────────────────────────────────────────────

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(loginEmail));
        loginEmail.clear();
        loginEmail.sendKeys(email);
        System.out.println("[LoginPage] Email entered: " + email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(loginPassword));
        loginPassword.clear();
        loginPassword.sendKeys(password);
        System.out.println("[LoginPage] Password entered.");
    }

    public void clickLogin() {
        js.executeScript("arguments[0].click();", loginButton);
        System.out.println("[LoginPage] Login button clicked.");
    }

    /**
     * Waits up to 8 seconds for the error message to appear and returns its text.
     * Falls back to scanning the page body if the styled paragraph isn't found.
     */
    public String captureErrorMessage() {
        WebDriverWait errWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try {
            String msg = errWait.until(ExpectedConditions.visibilityOf(errorParagraph)).getText().trim();
            System.out.println("[LoginPage] Error captured: " + msg);
            return msg;
        } catch (Exception e) {
            System.out.println("[LoginPage] Styled error not found, scanning body: " + e.getMessage());
            String body = driver.findElement(By.tagName("body")).getText();
            if (body.contains("incorrect")) return "Your email or password is incorrect!";
            if (body.contains("invalid"))   return "Invalid credentials.";
            return "Error message not found.";
        }
    }

    public String takeErrorScreenshot() {
        String path = ScreenshotUtil.capture(driver, "Login_WrongPassword_Error");
        System.out.println("[LoginPage] Error screenshot saved: " + path);
        return path;
    }
}
