package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;
import pageObjects.HomePage;
import pageObjects.HotelPage;

import java.util.List;

/**
 * HotelBookingSteps - Step definitions for HotelBooking.feature
 *
 * Covers:
 *   - Navigate to Hotels
 *   - Open guest selector
 *   - Extract all adult numbers and store in a List
 *   - Display the list
 *   - Capture screenshot
 */
public class HotelBookingSteps {

    private HomePage  home;
    private HotelPage hotel;

    private List<Integer> adultNumbersList;

    // ── Background ──────────────────────────────────────────────────────────────

    @Given("User opens MakeMyTrip homepage for Hotel Booking")
    public void openHomePageForHotel() {
        home  = new HomePage(Hooks.getDriver(), Hooks.getWait());
        hotel = new HotelPage(Hooks.getDriver(), Hooks.getWait());
        Hooks.getDriver().get("https://www.makemytrip.com/");
        home.handlePopups();
        System.out.println("[HotelBookingSteps] MakeMyTrip home page loaded.");
    }

    // ── Navigate to Hotels ──────────────────────────────────────────────────────

    @When("User navigates to the Hotels section")
    public void navigateToHotels() {
        hotel.clickHotelsTab();
        System.out.println("[HotelBookingSteps] Hotels section opened.");
    }

    // ── Open Guest Dropdown ─────────────────────────────────────────────────────

    @And("User opens the guest selector dropdown")
    public void openGuestDropdown() throws InterruptedException {
        hotel.openGuestDropdown();
    }

    // ── Extract Adult Numbers ───────────────────────────────────────────────────

    @Then("All adult person numbers are extracted and stored in a list")
    public void extractAdultNumbers() throws InterruptedException {
        adultNumbersList = hotel.extractAllAdultNumbers();
        System.out.println("[HotelBookingSteps] Adult numbers list: " + adultNumbersList);
    }

    // ── Display List ────────────────────────────────────────────────────────────

    @And("The adult numbers list is printed and displayed")
    public void displayAdultNumbers() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   ADULT PERSON NUMBERS EXTRACTED:        ");
        if (adultNumbersList == null || adultNumbersList.isEmpty()) {
            System.out.println("║   (No adult numbers found)");
        } else {
            for (int i = 0; i < adultNumbersList.size(); i++) {
                System.out.printf("║   [%d] Adults: %d%n", i + 1, adultNumbersList.get(i));
            }
        }
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ── Screenshot ──────────────────────────────────────────────────────────────

    @And("A screenshot of the hotel guest selection is captured")
    public void captureHotelScreenshot() {
        String path = hotel.captureHotelScreenshot();
        System.out.println("[HotelBookingSteps] Screenshot saved: " + path);
    }
}
