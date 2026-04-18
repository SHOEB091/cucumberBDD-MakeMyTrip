package pageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ScreenshotUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * HotelPage – Repurposed for automationexercise.com Category Extraction flow.
 *
 * Original role : MakeMyTrip Hotel adult-count extraction
 * Current role  : Navigate to Women > Dress category, extract ALL product names
 *                 and prices into Lists, and display them.
 *
 * Key selectors (automationexercise.com):
 *   /category_products/1     – Women → Dress  category page
 *   .productinfo p           – product name inside each card
 *   .productinfo h2          – product price inside each card
 */
public class HotelPage extends BaseDriver {

    private final JavascriptExecutor js;

    public HotelPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ── Locators ─────────────────────────────────────────────────────────────

    // All product name elements on the category page
    @FindBy(css = ".productinfo p")
    private List<WebElement> nameElements;

    // All product price elements on the category page
    @FindBy(css = ".productinfo h2")
    private List<WebElement> priceElements;

    // Each complete product card (used to confirm page is loaded)
    @FindBy(css = ".single-products")
    private List<WebElement> productCards;

    // ── Actions ──────────────────────────────────────────────────────────────

    /**
     * Navigates directly to the Women → Dress category page.
     * URL pattern: /category_products/1
     */
    public void navigateToWomenDress() throws InterruptedException {
        driver.get("https://automationexercise.com/category_products/1");
        System.out.println("[CategoryPage] Navigated to Women → Dress category.");
        wait.until(ExpectedConditions.visibilityOfAllElements(productCards));
        Thread.sleep(1000);
    }

    /**
     * Extracts all product names from the current category page into a List<String>.
     *
     * @return List of product name strings.
     */
    public List<String> extractAllProductNames() {
        List<String> names = new ArrayList<>();
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(nameElements));
            for (WebElement el : nameElements) {
                String name = el.getText().trim();
                if (!name.isEmpty()) names.add(name);
            }
            System.out.println("[CategoryPage] Names extracted (" + names.size() + "): " + names);
        } catch (Exception e) {
            System.out.println("[CategoryPage] Name extraction issue: " + e.getMessage());
        }
        return names;
    }

    /**
     * Extracts all product prices from the current category page into a List<Integer>.
     * Prices look like "Rs. 700" – all non-digit characters are stripped.
     *
     * @return List of integer prices.
     */
    public List<Integer> extractAllProductPrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            for (WebElement el : priceElements) {
                String raw = el.getText().replaceAll("[^0-9]", "");
                if (!raw.isEmpty()) prices.add(Integer.parseInt(raw));
            }
            System.out.println("[CategoryPage] Prices extracted (" + prices.size() + "): " + prices);
        } catch (Exception e) {
            System.out.println("[CategoryPage] Price extraction issue: " + e.getMessage());
        }
        return prices;
    }

    public String takeCategoryScreenshot() {
        String path = ScreenshotUtil.capture(driver, "Category_WomenDress");
        System.out.println("[CategoryPage] Screenshot saved: " + path);
        return path;
    }
}
