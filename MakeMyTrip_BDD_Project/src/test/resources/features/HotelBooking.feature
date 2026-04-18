@HotelBooking
Feature: Hotel Booking - Extract Adult Guest Numbers

  Background:
    Given User opens MakeMyTrip homepage for Hotel Booking

  @AdultExtraction @GuestList
  Scenario: Extract all adult person numbers from Hotel booking page and store in a list
    When User navigates to the Hotels section
    And User opens the guest selector dropdown
    Then All adult person numbers are extracted and stored in a list
    And The adult numbers list is printed and displayed
    And A screenshot of the hotel guest selection is captured
