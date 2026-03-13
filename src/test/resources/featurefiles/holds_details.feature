Feature: Hold Details page

  Background:
    Given logged in
    And selects "BWI" from facility modal
    And Hold Cases exist

  @All @UI @WEL-13103 @WEL-19544
  Scenario: services displayed on the Hold Detail page
    Given click on any holdCase display on "dashboard"
    When page "Holds Detail" is loaded for clicked holdCase
    Then "CASE #" header is visible with clicked holdCase detail on page
    And "services" displayed on holds detail page
      | MAX     |
      | EXP     |
      | GND     |
      | INTL    |
      | PALLETS |

  @All @UI @WEL-13103 @WEL-19544
  Scenario: fields displayed on the Hold Detail page
    Given click on any holdCase display on "dashboard"
    When page "Holds Detail" is loaded for clicked holdCase
    Then "CASE #" header is visible with clicked holdCase detail on page
    And "fields" displayed on holds detail page
      | Case Number            |
      | BOL/DSM Number         |
      | Hold Type              |
      | Soldto Customer Name   |
      | Soldto Customer Number |
      | Pickup Customer Name   |
      | Pickup Customer Number |
      | Created By             |
      | Time On Hold           |
      | Description            |

  @All @UI @WEL-13103 @WEL-19544
  Scenario: buttons displayed on the Hold Detail page
    Given click on any holdCase display on "dashboard"
    When page "Holds Detail" is loaded for clicked holdCase
    Then "CASE #" header is visible with clicked holdCase detail on page
    And buttons are visible on holds detail page
      | Edit      |
      | Details   |
      | Placard   |
      | DASHBOARD |
      | Send      |

  @All @UI @Smoke @WEL-13103 @WEL-19544
  Scenario: hold case info (Status, Service(s), Case Number, BOL/DSM Number, Hold Type, Soldto Customer Name & Number, Pickup Customer Name & Number, Created By, Time on Hold, Description) displayed on the Hold Detail page
    Given click on any holdCase display on "dashboard"
    When page "Holds Detail" is loaded for clicked holdCase
    Then "CASE #" header is visible with clicked holdCase detail on page
    And status should display on detail page in respected color
    And services values displayed on detail page should matched with clicked holdCase
    And fields values displayed on detail page should matched with clicked holdCase

  @All @UI @Mobile @Smoke @WEL-13103 @WEL-19544
  Scenario: hold case info (Status, Service(s), Case Number, BOL/DSM Number, Hold Type, Soldto Customer Name & Number, Pickup Customer Name & Number, Created By, Time on Hold, Description) displayed on the Hold Detail page
    Given click on any holdCase display on "dashboard"
    And  on mobile device
    When page "Holds Detail" is loaded for clicked holdCase
    Then "CASE #" header is visible with clicked holdCase detail on page
    And status should display on detail page in respected color
    And services values displayed on detail page should matched with clicked holdCase
    And fields values displayed on mobile detail page should matched with clicked holdCase

  @All @UI @WEL-13106
  Scenario: User must be able to select back button on the Hold Detail page and go back to Hold Management page
    Given click on any holdCase display on "dashboard"
    And page "Holds Detail" is loaded for clicked holdCase
    When BACK button display
    And click on BACK button
    Then page "Holds Dashboard" is loaded

  @All @UI @WEL-13107
  Scenario Outline: User must be able to scroll through all the hold event details on the Hold detail page
    Given click on holdCase "<caseNumber>" display on "dashboard"
    When page "Holds Detail" is loaded for clicked holdCase
    Then hold events are displayed
    And it is possible to scroll to see all events
    Examples:
      | caseNumber |
      | 06483317   |

  @All @UI @WEL-19542
  Scenario Outline: User seeing the bottom of the case history when entering the page
    Given click on holdCase "<caseNumber>" display on "dashboard"
    When page "Holds Detail" is loaded for clicked holdCase
    Then hold events are displayed
    And most recent events "<lastEvent>" displayed on the bottom
    Examples:
      | caseNumber | lastEvent                  |
      | 06483317   | Thanks again for chatting. |