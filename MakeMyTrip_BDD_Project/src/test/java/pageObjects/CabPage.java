package pageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * CabPage – Repurposed for automationexercise.com Product Search flow.
 *
 * Original role : MakeMyTrip cab booking
 * Current role  : Search for a product, apply category filter, extract & find lowest price
 *
 * Key selectors (verified against automationexercise.com DOM):
 *   #search_product      – search text box
 *   #submit_search       – search button
 *   .productinfo p       – product name inside each card
 *   .productinfo h2      – product price inside each card
 *   .left-sidebar        – category filter panel on the left
 */
public class CabPage extends BaseDriver {

    private final JavascriptExecutor js;
    private final WebDriverWait longWait;

    public CabPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
        this.js       = (JavascriptExecutor) driver;
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    // ── Locators ─────────────────────────────────────────────────────────────

    @FindBy(id = "search_product")
    private WebElement searchBox;

    @FindBy(id = "submit_search")
    private WebElement searchBtn;

    // Product cards after search / after filter
    @FindBy(css = ".productinfo p")
    private List<WebElement> productNames;

    @FindBy(css = ".productinfo h2")
    private List<WebElement> productPrices;

    // The entire product card wrapper (confirms results are loaded)
    @FindBy(css = ".single-products")
    private List<WebElement> productCards;

    // Left sidebar – "WOMEN" panel heading (collapses/expands the sub-list)
    @FindBy(xpath = "//div[@id='accordian']//a[contains(normalize-space(.),'Women')]")
    private WebElement womenCategoryHeading;

    // "Tops" sub-link under Women
    @FindBy(xpath = "//div[@id='Women']//a[contains(normalize-space(.),'Tops')]")
    private WebElement topsLink;

    // ── Actions ──────────────────────────────────────────────────────────────

    /** Types the keyword into the search box and submits. */
    public void searchProduct(String keyword) throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOf(searchBox));
        searchBox.clear();
        searchBox.sendKeys(keyword);
        System.out.println("[ProductSearch] Typed keyword: " + keyword);
        searchBtn.click();
        System.out.println("[ProductSearch] Search submitted.");
        Thread.sleep(1500);
    }

    /** Waits until at least one product card is visible on the results page. */
    public boolean waitForResults() {
        try {
            longWait.until(ExpectedConditions.visibilityOfAllElements(productCards));
            System.out.println("[ProductSearch] Results loaded – " + productCards.size() + " cards found.");
            return true;
        } catch (Exception e) {
            System.out.println("[ProductSearch] Result wait: " + e.getMessage());
            return !productCards.isEmpty();
        }
    }

    /**
     * Applies the Women > Tops category filter via the left sidebar.
     * First click expands the "WOMEN" accordion, second click selects "Tops".
     */
    public void applyCategoryFilter(String category) throws InterruptedException {
        System.out.println("[ProductSearch] Applying category filter: " + category);
        try {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", womenCategoryHeading);
            Thread.sleep(400);
            womenCategoryHeading.click();
            System.out.println("[ProductSearch] Women accordion expanded.");
            Thread.sleep(700);
            wait.until(ExpectedConditions.elementToBeClickable(topsLink)).click();
            System.out.println("[ProductSearch] Tops link clicked.");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("[ProductSearch] Category filter click issue: " + e.getMessage());
        }
    }

    /**
     * Reads all product prices from the current page.
     * Prices on automationexercise.com look like "Rs. 500" – strips non-digits.
     */
    public List<Integer> getAllPrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(productPrices));
            for (WebElement el : productPrices) {
                String raw = el.getText().replaceAll("[^0-9]", "");
                if (!raw.isEmpty()) {
                    prices.add(Integer.parseInt(raw));
                }
            }
            System.out.println("[ProductSearch] Prices found: " + prices);
        } catch (Exception e) {
            System.out.println("[ProductSearch] Price extraction issue: " + e.getMessage());
        }
        return prices;
    }

    public int getLowestPrice() {
        List<Integer> p = getAllPrices();
        if (p.isEmpty()) { System.out.println("[ProductSearch] No prices found."); return -1; }
        return p.stream().min(Integer::compareTo).orElse(-1);
    }

    /** Returns all product names from the currently visible cards. */
    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        try {
            for (WebElement el : productNames) {
                String name = el.getText().trim();
                if (!name.isEmpty()) names.add(name);
            }
        } catch (Exception e) {
            System.out.println("[ProductSearch] Name extraction issue: " + e.getMessage());
        }
        return names;
    }
}
