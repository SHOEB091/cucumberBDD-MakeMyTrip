Feature: AutomationExercise E2E Test Suite

  # Website : https://automationexercise.com  (automation-friendly, no bot detection)
  # Three flows in ONE browser session:
  #   Flow 1 – Product Search  → apply category filter → extract & display lowest price
  #   Flow 2 – Login form      → wrong password        → capture error message + screenshot
  #   Flow 3 – Category page   → extract all product names & prices into a List

  Scenario: Execute complete workflow for Product Search, Login Error, and Category Extraction

    # ── FLOW 1 : PRODUCT SEARCH ──────────────────────────────────────────────
    Given User opens AutomationExercise home page

    When User navigates to Products page
    And User searches for product "Blue Top"
    Then User should see search results
    And User applies "Women" category filter
    And The lowest product price should be displayed

    # ── FLOW 2 : LOGIN ERROR  ─────────────────────────────────────────────────
    When User navigates to the Login page
    And User enters login email "wronguser@test.com" and password "wrongpass123"
    And User clicks the Login button
    Then The login error message should be captured and screenshot taken

    # ── FLOW 3 : CATEGORY PRODUCT EXTRACTION ─────────────────────────────────
    When User navigates to Women Dress category
    Then All product names and prices should be extracted into a List
    And User takes a final screenshot
