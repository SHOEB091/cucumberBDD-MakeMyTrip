package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * HomePage – automationexercise.com landing page.
 *
 * Responsibilities:
 *   - Navigate to base URL
 *   - Dismiss cookie / ad banners if present
 *   - Verify the page is loaded
 */
public class HomePage extends BaseDriver {

    private final WebDriverWait shortWait;

    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        PageFactory.initElements(driver, this);
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
    }

    // Site logo confirms the page has loaded
    @FindBy(xpath = "//img[@alt='Website for automation practice']")
    private WebElement siteLogo;

    // "Products" link in the top navigation
    @FindBy(xpath = "//a[@href='/products']")
    private WebElement productsNav;

    // "Signup / Login" link
    @FindBy(xpath = "//a[@href='/login']")
    private WebElement loginNav;

    // ── Actions ──────────────────────────────────────────────────────────────

    public void open(String url) {
        driver.get(url);
        System.out.println("[HomePage] Navigated to: " + url);
        dismissAds();
    }

    /**
     * Dismisses the Google AdSense iframe ad that sometimes overlays the page.
     * Silently ignores if no ad is present.
     */
    public void dismissAds() {
        try {
            shortWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                    By.cssSelector("iframe[id^='aswift']")));
            WebElement closeBtn = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("dismiss-button")));
            closeBtn.click();
            driver.switchTo().defaultContent();
            System.out.println("[HomePage] Ad dismissed.");
        } catch (Exception ignored) {
            try { driver.switchTo().defaultContent(); } catch (Exception e2) { /* ignore */ }
        }
    }

    public boolean isLoaded() {
        try {
            shortWait.until(ExpectedConditions.visibilityOf(siteLogo));
            System.out.println("[HomePage] Home page is loaded.");
            return true;
        } catch (Exception e) {
            System.out.println("[HomePage] Logo not found, checking URL instead.");
            return driver.getCurrentUrl().contains("automationexercise");
        }
    }

    public void goToProducts() {
        wait.until(ExpectedConditions.elementToBeClickable(productsNav)).click();
        System.out.println("[HomePage] Clicked Products nav.");
    }

    public void goToLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginNav)).click();
        System.out.println("[HomePage] Clicked Login nav.");
    }

    public void scrollDown(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + pixels + ")");
    }
}
