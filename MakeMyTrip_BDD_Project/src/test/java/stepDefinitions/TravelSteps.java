package stepDefinitions;

import io.cucumber.java.en.*;
import hooks.Hooks;
import pageObjects.*;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * TravelSteps – step definitions for TravelBooking.feature.
 *
 * Website   : https://automationexercise.com
 * One browser session, three sequential flows:
 *   Flow 1 – Product Search   → category filter → lowest price
 *   Flow 2 – Login page       → wrong credentials → capture error
 *   Flow 3 – Category page    → extract product names & prices into Lists
 */
public class TravelSteps {

    private static final String BASE_URL = "https://automationexercise.com/";

    private HomePage    home;
    private CabPage     search;   // repurposed: Product Search
    private GiftCardPage login;   // repurposed: Login Error
    private HotelPage   category; // repurposed: Category Extraction

    /** Lazy-init – page objects are created only AFTER @Before has set Hooks.driver. */
    private void initPages() {
        if (home == null) {
            home     = new HomePage(Hooks.driver, Hooks.wait);
            search   = new CabPage(Hooks.driver, Hooks.wait);
            login    = new GiftCardPage(Hooks.driver, Hooks.wait);
            category = new HotelPage(Hooks.driver, Hooks.wait);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FLOW 1 – PRODUCT SEARCH
    // ═════════════════════════════════════════════════════════════════════════

    @Given("User opens AutomationExercise home page")
    public void user_opens_home_page() {
        initPages();
        home.open(BASE_URL);
        boolean loaded = home.isLoaded();
        System.out.println("[TravelSteps] Home page loaded: " + loaded);
        takeScreenshot("01_HomePage");
    }

    @When("User navigates to Products page")
    public void user_navigates_to_products() {
        initPages();
        home.goToProducts();
        System.out.println("[TravelSteps] Navigated to Products.");
        takeScreenshot("02_ProductsPage");
    }

    @When("User searches for product {string}")
    public void user_searches_for_product(String keyword) throws InterruptedException {
        initPages();
        search.searchProduct(keyword);
        System.out.println("[TravelSteps] Searched: " + keyword);
    }

    @Then("User should see search results")
    public void user_should_see_search_results() {
        initPages();
        boolean found = search.waitForResults();
        System.out.println("[TravelSteps] Results visible: " + found);
        takeScreenshot("03_SearchResults");
    }

    @And("User applies {string} category filter")
    public void user_applies_category_filter(String category) throws InterruptedException {
        initPages();
        search.applyCategoryFilter(category);
        System.out.println("[TravelSteps] Category filter applied: " + category);
        takeScreenshot("04_CategoryFiltered");
    }

    @And("The lowest product price should be displayed")
    public void the_lowest_product_price_should_be_displayed() {
        initPages();
        List<Integer> prices = search.getAllPrices();
        System.out.println("╔══════════════════════════════════════════╗");
        if (prices.isEmpty()) {
            System.out.println("║  No prices extracted on filtered page.");
        } else {
            int lowest = prices.stream().min(Integer::compareTo).orElse(-1);
            System.out.println("║  All prices (Rs): " + prices);
            System.out.println("║  >>> LOWEST PRICE: Rs. " + lowest + " <<<");
        }
        System.out.println("╚══════════════════════════════════════════╝");
        takeScreenshot("05_LowestPrice");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FLOW 2 – LOGIN ERROR
    // ═════════════════════════════════════════════════════════════════════════

    @When("User navigates to the Login page")
    public void user_navigates_to_login() {
        initPages();
        Hooks.driver.get("https://automationexercise.com/login");
        System.out.println("[TravelSteps] Navigated to Login page.");
        takeScreenshot("06_LoginPage");
    }

    @And("User enters login email {string} and password {string}")
    public void user_enters_login_credentials(String email, String password) {
        initPages();
        login.enterEmail(email);
        login.enterPassword(password);
    }

    @And("User clicks the Login button")
    public void user_clicks_login_button() {
        initPages();
        login.clickLogin();
    }

    @Then("The login error message should be captured and screenshot taken")
    public void the_login_error_captured() throws InterruptedException {
        initPages();
        Thread.sleep(1500);
        String error = login.captureErrorMessage();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  Login Error Message: " + error);
        System.out.println("╚══════════════════════════════════════════╝");
        login.takeErrorScreenshot();
        takeScreenshot("07_LoginError");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FLOW 3 – CATEGORY PRODUCT EXTRACTION
    // ═════════════════════════════════════════════════════════════════════════

    @When("User navigates to Women Dress category")
    public void user_navigates_to_women_dress() throws InterruptedException {
        initPages();
        category.navigateToWomenDress();
        System.out.println("[TravelSteps] On Women → Dress category page.");
        takeScreenshot("08_WomenDressCategory");
    }

    @Then("All product names and prices should be extracted into a List")
    public void all_products_extracted() {
        initPages();
        List<String>  names  = category.extractAllProductNames();
        List<Integer> prices = category.extractAllProductPrices();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  WOMEN → DRESS CATEGORY – ALL PRODUCTS");
        System.out.println("╠══════════════════════════════════════════╣");
        for (int i = 0; i < Math.max(names.size(), prices.size()); i++) {
            String name  = i < names.size()  ? names.get(i)          : "(no name)";
            String price = i < prices.size() ? "Rs. " + prices.get(i) : "(no price)";
            System.out.printf("║  [%2d] %-30s  %s%n", i + 1, name, price);
        }
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Total products: " + Math.max(names.size(), prices.size()));
        System.out.println("╚══════════════════════════════════════════╝");

        category.takeCategoryScreenshot();
    }

    @And("User takes a final screenshot")
    public void take_final_screenshot() {
        takeScreenshot("09_Final");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPER
    // ═════════════════════════════════════════════════════════════════════════

    private void takeScreenshot(String fileName) {
        try {
            File src  = ((TakesScreenshot) Hooks.driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(System.getProperty("user.dir")
                        + "/target/Screenshots/" + fileName + ".png");
            if (dest.getParentFile() != null) dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            System.out.println("[TravelSteps] Screenshot saved: " + dest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[TravelSteps] Screenshot failed: " + e.getMessage());
        }
    }
}
