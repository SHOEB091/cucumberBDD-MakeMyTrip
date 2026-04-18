package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;
import utils.ScreenshotUtil;

import java.time.Duration;

/**
 * Hooks - Cucumber lifecycle hooks.
 *
 * Uses a simple static WebDriver (no ThreadLocal) since parallel
 * testing is disabled and all three flows share ONE browser session.
 *
 * Flow:
 *   Tab 1 (homeId) → Cabs search
 *   Tab 2           → Gift Cards (MakeMyTrip opens this automatically)
 *   Tab 1 (homeId) → Hotels
 */
public class Hooks {

    public static WebDriver     driver;
    public static WebDriverWait wait;
    public static String        homeId;

    // Backward-compatible getters (used by new step-def classes)
    public static WebDriver     getDriver()  { return driver;  }
    public static WebDriverWait getWait()    { return wait;    }
    public static String        getHomeId()  { return homeId;  }

    // ── Cucumber Lifecycle ───────────────────────────────────────────────────

    @Before(order = 0)
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        wait   = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        homeId = driver.getWindowHandle();

        System.out.println("[Hooks] Browser started. Home tab ID: " + homeId);
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            if (scenario.isFailed()) {
                byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
                if (screenshot.length > 0) {
                    scenario.attach(screenshot, "image/png", "FAILURE_Screenshot");
                }
                System.out.println("[Hooks] Failure screenshot attached for: " + scenario.getName());
            } else {
                byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
                if (screenshot.length > 0) {
                    scenario.attach(screenshot, "image/png", "PASS_Screenshot");
                }
            }
            driver.quit();
            driver = null;
            System.out.println("[Hooks] Browser closed.");
        }
    }
}
