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
import java.util.List;

/**
 * HomePage - Page Object for MakeMyTrip home page.
 * Handles popups, navigation, and tab switching.
 */
public class HomePage extends BaseDriver {

    private final WebDriverWait shortWait;

    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        PageFactory.initElements(driver, this);
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    @FindBy(className = "commonModal__close")
    private WebElement commonModalCloseBtn;

    @FindBy(xpath = "//img[@alt='minimize']")
    private WebElement minimizeImg;

    @FindBy(css = "span.coachmark")
    private List<WebElement> coachmarkOverlays;

    @FindBy(xpath = "//span[contains(@class,'chCabs')]")
    private WebElement cabsTab;

    @FindBy(xpath = "//span[contains(@class,'chHotels')]")
    private WebElement hotelsTab;

    public void handlePopups() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(commonModalCloseBtn)).click();
            System.out.println("[HomePage] Closed login modal.");
        } catch (Exception ignored) {}

        try {
            if (minimizeImg.isDisplayed()) {
                minimizeImg.click();
                System.out.println("[HomePage] Minimized floating widget.");
            }
        } catch (Exception ignored) {}

        try {
            if (!coachmarkOverlays.isEmpty() && coachmarkOverlays.get(0).isDisplayed()) {
                coachmarkOverlays.get(0).click();
            }
        } catch (Exception ignored) {}
    }

    public void goToCabs() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(cabsTab)).click();
            System.out.println("[HomePage] Navigated to Cabs.");
        } catch (Exception e) {
            System.out.println("[HomePage] Could not click Cabs tab: " + e.getMessage());
        }
    }

    public void goToHotels() {
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(hotelsTab)).click();
            System.out.println("[HomePage] Navigated to Hotels.");
        } catch (Exception e) {
            driver.findElement(By.xpath("//span[text()='Hotels']")).click();
        }
    }

    public void scrollDown(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + pixels + ")");
    }

    public void goToMenu(String menuName) {
        try {
            driver.findElement(By.xpath("//span[contains(@class,'ch" + menuName + "')]")).click();
        } catch (Exception e) {
            driver.findElement(By.xpath("//span[text()='" + menuName + "']")).click();
        }
    }
}
