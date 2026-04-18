@CabBooking
Feature: One-Way Outstation Cab Booking - Delhi to Manali

  Background:
    Given User opens MakeMyTrip homepage for Cab Booking

  @SUV @LowestPrice
  Scenario Outline: Book one-way outstation cab and display lowest SUV price
    When User navigates to the Cabs section
    And User enters from city "<fromCity>" and to city "<toCity>"
    And User selects travel date month "<month>" and year "<year>"
    And User clicks Search Cabs button
    Then User should see list of available cabs
    And User applies SUV filter
    And The lowest SUV cab price is captured and displayed

    Examples:
      | fromCity | toCity              | month | year |
      | Delhi    | Manali              | June  | 2026 |
