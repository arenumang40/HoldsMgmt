Feature: Display Hold Case details on Hold Details page

  Background:
    Given logged in
    And "ATL" is the current facility

  # Below scenario is not required provided that the next scenario validates the fields are displayed, until then we keep this.
  @WEL-19544  @WEL-13103 @DETAIL
  Scenario: View details of a Hold Case on the Hold Detail
    Given on "Holds Dashboard" page
    And a Hold Case
    When Hold Case is selected
    Then page "Hold Detail" is loaded
    And User is able to see below hold details visible on page
      | Text            |
      | Customer Name   |
      | Customer Number |
      | Hold ID         |
      | Type            |
      | BOL/DSM Number  |
      | Created By      |
      | Time On Hold    |
      | Status          |
      | MAX             |
      | EXP             |
      | GND             |
      | INTL            |
      | Pallets         |

    # TODO: And when user clicks on the View/Add attachments button they will be able to see all the details

  @WEL-13103
  Scenario Outline: Hold Case details are displayed on Hold Detail page
    Given Hold Case with "<Hold ID>", "<BOL/DSM Number>", "<Customer Name>", "<Customer Number>", "<Hold Type>", "<Status>", "<MAX>", "<EXP>", "<GND>", "<INTL>", "<Pallets>", "<Time On Hold>"
    And hold case is selected
    When on "Hold Detail" page
    Then Hold Case displays following values
      | Field              | Value                          |
      | Hold ID            | <Hold ID>                      |
      | BOL/DSM Number     | <BOL/DSM Number>               |
      | Hold Type          | <Hold Type>                    |
      | Customer Name      | <Customer Name>                |
      | Customer Number    | <Customer Number>              |
#      | Created By         |      -                         |
      | Time On Hold       | <Time On Hold>                 |
      | MAX                | <MAX>                          |
      | EXP                | <EXP>                          |
      | GND                | <GND>                          |
      | INTL               | <INTL>                         |
      | Pallets            | <Pallets>                      |
      | Status             | <Status>                       |
    Examples:
      | Hold ID | BOL/DSM Number | Customer Name     | Customer Number | Hold Type          | Status               | MAX | EXP | GND | INTL | Pallets | Time On Hold                  |
      | 4       |    1234        | David Miller      | 6543            | NO SLOT FOUND      | Released             | 7   | 2   | 10  | 3    | 10      | 2024-03-29T11:34:58.000+0000  |

  @WEL-13106
  Scenario: User must be able to select back button on the Hold Detail page and go back to Hold Management page
    Given a Hold Case
    And on "Hold Detail" page
    When "DASHBOARD" is selected
    Then page "Holds Dashboard" is loaded

  @WEL-13107
  Scenario: User must be able to scroll through all the hold event details on the Hold detail page
    Given a Hold Case with "50" comments
    When on "Hold Detail" page
    Then hold events are displayed
    And it is possible to scroll to see all events

  @WEL-19542 @todo
  Scenario: User seeing the bottom of the case history when entering the page
    Given a Hold Case
    And on "Hold Detail" page
    When "20" Hold Case events
    Then most recent events are displayed on the bottom

  Scenario: File attached can be seen at the Hold Case Details screen
    Given a Hold Case
    And following files are attached
      | file1.jpg |
      | file2.jpg |
      | file3.jpg |
      | file4.jpg |
      | file5.png |
      | file6.pdf |
    When on "Hold Detail" page
    Then attached file names are visible
      | file1.jpg |
      | file2.jpg |
      | file3.jpg |
      | file4.jpg |
      | file5.png |
      | file6.pdf |

  @WEL-13128
  Scenario: Photos can be seen at the Hold Case Details screen
    Given a Hold Case with image attached
    When on "Hold Detail" page
    Then attached image is visible

  @WEL-19545
  Scenario: Printing Hold Placard
    Given a Hold Case
    And on "Hold Detail" page
    When "Placard" is selected
    Then system prints the "Hold Placard"
