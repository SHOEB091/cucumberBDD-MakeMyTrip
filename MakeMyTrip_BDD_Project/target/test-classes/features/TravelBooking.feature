Feature: MakeMyTrip End-to-End Travel Validation

  Scenario: Execute complete workflow for Cabs, Gift Cards, and Hotels
    Given User is on MakeMyTrip Home Page
    
    When User navigates to Cabs page
    And User enters "Delhi" as From location and "Manali,Himachal Pradesh" as To location
    And User selects departure date "June" "2026"
    And User selects pickup time "10:00 AM" and clicks Apply
    And User clicks Search
    Then User should see available cab options
    And The lowest cab price should be displayed
    
    And User returns to Home Page
    # --- GIFT CARD MODULE ---
    When User selects Gift Cards and switches to the new tab
    And User selects the Wedding Gift Card
    And User enters sender details "Sathvik", "8709390380", "admin1234"
    Then User clicks Buy Now and captures a screenshot
    
    # --- HOTEL MODULE ---
    And User checks maximum adult capacity in Hotels
    Then User takes a screenshot