Feature: MakeMyTrip End-to-End Travel Validation

  # ONE browser, multiple tabs:
  #   Tab 1 (main)  → Cab booking: Delhi → Manali, June 10, SUV, lowest price
  #   Tab 2 (new)   → Gift Card:   Wedding card, invalid email, capture error
  #   Tab 1 (main)  → Hotels:      Extract adult numbers, display list

  Scenario: Execute complete workflow for Cabs, Gift Cards, and Hotels

    # ── TAB 1 : CAB BOOKING ─────────────────────────────────────────────────
    Given User is on MakeMyTrip Home Page

    When User navigates to Cabs page
    And User enters "Delhi" as From location and "Manali,Himachal Pradesh" as To location
    And User selects departure date "June" "2026"
    And User selects pickup time "10:30 AM" and clicks Apply
    And User clicks Search
    Then User should see available cab options
    And The lowest cab price should be displayed

    # ── BACK TO HOME ────────────────────────────────────────────────────────
    And User returns to Home Page

    # ── TAB 2 : GIFT CARD (opens new tab automatically) ─────────────────────
    When User selects Gift Cards and switches to the new tab
    And User selects the Wedding Gift Card
    And User enters sender details "Sathvik", "8709390380", "invalid-email@@test"
    Then User clicks Buy Now and captures a screenshot

    # ── TAB 1 : HOTEL BOOKING ───────────────────────────────────────────────
    And User checks maximum adult capacity in Hotels
    Then User takes a screenshot
