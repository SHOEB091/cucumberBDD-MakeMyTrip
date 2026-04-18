@GiftCard
Feature: Gift Card - Group Gifting with Invalid Email Validation

  Background:
    Given User opens MakeMyTrip homepage for Gift Card

  @InvalidEmail @ErrorCapture
  Scenario: Find Group Gifting, fill card details with invalid email and capture error
    When User scrolls down and clicks on Gift Cards menu
    And User switches to the Gift Cards tab
    And User selects the Wedding Gift Card option
    And User scrolls to the sender details form
    And User enters sender name "TestUser"
    And User enters sender mobile "9876543210"
    And User enters invalid sender email "invalid-email@@test"
    And User clicks the Buy Now button
    Then An error message is displayed
    And A screenshot of the error is captured and saved
