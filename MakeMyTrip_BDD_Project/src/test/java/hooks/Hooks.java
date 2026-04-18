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
 * Uses ThreadLocal<WebDriver> so that parallel test execution is safe:
 * each thread has its own isolated WebDriver instance.
 */
public class Hooks {

    private static final ThreadLocal<WebDriver>     driverThread = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThread   = new ThreadLocal<>();
    private static final ThreadLocal<String>        homeIdThread = new ThreadLocal<>();

    // ── Static accessors used by step definitions ────────────────────────────

    public static WebDriver getDriver()     { return driverThread.get(); }
    public static WebDriverWait getWait()   { return waitThread.get();   }
    public static String getHomeId()        { return homeIdThread.get(); }

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

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

        driverThread.set(driver);
        waitThread.set(wait);
        homeIdThread.set(driver.getWindowHandle());

        System.out.println("[Hooks] Browser started on thread: " + Thread.currentThread().getId());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = getDriver();

        if (driver != null) {
            if (scenario.isFailed() && ConfigReader.screenshotOnFail()) {
                byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
                if (screenshot.length > 0) {
                    scenario.attach(screenshot, "image/png", "FAILURE_Screenshot");
                }
                System.out.println("[Hooks] Failure screenshot attached for scenario: " + scenario.getName());
            }

            if (!scenario.isFailed() && ConfigReader.screenshotOnPass()) {
                byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
                if (screenshot.length > 0) {
                    scenario.attach(screenshot, "image/png", "PASS_Screenshot");
                }
            }

            driver.quit();
            driverThread.remove();
            waitThread.remove();
            homeIdThread.remove();
            System.out.println("[Hooks] Browser closed on thread: " + Thread.currentThread().getId());
        }
    }
}
