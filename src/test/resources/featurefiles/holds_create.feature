Feature: Holds Creation
  Hold Case can be created from the application.

  Background:
    Given logged in
    And selects "PHX" from facility modal
    And No Hold Cases detail exist
    And click on "Create Hold" button
    And "NEW HOLD" header is visible on page

  @All @UI @Mobile @WEL-13300 @WEL-19711
  Scenario: Pick up is mandatory when creating a new hold case
    When "Proceed" button is visible
    And click on "Proceed" button
    Then message "Required" is displayed

  @All @UI @WEL-19582 @WEL-13302 @WEL-13301
  Scenario: User can select pickup by clicking on the dropdown icon.
    When click on Pickup Customer Dropdown
    And selects any Pickup Customer from List
    Then selected Pickup is displayed on Screen

  @All @UI @WEL-13301 @WEL-13302
  Scenario Outline: System should display the pickup list based on the "<searchText>" entered in the search textbox
    When click on Pickup Customer Dropdown
    And enters "<searchText>" in Pickup Customer textbox
    Then all pickup details containing "<searchText>" is displayed
    Examples:
      | searchText |
      | 3390       |
      | 3veni      |
      | IN 4623    |
      | 1390       |

  @All @UI @WEL-19612
  Scenario: Full list of Hold Type is displayed when "Show More" text is clicked
    Given selects a Pickup by clicking the dropdown icon
    When click on "showMore" icon
    Then Full List of "Hold Type" is displayed
      | NO ACTIVE AGREEMENT |
      | NO SLOT FOUND       |
      | MIXED SERVICES      |
      | NO DATA             |
      | BARCODE QUALITY     |
      | CREDIT HOLD         |
      | MISSING IAC         |
      | TSA NOT APPROVED    |
      | MISSING DSM/BOL     |
      | INCORRECT ACCOUNT   |
      | DG HOLD             |
      | LATE ARRIVAL        |
      | SORT CODE ISSUE     |
      | OVERSIZED           |
      | DIMMING             |
      | OTHER               |

  @All @UI @WEL-19612
  Scenario: User selects a Hold Type from the Hold Type Radio buttons
    Given selects a Pickup by clicking the dropdown icon
    And click on "showMore" icon
    When selects any Hold Type
    Then selected HoldType is displayed on Screen
    And CREATE HOLD button is displayed

  @All @UI @Smoke @WEL-19613
  Scenario: User is able to see data on the subsequent screen from previous screens
    When fills in the details on the new case creation page
    Then selected Pickup is displayed on Screen
    And selected HoldType is displayed on Screen
    And added values is displayed in all fields

  @All @UI @Smoke @WEL-13320 @WEL-13317
  Scenario: User should redirected to confirmation page after creating a case without an attachment
    Given fills in the details on the new case creation page
    When click on CREATE HOLD button
    Then "Hold Successfully Created" header is visible on page
    And "Case Number / SalesForce Case Number" is visible
    And newly generated case number is visible
    And "Print Placard" button is visible
    And "Open hold details" button is visible
    And "Return to Dashboard" button is visible

  @All @UI @Smoke @Mobile @WEL-13320 @WEL-13317
  Scenario Outline: User should redirected to confirmation page after creating a case with attachment(s)
    Given fills in the details on the new case creation page
    And upload the "<attachment>" file(s)
    When click on CREATE HOLD button
    And waits until the files are uploaded
    Then "Hold Successfully Created" header is visible on page
    And "Case Number / SalesForce Case Number" is visible
    And newly generated case number is visible
    Examples:
      | attachment                                 |
      | JPG_file.jpg, JPEG_file.jpeg, PNG_file.png |

  @All @UI @WEL-19706
  Scenario: User can traverse back in the process from 'Select Hold Type'
    Given selects a Pickup by clicking the dropdown icon
    When click on "Back" button
    Then user is on "Select Pickup Customer" selection step

  @All @UI @WEL-19706
  Scenario: User can traverse forward in the process from 'Select Pickup Customer'
    Given selects a Pickup by clicking the dropdown icon
    And click on "Back" button
    When click on "Proceed" button
    Then user is on "Select Hold Type" selection step
    And Short List of "Hold Type" is displayed
      | NO ACTIVE AGREEMENT |
      | NO SLOT FOUND       |
      | MIXED SERVICES      |
      | NO DATA             |
      | BARCODE QUALITY     |

  @All @UI @WEL-19706 @WEL-19612
  Scenario: User can not traverse forward from 'Select Hold Type' without selecting any 'Hold Type'
    Given selects a Pickup by clicking the dropdown icon
    And selected Pickup is displayed on Screen
    When click on "Proceed" button
    Then message "Required" is displayed

  @All @UI @WEL-19706 @WEL-19612
  Scenario: User can traverse forward from 'Select Hold Type' after selecting 'Hold Type'
    Given selects a Pickup by clicking the dropdown icon
    When selects any Hold Type
    Then user is on "Enter Hold Details" selection step
    And  Full List of "Enter Hold Details" is displayed
      | BOL/DSM Number          |
      | Total Number of Pallets |
      | MAX PC CNT              |
      | EXP PC CNT              |
      | GND PC CNT              |
      | INTL PC CNT             |
      | Description             |
      | Attachments             |

  @All @UI @WEL-19706
  Scenario: User can traverse back from 'Enter Hold Details'
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And user is on "Enter Hold Details" selection step
    When click on "Back" button
    Then user is on "Select Hold Type" selection step
    And "Hold Type" is already selected

  @All @UI @WEL-19612
  Scenario: User can traverse forward from 'Select Hold Type'
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And click on "Back" button
    And user is on "Select Hold Type" selection step
    When click on "Proceed" button
    Then user is on "Enter Hold Details" selection step

  @All @UI @Smoke @WEL-19583
  Scenario: Determining which step the user is on when creating Holds (Graphical Indicators)
    Given user is on "Select Pickup Customer" selection step
    When selects a Pickup by clicking the dropdown icon
    And user is on "Select Hold Type" selection step
    And selects any Hold Type
    Then user is on "Enter Hold Details" selection step

  @All @UI @WEL-13308
  Scenario Outline: "BROWSE FILES" button is displayed on the 'New Hold' page to attach files (Desktop)
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When user is on "Enter Hold Details" selection step
    And "Browse files" button is visible
    Then able to add "<attachment>" file(s)
    Examples:
      | attachment                                 |
      | DOC_file.doc, JPEG_file.jpeg, PDF_file.pdf |

  @All @UI @WEL-25243
  Scenario Outline: Ensure at least one positive service value is present during hold creation with "<fields>"
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And enters the value in different "<fields>"
    When click on CREATE HOLD button
    Then message "At least one of the following fields must have a value greater than 0: MAX, EXP, GND, INTL" is displayed
    Examples:
      | fields                                               |
      | BOL/DSM Number                                       |
      | Total Number of Pallets                              |
      | Description                                          |
      | Description, BOL/DSM Number                          |
      | Description, BOL/DSM Number, Total Number of Pallets |

  @All @UI @WEL-19756
  Scenario Outline: Display a validation message when the user exceeds the maximum allowed value for a field "<fields>"
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And enter the maximum value in different "<fields>"
    When click on CREATE HOLD button
    Then message "<validation msg>" is displayed
    Examples:
      | fields                  | validation msg                                  |
      | Total Number of Pallets | Number must be less than or equal to 9999999999 |
      | MAX PC CNT              | Number must be less than or equal to 9999999    |
      | EXP PC CNT              | Number must be less than or equal to 9999999999 |
      | GND PC CNT              | Number must be less than or equal to 9999999999 |
      | INTL PC CNT             | Number must be less than or equal to 9999999999 |

  @All @UI @WEL-13313 @WEL-13130
  Scenario Outline: Initial uploads (Comment & Images) should remain on the screen until Holds are created
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When enters the value in different "<field>"
    And upload the "<attachment>" file(s)
    Then added value is displayed against the respective "<field>"
    And thumbnails of "<attachment>" file(s) is displayed on Screen
    Examples:
      | field       | attachment                                 |
      | Description | DOC_file.doc, JPEG_file.jpeg, PDF_file.pdf |

  @All @UI @WEL-13307
  Scenario Outline: User should able to add comment during New hold creation
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When enters the value in different "<field>"
    Then added value is displayed against the respective "<field>"
    Examples:
      | field       |
      | Description |

  @All @UI @WEL-19710, @WEL-19709, @WEL-19708 @WEL-25243
  Scenario Outline: User should be able to track the Holds created with '<fields>' value on both the Dashboard and Salesforce Application
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And enters the value in different "<fields>"
    When click on CREATE HOLD button
    And newly generated case number is visible
    Then added value is displayed on "Dashboard" page against the "<fields>"
    And added value is displayed on "Salesforce" application against "<fields>"
    Examples:
      | fields                                          |
      | BOL/DSM Number, MAX PC CNT                      |
      | Total Number of Pallets, EXP PC CNT             |
      | MAX PC CNT, EXP PC CNT, GND PC CNT, INTL PC CNT |

  @All @UI @Smoke @WEL-13319
  Scenario Outline: case Created from Holds Application should display in Salesforce
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And enters the value in different "<fields>"
    When click on CREATE HOLD button
    And newly generated case number is visible
    Then added value is displayed on "Salesforce" application against "<fields>"
    Examples:
      | fields                                                                                                |
      | BOL/DSM Number, Total Number of Pallets, MAX PC CNT, EXP PC CNT, GND PC CNT, INTL PC CNT, Description |

  @All @UI @WEL-19756
  Scenario Outline: Store the sum of Service values (MAX, EXP, and GND) in a separate (Claims) field in Salesforce
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And enters the value in different "<fields>"
    When click on CREATE HOLD button
    And newly generated case number is visible
    Then item count values for each service "<fields>" level is stored separately in SF
    Examples:
      | fields                                                                                                |
      | BOL/DSM Number, Total Number of Pallets, MAX PC CNT, EXP PC CNT, GND PC CNT, INTL PC CNT, Description |

  @All @UI @Smoke @WEL-19611
  Scenario: Canceling the "New Hold" page when no data is selected on the "New Hold" page
    Given "Cancel" button is visible
    When click on "Cancel" button
    Then page "Holds Dashboard" is loaded

  @All @UI @WEL-19611
  Scenario Outline: Cancel the "New Hold" page when data is selected and the "<button>" button is clicked
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When click on "Cancel" button
    And "Unsaved Changes" header is visible on page
    And "You have unsaved changes. Leave without saving?" is visible
    And "<button>" button is visible
    And clicks on "<button>" button
    Then stays on or is redirected to "<redirected page>" page
    Examples:
      | button | redirected page |
      | Leave  | Holds Dashboard |
      | Stay   | Holds Create    |

  @All @UI @WEL-19615
  Scenario Outline: User should be able to create Holds with Multiple Service(s) (<fields>) but only 1 Hold Type allowed
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    And enters the value in different "<fields>"
    When added value is displayed against the respective "<fields>"
    And click on CREATE HOLD button
    Then newly generated case number is visible
    And added value is displayed on "Dashboard" page against the "<fields>"
    And added value is displayed on "Salesforce" application against "<fields>"
    Examples:
      | fields                                          |
      | MAX PC CNT                                      |
      | MAX PC CNT, EXP PC CNT                          |
      | MAX PC CNT, GND PC CNT                          |
      | MAX PC CNT, INTL PC CNT                         |
      | MAX PC CNT, EXP PC CNT, GND PC CNT              |
      | MAX PC CNT, EXP PC CNT, INTL PC CNT             |
      | EXP PC CNT                                      |
      | EXP PC CNT, GND PC CNT                          |
      | EXP PC CNT, INTL PC CNT                         |
      | EXP PC CNT, GND PC CNT, INTL PC CNT             |
      | GND PC CNT                                      |
      | GND PC CNT, INTL PC CNT                         |
      | INTL PC CNT                                     |
      | MAX PC CNT, EXP PC CNT, GND PC CNT, INTL PC CNT |

  @All @UI @@WEL-19711
  Scenario: User should be able to add the details by tabbing on Hold Creation Form
    Given using the Tab key only add the details on the new case creation page
    When click on CREATE HOLD button
    Then "Hold Successfully Created" header is visible on page
    And newly generated case number is visible