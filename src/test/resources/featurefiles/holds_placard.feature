Feature: Placard Label Printing

  Background:
    Given logged in
    And selects "PHX" from facility modal
    And No Hold Cases detail exist
    And click on "Create Hold" button
    And "NEW HOLD" header is visible on page

  @All @UI @WEL-19620
  Scenario: Printing Placard on Tablet/PC (not on Mobile)
    Given fills in the details on the new case creation page
    And click on CREATE HOLD button
    When "Hold Successfully Created" header is visible on page
    And newly generated case number is visible
    Then "Print Placard" button is visible

  @All @UI @WEL-19620 @Mobile
  Scenario: No 'Print Placard' in Mobile
    Given fills in the details on the new case creation page
    And click on CREATE HOLD button
    And on mobile device
    When "Hold Successfully Created" header is visible on page
    And newly generated case number is visible
    Then "Print Placard" button is not visible

  @All @UI @WEL-22729
  Scenario: Only first 500 characters of description should display on placard
    Given fills in the details on the new case creation page
    And click on CREATE HOLD button
    When newly generated case number is visible
    And "Print Placard" button is visible
    And click on "Print Placard" button
    Then PDF file is generated for placard with holds detail
    And Placard details should match the details in the application
    And Only first 500 characters of description should display on placard

  @All @UI @WEL-22730
  Scenario: Hold Creation Date should display as MM/DD on placard
    Given fills in the details on the new case creation page
    And click on CREATE HOLD button
    When newly generated case number is visible
    And "Print Placard" button is visible
    And click on "Print Placard" button
    Then PDF file is generated for placard with holds detail
    And Placard details should match the details in the application
    Then holds creation date display as 'MM/DD' on placard

  @All @UI @WEL-19709 @WEL-19710
  Scenario Outline: User should be able to track the "<fieldName>" on Placard from "<processName>"
    Given fills in the details on the new case creation page
    And click on CREATE HOLD button
    When newly generated case number is visible
    And "Print Placard" button is visible
    And click on "Print Placard" button
    Then PDF file is generated for placard with holds detail
    And Placard details should match the details in the application
    Then "<fieldName>" added during "<processName>" should display on Placard
    Examples:
      | fieldName               | processName   |
      | BOL/DSM Number          | Hold Creation |
      | Total Number of Pallets | Hold Creation |

  @All @UI @Smoke @WEL-19545 @WEL-22730
  Scenario: placard Should Display the exact same details as on the Hold Detail Page
    Given fills in the details on the new case creation page
    And click on CREATE HOLD button
    And newly generated case number is visible
    When "Print Placard" button is visible
    And click on "Open hold details" button
    And "Placard" button is visible
    And click on "Placard" button
    Then PDF file is generated for placard with holds detail
    And Placard details should match the details in the application