package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.*;
import pageObjects.GiftCardPage;
import pageObjects.HomePage;

/**
 * GiftCardSteps - Step definitions for GiftCard.feature
 *
 * Covers:
 *   - Navigate to Gift Cards
 *   - Switch to new tab
 *   - Select Wedding card
 *   - Fill form with invalid email
 *   - Click Buy Now
 *   - Capture and display error message + screenshot
 */
public class GiftCardSteps {

    private HomePage     home;
    private GiftCardPage giftCard;

    // ── Background ──────────────────────────────────────────────────────────────

    @Given("User opens MakeMyTrip homepage for Gift Card")
    public void openHomePageForGiftCard() {
        home     = new HomePage(Hooks.getDriver(), Hooks.getWait());
        giftCard = new GiftCardPage(Hooks.getDriver(), Hooks.getWait());
        Hooks.getDriver().get("https://www.makemytrip.com/");
        home.handlePopups();
        System.out.println("[GiftCardSteps] MakeMyTrip home page loaded.");
    }

    // ── Navigation ──────────────────────────────────────────────────────────────

    @When("User scrolls down and clicks on Gift Cards menu")
    public void clickGiftCardsMenu() throws InterruptedException {
        giftCard.clickGiftCardsMenu();
    }

    @And("User switches to the Gift Cards tab")
    public void switchToGiftCardTab() {
        giftCard.switchToNewTab();
    }

    // ── Card Selection ──────────────────────────────────────────────────────────

    @And("User selects the Wedding Gift Card option")
    public void selectWeddingCard() throws InterruptedException {
        giftCard.clickWeddingCard();
    }

    // ── Form Filling ────────────────────────────────────────────────────────────

    @And("User scrolls to the sender details form")
    public void scrollToForm() throws InterruptedException {
        giftCard.scrollToForm();
    }

    @And("User enters sender name {string}")
    public void enterName(String name) {
        giftCard.enterName(name);
        System.out.println("[GiftCardSteps] Sender name entered: " + name);
    }

    @And("User enters sender mobile {string}")
    public void enterMobile(String mobile) {
        giftCard.enterMobile(mobile);
        System.out.println("[GiftCardSteps] Sender mobile entered: " + mobile);
    }

    @And("User enters invalid sender email {string}")
    public void enterInvalidEmail(String email) {
        giftCard.enterEmail(email);
        System.out.println("[GiftCardSteps] Invalid email entered: " + email);
    }

    // ── Buy Now + Error Capture ──────────────────────────────────────────────────

    @And("User clicks the Buy Now button")
    public void clickBuyNow() throws InterruptedException {
        giftCard.clickBuyNow();
        Thread.sleep(2000);
    }

    @Then("An error message is displayed")
    public void verifyErrorMessage() {
        String msg = giftCard.captureErrorMessage();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  >>> ERROR MESSAGE CAPTURED <<<");
        System.out.println("║  " + msg);
        System.out.println("╚══════════════════════════════════════════╝");
    }

    @And("A screenshot of the error is captured and saved")
    public void captureErrorScreenshot() {
        String path = giftCard.takeErrorScreenshot();
        System.out.println("[GiftCardSteps] Screenshot path: " + path);
    }
}
