package stepDefinitions;

import io.cucumber.java.en.*;
import hooks.Hooks;
import pageObjects.*;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * TravelSteps – Step definitions for TravelBooking.feature.
 *
 * One browser, three flows:
 *   1. Cabs  – Tab 1
 *   2. Gift  – Tab 2 (MakeMyTrip opens it automatically)
 *   3. Hotels– Tab 1 (switched back to homeId)
 */
public class TravelSteps {

    private HomePage     home;
    private CabPage      cabs;
    private GiftCardPage gift;
    private HotelPage    hotel;

    /** Lazy-init so page objects are created only AFTER @Before has set Hooks.driver. */
    private void initPages() {
        if (home == null) {
            home  = new HomePage(Hooks.driver, Hooks.wait);
            cabs  = new CabPage(Hooks.driver, Hooks.wait);
            gift  = new GiftCardPage(Hooks.driver, Hooks.wait);
            hotel = new HotelPage(Hooks.driver, Hooks.wait);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  COMMON
    // ═══════════════════════════════════════════════════════════════════════

    @Given("User is on MakeMyTrip Home Page")
    public void user_is_on_home() {
        initPages();
        Hooks.driver.get("https://www.makemytrip.com/");
        home.handlePopups();
        System.out.println("[TravelSteps] Home page loaded.");
    }

    @Then("User takes a screenshot")
    public void take_final_screenshot() {
        takeScreenshot("Final_Output");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  TAB 1 – CAB BOOKING
    // ═══════════════════════════════════════════════════════════════════════

    @When("User navigates to Cabs page")
    public void user_navigates_to_cabs_page() {
        initPages();
        cabs.clickCabsTab();
    }

    @When("User enters {string} as From location and {string} as To location")
    public void user_enters_locations(String from, String to) throws InterruptedException {
        initPages();
        cabs.enterCities(from, to);
    }

    @When("User selects departure date {string} {string}")
    public void user_selects_departure_date(String month, String year) throws InterruptedException {
        initPages();
        cabs.selectDate(month, year);
    }

    /**
     * Parses "10:30 AM" → hour=10, minute=30, and calls CabPage.selectPickupTimeAndApply().
     */
    @When("User selects pickup time {string} and clicks Apply")
    public void user_selects_pickup_time(String time) {
        initPages();
        try {
            // Expected format: "10:30 AM"
            String[] colonSplit = time.split(":");
            String hour        = colonSplit[0].trim();                      // "10"
            String[] minParts  = colonSplit[1].trim().split("\\s+");
            String minute      = minParts[0].trim();                        // "30"
            System.out.printf("[TravelSteps] Time to select: %s hr, %s min%n", hour, minute);
            cabs.selectPickupTimeAndApply(hour, minute);
        } catch (Exception e) {
            System.out.println("[TravelSteps] Time parse/select failed, attempting APPLY only: " + e.getMessage());
            try {
                Hooks.driver.findElement(By.xpath(
                    "//div[contains(@class,'applyBtn')]//span | //span[text()='APPLY']"
                )).click();
            } catch (Exception ignored) {}
        }
    }

    @When("User clicks Search")
    public void user_clicks_search() {
        initPages();
        cabs.clickSearch();
    }

    @Then("User should see available cab options")
    public void user_should_see_available_cab_options() throws InterruptedException {
        initPages();
        // Wait for the actual results page to load (not just a fixed sleep)
        boolean loaded = cabs.waitForResultsPage();
        if (!loaded) {
            System.out.println("[TravelSteps] WARNING: Results page may not have loaded correctly.");
            System.out.println("[TravelSteps] Current URL: " + Hooks.driver.getCurrentUrl());
        }
        takeScreenshot("CabList_AfterSearch");
        cabs.selectSUVFilter();
        takeScreenshot("CabList_SUV");
    }

    @Then("The lowest cab price should be displayed")
    public void the_lowest_cab_price_should_be_displayed() {
        initPages();
        List<Integer> prices = cabs.getAllPrices();
        System.out.println("╔══════════════════════════════════════════╗");
        if (prices.isEmpty()) {
            System.out.println("║  No prices extracted (check SUV filter).");
        } else {
            int lowest = prices.stream().min(Integer::compareTo).orElse(-1);
            System.out.println("║  All prices (INR): " + prices);
            System.out.println("║  >>> LOWEST PRICE: ₹" + lowest + " <<<");
        }
        System.out.println("╚══════════════════════════════════════════╝");
        takeScreenshot("CabList_LowestPrice");
    }

    @And("User returns to Home Page")
    public void user_returns_to_home_page() throws InterruptedException {
        Hooks.driver.get("https://www.makemytrip.com/");
        try {
            Hooks.wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("landingContainer")));
        } catch (Exception ignored) {}
        // Re-init pages because driver context may have changed
        home  = new HomePage(Hooks.driver, Hooks.wait);
        cabs  = new CabPage(Hooks.driver, Hooks.wait);
        gift  = new GiftCardPage(Hooks.driver, Hooks.wait);
        hotel = new HotelPage(Hooks.driver, Hooks.wait);
        home.handlePopups();
        System.out.println("[TravelSteps] Returned to Home Page.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  TAB 2 – GIFT CARD
    // ═══════════════════════════════════════════════════════════════════════

    @When("User selects Gift Cards and switches to the new tab")
    public void user_selects_gift_cards() throws InterruptedException {
        initPages();
        gift.clickGiftCardsMenu();
        Thread.sleep(1500);
        gift.switchToNewTab();
        // Re-init page objects with the new tab's context
        home  = new HomePage(Hooks.driver, Hooks.wait);
        gift  = new GiftCardPage(Hooks.driver, Hooks.wait);
    }

    @And("User selects the Wedding Gift Card")
    public void user_selects_wedding_card() throws InterruptedException {
        initPages();
        gift.clickWeddingCard();
    }

    @And("User enters sender details {string}, {string}, {string}")
    public void user_enters_sender_details(String name, String mobile, String email) throws InterruptedException {
        initPages();
        gift.scrollToForm();
        gift.enterName(name);
        gift.enterMobile(mobile);
        gift.enterEmail(email);
        System.out.println("[TravelSteps] Sender details entered. Email (invalid): " + email);
    }

    @Then("User clicks Buy Now and captures a screenshot")
    public void user_clicks_buy_now_and_captures_screenshot() throws InterruptedException {
        initPages();
        gift.clickBuyNow();
        Thread.sleep(2500);
        // Capture the error/result screen
        String error = gift.captureErrorMessage();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  Error/Result message: " + error);
        System.out.println("╚══════════════════════════════════════════╝");
        gift.takeErrorScreenshot();
        takeScreenshot("GiftCard_BuyNow_Result");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  TAB 1 – HOTELS (switch back to main tab)
    // ═══════════════════════════════════════════════════════════════════════

    @And("User checks maximum adult capacity in Hotels")
    public void hotel_logic() throws InterruptedException {
        // Switch back to the main/home tab
        if (Hooks.homeId != null) {
            Hooks.driver.switchTo().window(Hooks.homeId);
            System.out.println("[TravelSteps] Switched back to main tab: " + Hooks.homeId);
        }
        // Re-init page objects for the main tab context
        home  = new HomePage(Hooks.driver, Hooks.wait);
        hotel = new HotelPage(Hooks.driver, Hooks.wait);

        home.goToMenu("Hotels");
        Thread.sleep(1500);
        hotel.openGuestDropdown();
        List<Integer> adultNums = hotel.extractAllAdultNumbers();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  Adult numbers extracted: " + adultNums);
        System.out.println("╚══════════════════════════════════════════╝");
        takeScreenshot("Hotels_GuestList");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HELPER
    // ═══════════════════════════════════════════════════════════════════════

    private void takeScreenshot(String fileName) {
        try {
            File src  = ((TakesScreenshot) Hooks.driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(System.getProperty("user.dir") + "/target/Screenshots/" + fileName + ".png");
            if (dest.getParentFile() != null) dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            System.out.println("[TravelSteps] Screenshot: " + dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
