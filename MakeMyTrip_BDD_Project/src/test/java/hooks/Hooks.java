package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;
import utils.ScreenshotUtil;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Hooks – Cucumber @Before / @After lifecycle.
 *
 * ONE static browser for the whole scenario.
 * Stealth ChromeOptions are applied so that MakeMyTrip does NOT
 * detect Selenium as a bot (which causes a "200-OK" JSON block page).
 *
 * Without stealth options Chrome exposes:
 *   navigator.webdriver = true
 *   window.cdc_*        properties set by ChromeDriver
 * Both are checked by MakeMyTrip's bot-detection script.
 */
public class Hooks {

    public static WebDriver     driver;
    public static WebDriverWait wait;
    public static String        homeId;

    public static WebDriver     getDriver() { return driver; }
    public static WebDriverWait getWait()   { return wait;   }
    public static String        getHomeId() { return homeId; }

    // ─────────────────────────────────────────────────────────────────────────

    @Before(order = 0)
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ── Standard flags ──────────────────────────────────────────────────
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--lang=en-IN");

        // ── Bot-detection bypass ─────────────────────────────────────────────
        // 1. Remove the "Chrome is being controlled by automated software" bar
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        // 2. Disable the useAutomationExtension flag (sets navigator.webdriver = false)
        options.setExperimentalOption("useAutomationExtension", false);
        // 3. Disable the AutomationControlled blink feature
        options.addArguments("--disable-blink-features=AutomationControlled");
        // 4. Realistic user-agent (change version to match installed Chrome if needed)
        options.addArguments(
            "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/147.0.0.0 Safari/537.36"
        );
        // 5. Disable password saving pop-up
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        wait   = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        homeId = driver.getWindowHandle();

        // 6. Override navigator.webdriver via JavaScript (runs on every new document)
        //    This prevents site-side JS from detecting the automated context.
        try {
            ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );
        } catch (Exception ignored) {}

        System.out.println("[Hooks] Browser started (stealth mode). Home tab ID: " + homeId);
    }

    // ─────────────────────────────────────────────────────────────────────────

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
            if (screenshot.length > 0) {
                String label = scenario.isFailed() ? "FAILURE_Screenshot" : "PASS_Screenshot";
                scenario.attach(screenshot, "image/png", label);
            }
            if (scenario.isFailed()) {
                System.out.println("[Hooks] FAILED: " + scenario.getName());
            }
            driver.quit();
            driver = null;
            System.out.println("[Hooks] Browser closed.");
        }
    }
}
