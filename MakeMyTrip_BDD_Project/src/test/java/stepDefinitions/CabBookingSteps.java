package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;
import pageObjects.CabPage;
import pageObjects.HomePage;
import utils.ScreenshotUtil;

import java.util.List;

/**
 * CabBookingSteps - Step definitions for CabBooking.feature
 *
 * Covers:
 *   - Navigate to Cabs
 *   - Enter From / To cities
 *   - Select travel date
 *   - Click search
 *   - Apply SUV filter
 *   - Extract and display the lowest price
 */
public class CabBookingSteps {

    private HomePage home;
    private CabPage  cabs;

    // ── Background ──────────────────────────────────────────────────────────────

    @Given("User opens MakeMyTrip homepage for Cab Booking")
    public void openHomePageForCabs() {
        home = new HomePage(Hooks.getDriver(), Hooks.getWait());
        cabs = new CabPage(Hooks.getDriver(), Hooks.getWait());
        Hooks.getDriver().get("https://www.makemytrip.com/");
        home.handlePopups();
        System.out.println("[CabBookingSteps] MakeMyTrip home page loaded.");
    }

    // ── Navigate to Cabs ────────────────────────────────────────────────────────

    @When("User navigates to the Cabs section")
    public void navigateToCabs() {
        cabs.clickCabsTab();
        cabs.handlePopups();
    }

    // ── Enter Cities ────────────────────────────────────────────────────────────

    @When("User enters from city {string} and to city {string}")
    public void enterCities(String from, String to) throws InterruptedException {
        cabs.enterCities(from, to);
        System.out.printf("[CabBookingSteps] Cities entered: %s -> %s%n", from, to);
    }

    // ── Select Date ─────────────────────────────────────────────────────────────

    @When("User selects travel date month {string} and year {string}")
    public void selectDate(String month, String year) throws InterruptedException {
        cabs.selectDate(month, year);
        System.out.printf("[CabBookingSteps] Date selected: %s %s%n", month, year);
    }

    // ── Search ──────────────────────────────────────────────────────────────────

    @When("User clicks Search Cabs button")
    public void clickSearch() {
        cabs.clickSearch();
    }

    // ── Available Cabs ──────────────────────────────────────────────────────────

    @Then("User should see list of available cabs")
    public void verifyAvailableCabs() throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("[CabBookingSteps] Waiting for cab list to load...");
        ScreenshotUtil.capture(Hooks.getDriver(), "CabList_Loaded");
    }

    // ── SUV Filter ──────────────────────────────────────────────────────────────

    @And("User applies SUV filter")
    public void applySUVFilter() throws InterruptedException {
        cabs.selectSUVFilter();
        Thread.sleep(2000);
        ScreenshotUtil.capture(Hooks.getDriver(), "CabList_SUV_Filter");
    }

    // ── Lowest Price ────────────────────────────────────────────────────────────

    @And("The lowest SUV cab price is captured and displayed")
    public void displayLowestPrice() {
        List<Integer> prices = cabs.getAllPrices();
        if (prices.isEmpty()) {
            System.out.println("[CabBookingSteps] *** No prices found on page. ***");
        } else {
            int lowest = prices.stream().min(Integer::compareTo).orElse(-1);
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║  All Cab Prices (INR): " + prices);
            System.out.println("║  >>> LOWEST CAB PRICE: ₹" + lowest + " <<<");
            System.out.println("╚══════════════════════════════════════════╝");
        }
        ScreenshotUtil.capture(Hooks.getDriver(), "CabList_LowestPrice");
    }
}
