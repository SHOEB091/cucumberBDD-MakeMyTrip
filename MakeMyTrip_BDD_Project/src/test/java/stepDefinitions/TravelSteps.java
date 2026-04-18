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

/**
 * TravelSteps - Combined step definitions for the legacy TravelBooking.feature.
 *
 * All three scenarios (Cabs, Gift Cards, Hotels) are wired here for
 * the single-feature combined run. For parallel execution per-feature,
 * see CabBookingSteps, GiftCardSteps, and HotelBookingSteps instead.
 */
public class TravelSteps {

    private HomePage     home;
    private CabPage      cabs;
    private GiftCardPage gift;
    private HotelPage    hotel;

    private void initPages() {
        if (home == null) {
            home  = new HomePage(Hooks.getDriver(), Hooks.getWait());
            cabs  = new CabPage(Hooks.getDriver(), Hooks.getWait());
            gift  = new GiftCardPage(Hooks.getDriver(), Hooks.getWait());
            hotel = new HotelPage(Hooks.getDriver(), Hooks.getWait());
        }
    }

    // ── Background / Common ─────────────────────────────────────────────────

    @Given("User is on MakeMyTrip Home Page")
    public void user_is_on_home() {
        initPages();
        Hooks.getDriver().get("https://www.makemytrip.com/");
        home.handlePopups();
    }

    @Then("User takes a screenshot")
    public void take_final_screenshot() {
        takeScreenshot("Final_Output");
    }

    // ── Cab Scenario Steps ──────────────────────────────────────────────────

    @When("User navigates to Cabs page")
    public void user_navigates_to_cabs_page() {
        initPages();
        cabs.clickCabsTab();
        cabs.handlePopups();
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

    @When("User selects pickup time {string} and clicks Apply")
    public void user_selects_pickup_time(String time) {
        System.out.println("[TravelSteps] Pickup time selection step (time=" + time + ") - handled by date selection.");
    }

    @When("User clicks Search")
    public void user_clicks_search() {
        initPages();
        cabs.clickSearch();
    }

    @Then("User should see available cab options")
    public void user_should_see_available_cab_options() throws InterruptedException {
        initPages();
        cabs.selectSUVFilter();
        Thread.sleep(2000);
    }

    @Then("The lowest cab price should be displayed")
    public void the_lowest_cab_price_should_be_displayed() {
        initPages();
        int price = cabs.getLowestPrice();
        System.out.println("=== Lowest SUV Cab Price: ₹" + price + " ===");
    }

    @And("User returns to Home Page")
    public void user_returns_to_home_page() throws InterruptedException {
        Hooks.getDriver().get("https://www.makemytrip.com/");
        Hooks.getWait().until(ExpectedConditions.visibilityOfElementLocated(By.className("landingContainer")));
        initPages();
        home.handlePopups();
    }

    // ── Gift Card Scenario Steps ────────────────────────────────────────────

    @When("User selects Gift Cards and switches to the new tab")
    public void user_selects_gift_cards() throws InterruptedException {
        initPages();
        gift.clickGiftCardsMenu();
        gift.switchToNewTab();
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
    }

    @Then("User clicks Buy Now and captures a screenshot")
    public void user_clicks_buy_now_and_captures_screenshot() throws InterruptedException {
        initPages();
        gift.clickBuyNow();
        Thread.sleep(2000);
        gift.takeErrorScreenshot();
    }

    // ── Hotel Scenario Steps ────────────────────────────────────────────────

    @And("User checks maximum adult capacity in Hotels")
    public void hotel_logic() throws InterruptedException {
        if (Hooks.getHomeId() != null) {
            Hooks.getDriver().switchTo().window(Hooks.getHomeId());
        }
        initPages();
        home.goToMenu("Hotels");
        hotel.openGuestDropdown();
        java.util.List<Integer> adultNums = hotel.extractAllAdultNumbers();
        System.out.println("=== Adult Numbers from Hotels page: " + adultNums + " ===");
    }

    // ── Helper ──────────────────────────────────────────────────────────────

    public void takeScreenshot(String fileName) {
        try {
            File src  = ((TakesScreenshot) Hooks.getDriver()).getScreenshotAs(OutputType.FILE);
            File dest = new File(System.getProperty("user.dir") + "/target/" + fileName + ".png");
            if (dest.getParentFile() != null) dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            System.out.println("[TravelSteps] Screenshot saved: " + dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
