#@wiremock
Feature: Provide Hold Dashboard where Hold Cases are listed

# "Customer Name" should be "Pickup Customer Name"” on all pages
# "Customer Number" should be "Pickup Customer Number" on all pages
# I'd like to add a field: "BOL/DSM Number" to the "Holds Dashboard" page and "Holds Details" Page
# I'd like to add the "Total Number of Pallets" value for the case to the "Holds Details" page
# "Info Required" Status should be "Information Required" and should match what is in Salesforce, but it does not need to change if it is already setup. -
# The CSV export should match the current filters on the dashboard, and also include the "BOL/DSM Number" field

  Background:
    Given "Hold Cases" detail exist
#      | id | NA_BOL_Number__c | CaseNumber | status             | NA_Mail_Terminal__c | NA_Total_Pallet_Count_On_Placard__c | NA_Case_Detail__c    | Qty_Of_Expedited_MAX_items__c | Qty_Of_Expedited_items__c | Qty_Of_Ground_items__c | NA_Qty_Of_International_Items__c | Last_Opened_Timestamp__c       |
#      | case no | BOL/DSM Number | Customer Name     | customer | status                 | facility | pallets | hold type       | MAX | EXP | GND | INTL | createdon                  |
      | Hold ID   | BOL/DSM Number   | Customer Name       | Customer Number   | Hold Type            | Status                 | MAX   | EXP   | GND   | INTL   | Pallets   | Time on Hold                    | facility   |
      | --------- | ---------------- | ------------------- | ----------------- | -------------------- | ---------------------- | ----- | ----- | ----- | ------ | --------- | ------------------------------- | ---------- |
      | 1         | 9876             | Alice Johnson       | 5678              | BARCODE QUALITY      | Open                   | 7     | 2     | 10    | 3      | 10        | 3 months 27 days 1 hour         | ATL        |
      | 2         | 5678             | Bob Smith           | 1234              | SORT CODE ISSUE      | Info Required          | 7     | 2     | 10    | 3      | 10        | 3 months 22 days                | CVG        |
      | 3         | 1122             | Emily Brown         | 9876              | LATE ARRIVAL         | Returned to Customer   | 7     | 2     | 10    | 3      | 10        | 3 months 6 days 2 hours         | DFW        |
      | 4         | 5566             | David Miller        | 6543              | NO SLOT FOUND        | Released               | 7     | 2     | 10    | 3      | 10        | 3 months 5 days 2 hours         | ATL        |
      | 5         | 1234             | Sophia Wilson       | 7890              | DG HOLD              | Closed                 | 7     | 2     | 10    | 3      | 10        | 3 months 3 days                 | CVG        |
      | 6         | 3344             | James Anderson      | 2345              | MISSING IAC          | Released               | 7     | 2     | 10    | 3      | 10        | 3 months 1 day 22 hours         | DFW        |
      | 7         | 5566             | Olivia Martinez     | 8765              | SORT CODE ISSUE      | Info Required          | 7     | 2     | 10    | 3      | 10        | 3 months 1 day 1 hour           | ATL        |
      | 8         | 7788             | Lucas Taylor        | 3456              | MISSING IAC          | Returned to Customer   | 7     | 2     | 10    | 3      | 10        | 2 months 29 days 22 hours       | CVG        |
    And logged in
#      #todo time on hold maybe to be created date? How to set the date to arbitrary date?

#    Given authenticated
#    Given logged in
#    Given these Hold Cases exist
#      | case no | customer  | status             | facility | pallets | hold type | MAX | EXP | GND | INTL | createdon    |
#      |       1 | 000123445 | Open               |      ATL |      10 |   No Data |   7 |   2 |  10 |    3 | now          |
#      |       2 | 000123445 | Open               |      CVG |      10 |   No Data |   7 |   2 |  10 |    3 | -10min       |
#      |       3 | 000123445 | Info Required      |      DFW |      10 |   No Data |   7 |   2 |  10 |    3 | -1h 7min     |
#      |       4 | 000123445 | Return to Customer |      ATL |      10 |   No Data |   7 |   2 |  10 |    3 | -23h 59min   |
#      |       5 | 000123445 | Return to Customer |      CVG |      10 |   No Data |   7 |   2 |  10 |    3 | -1d 0h 1min  |
#      |       6 | 000123445 | Released           |      LAX |      10 |   No Data |   7 |   2 |  10 |    3 | -30d 14h 59min |
#      |       7 | 000123445 | Closed             |      LAX |      10 |   No Data |   7 |   2 |  10 |    3 | -30d 14h 59min |
#      #todo time on hold maybe to be created date? How to set the date to arbitrary date?

  Scenario: All Hold Case fields are displayed
    Given on "Holds Dashboard" page
    When page "Hold Detail" is loaded
    Then hold fields is one of the table headers in this order
      | Text              |
      | Hold ID           |
      | BOL/DSM Number    |
      | Customer Name     |
      | Customer Number   |
      | Hold Type         |
      | Status            |
      | MAX               |
      | EXP               |
      | GND               |
      | INTL              |
      | Pallets           |
      | Time on Hold      |

  @WEL-13288
  Scenario: Holds Dashboard is the application landing page
    Given on "Holds Login" page
    When logged in
    Then page "Holds Dashboard" is loaded

  @mobile
  Scenario: Hold Cases details visible on each tile
    Given on "Holds Dashboard" page
    # TODO specific tile? just labels? covered in next scenario?
    When user expands the tile
    Then the "SF case #" is displayed
    And the "Pickup Customer Name" is displayed
    And the "number of containers on Hold" is displayed
    # TODO separate scenario - format and other details
    And the "time the Hold Case has been opened" is displayed
    And the "Hold Case status" is displayed
    And the "Hold Case type" is displayed

  @mobile
  Scenario Outline: Hold Cases details visible on each tile
    Given user is on the "Hold Dashboard" page
    When user expands case "1"
    Then the <case no> is displayed
    And the <customer> is displayed
    And the <status> is displayed
    And the <timeonhold> is displayed
    And the <status> is displayed
    And the <hold type> is displayed

    Examples:
      | case no | customer | status | facility | pallets | hold type | MAX | EXP | GND | INTL | timeonhold |
      | 1       | 123445   | Open   | ATL      | 10      | No Data   | 7   | 2   | 10  | 3    | 50         |

# As a system, Dashboard drop downs should not be collapsed by default when entering the screen. For each hold case is displayed on a tile- SF case #, Pickup Customer name, no. of containers on Hold, time, the hold has been open, hold status, and hold Type so that all the details of each hold case are visible on the tile.

    # TODO time on hold check also in selected facility time zone
    # TODO check the time on hold format

#  @WEL-13083 @mobile @todo
#  Scenario: Hold Case tiles on the Hold Dashboard page are clickable
#    Given on "Holds Dashboard" page
#    When system displays Each Hold Case tile on the Screen
#    Then Those tiles should be Clickable/ selected in the Interface.

    # TODO possibly can be removed if details are checked elsewhere
#  @WEL-13082 @todo @mobile
#  Scenario: Displaying Hold Case details on the Holds Dashboard Page
#    # todo: turn this into an example that uses background cases to validate the data is displayed
#    Given User is on the "Holds Dashboard" page using a Mobile device
#    #And there are any Hold cases created/added
#    And Each case are displayed under designated dropdown
#    # TODO should list exactly what details
#    Then each tile should have all the listed information for each Hold Case.

    #As a system, Dashboard drop downs should not be collapsed by default when entering the screen. For each hold case is displayed on a tile- SF case #, Pickup Customer name, no. of containers on Hold, time, the hold has been open, hold status, and hold Type so that all the details of each hold case are visible on the tile.

  # TODO move to facility feature?

  @todo @clarify @mobile
  Scenario: Hold Cases listed on Holds Dashboard are expanded
    Given User is on the "Holds Dashboard" page
    Then drop downs should not be collapsed by default

  # TODO this is to behave same way on desktop and mobile
  # TODO duplicate with the next scenario
  # Scenario: Hold Case tiles can be expanded
  #   Given user is on the "Hold Dashboard" page
  #   When user expands the tile
  #   Then the tile is expanded
  #   And shows following Hold Case details

  # TODO move to facility feature?
  @WEL-13075 @query
  Scenario Outline: Only cases for selected facility are listed on the Holds Dashboard
    Given on "Holds Dashboard" page
    When selects "<facility>" from "Facility" modal
#    When  <facility> is the current facility
    Then Hold Cases "<noOfCases>" are visible in "<status>"
    # TODO implement to check list and add to preconditions
    #Then those Open cases for the local DC should be displayed automatically on the Hold Mgmt Screen.

    Examples:
      | facility   | status                                                | noOfCases   |
      | ---------- | ----------------------------------------------------- | ----------- |
      | ATL        | Released, Open                                        | 2           |
      | CVG        | Info Required, Returned to Customer                   | 2           |
      | DFW        | Returned to Customer, Released, Open                  | 3           |


  #TODO following scenarios possibly check for screen width if applicable only on mobile devicez
  @mobile
  Scenario: Hold Cases groups are collapsed
    Given logged in
    When on "Holds Dashboard" page
    Then Hold Case status groups are collapsed

  # TODO following can be outlines if we want to check all groups
  @mobile

  Scenario: Hold Case status groups can be expanded
    Given on "Holds Dashboard" page
    And "Released" status group is collapsed
    When "Released" is selected
    Then "Released" status group is expanded

  @mobile
  Scenario: Hold Case status groups are collapsed
    Given on "Holds Dashboard" page
    And "Open" status group is expanded
    When "Open" is selected
    Then "Open" status group is collapsed

  @WEL-13082 @mobile
  Scenario Outline: Each Hold Case details are visible on Holds Dashboard
    Given user is on the "Holds Dashboard" page
    # TODO expansion of the status group only on mobile
    When user expands case <holdid>
    Then "Hold ID" is <holdid>
    And "Pickup Customer Name" is <customername>
    And "Pickup Customer Number" is <customer>
     # TODO separate scenario - format and other details
    And "Time On Hold" is <timeonhold>
    And "Hold Type" is <holdtype>
    And "Number of Pallets" is <pieces>
    And "Service(s)" is <service>
    And "Case Status" is <status>
    And "BOL/DSM Number" is <bol>

    Examples:
      | holdid | customername | customer  | status | facility | pallets | hold type | MAX | EXP | GND | INTL | timeonhold | totalpallets |
      | 1      | John Doe     | 000123445 | Open   | ATL      | 10      | No Data   | 7   | 2   | 10  | 3    | 50         | 22           |

# As a system, Dashboard drop downs should not be collapsed by default when entering the screen. For each hold case is displayed on a tile- SF case #, Pickup Customer name, no. of containers on Hold, time, the hold has been open, hold status, and hold Type so that all the details of each hold case are visible on the tile.

    # TODO time on hold check also in selected facility time zone
    # TODO check the time on hold format

  # TODO - how to formulate for automation?
#  @WEL-13099 @todo @SORT
#  Scenario: Default sorting of Hold Cases on Dashboard
#    Given on "Holds Dashboard" page
#    Then Hold Cases are sorted by "Status" in order "Released" -> "Info Required" -> "Return to Customer" -> "Open"
#    And Hold Cases of the same status are sorted by "Time on Hold" descending

  @WEL-13099 @todo
  Scenario: Default sorting of Hold Cases on Dashboard
    Given on "Holds Dashboard" page
    Then Hold Cases are sorted by status in order
      | Released | Info Required | Returned to Customer | Open |
    And Hold Cases of the same status are sorted by time on hold column
      | columnName     | order       | defaultSortedHoldIds |
      | Time On Hold   | descending  | 6,4,2,7,3,8,1        |

  @WEL-13087 @todo
  Scenario Outline: Sorting of Hold Cases on Holds Dashboard
    Given on "Holds Dashboard" page
    When sorting cases by "<sortField>" in "<order>"
    Then Hold Cases are displayed in "<holdIds>" order

    Examples:
      | sortField       | order      | holdIds       |
      | Status          | ascending  | 2,7,1,4,6,3,8 |
      | Status          | descending | 3,8,4,6,1,2,7 |
      | Customer Number | ascending  | 2,6,8,1,4,7,3 |
      | Customer Number | descending | 3,7,4,1,8,6,2 |
      | Customer Name   | ascending  | 1,2,4,3,6,8,7 |
      | Customer Name   | descending | 7,8,6,3,4,2,1 |
      | Pallets         | ascending  | 1,2,3,4,6,7,8 |
      | Pallets         | descending | 1,2,3,4,6,7,8 |
      | Hold Type       | ascending  | 1,3,6,8,4,2,7 |
      | Hold Type       | descending | 2,7,4,6,8,3,1 |
      | MAX             | ascending  | 1,2,3,4,6,7,8 |
      | MAX             | descending | 1,2,3,4,6,7,8 |
      | EXP             | ascending  | 1,2,3,4,6,7,8 |
      | EXP             | descending | 1,2,3,4,6,7,8 |
      | GND             | ascending  | 1,2,3,4,6,7,8 |
      | GND             | descending | 1,2,3,4,6,7,8 |
      | INTL            | ascending  | 1,2,3,4,6,7,8 |
      | INTL            | descending | 1,2,3,4,6,7,8 |
      | Time on Hold    | ascending  | 8,7,6,4,1,3,2 |
      | Time on Hold    | descending | 1,2,3,4,6,7,8 |


  # TODO on mobile the cases are grouped by status
  @WEL-13089
  Scenario: User must be able to scroll through all the Hold Cases on the Holds Dashboard page
    Given logged in
    When on "Holds Dashboard" page
    Then Hold Cases are displayed
      | Hold ID |
      | 1       |
      | 2       |
      | 3       |
      | 4       |
      | 6       |
      | 7       |
      | 8       |
    And it is possible to scroll to see all Hold Cases

  @WEL-13089 @mobile
  Scenario: user must be able to see all Hold tiles at once
    Given Several SF Hold cases are created
    When hold cases are added to the Hold Mgmt screen
    Then the system displays all the Hold Cases on the Hold Mgmt screen
    And all the cases can be viewed on the H.Mgmt screen at once.
    And user must be able to scroll down to view all the hold cases

#  @WEL-13090 @WEL-13093
#  Scenario: Hold Cases can be filtered by Hold ID
#    Given on "Holds Dashboard" page
#    When sorting cases by "Hold ID" ascending
#    Then Hold Cases are displayed in this order
#
#  @todo
#  Scenario: Hold Cases can be filtered by Customer Name
#
#  @todo
#  Scenario: Hold Cases can be filtered by Customer Number
#
#  @todo
#  Scenario: Hold Cases can be filtered by Status
#
#  @todo
#  Scenario: Hold Cases can be filtered by Number of Pallets
#
#  @todo
#  Scenario: Hold Cases can be filtered by Hold Type
#
#  @todo
#  Scenario: Hold Cases can be filtered by Time on Hold


  Scenario Outline: Full text search
    Given on "Holds Dashboard" page
    When text "<searchtext>" is entered in "Search" box
    Then only Hold Cases with "<holdIds>" are displayed
    Examples:
      | searchtext           | holdIds       |
      | BARCODE QUALITY      | 1             |
      | SORT CODE ISSUE      | 2,7           |
      | LATE ARRIVAL         | 3             |
      | NO SLOT FOUND        | 4             |
      | MISSING IAC          | 6,8           |
      | 9876                 | 1             |
      | 5678                 | 2             |
      | 1122                 | 3             |
      | 5566                 | 4,7           |
      | 3344                 | 6             |
      | 7788                 | 8             |
      | Alice Johnson        | 1             |
      | Bob Smith            | 2             |
      | Emily Brown          | 3             |
      | David Miller         | 4             |
      | James Anderson       | 6             |
      | Olivia Martinez      | 7             |
      | Lucas Taylor         | 8             |
      | 5678                 | 1             |
      | 1234                 | 2             |
      | 9876                 | 3             |
      | 6543                 | 4             |
      | 2345                 | 6             |
      | 8765                 | 7             |
      | 3456                 | 8             |
      | Open                 | 1             |
      | Info Required        | 2,7           |
      | Returned to Customer | 3,8           |
      | Released             | 4,6           |
      | Closed               | 5             |
      | ATL                  | 1,4,7         |
      | CVG                  | 2,8           |
      | DFW                  | 3,6           |
      | 10                   | 1,2,3,4,6,7,8 |
      | 7                    | 1,2,3,4,6,7,8 |
      | 2                    | 1,2,3,4,6,7,8 |
      | 10                   | 1,2,3,4,6,7,8 |
      | 3                    | 1,2,3,4,6,7,8 |
#      | 3 months 4 days 17 hours | 1                |
#      | 3 months 25 days 16 hours| 2                |
#      | 3 months 20 days 16 hours| 3                |
#      | 3 months 3 days 17 hours | 4                |
#      | 3 months 13 hours        | 6                |
#      | 2 months 29 days 16 hours| 7                |
#      | 2 months 28 days 14 hours| 8                |



#       TODO add more examples

  @todo @query
  Scenario Outline: Time on hold is displayed
    Given on "Holds Dashboard" page
    When facility "ATL" is current
    And Hold Case <caseno> is expanded
    Then time on hold is <holdtime>
    # time is difference UTC time now - UTC time created
    # TODO dynamic validation?
    Examples:
      | caseno | holdtime |
      | 1      | 0min     |

  #TODO logout functionality
  @WEL-13098 @todo
  Scenario: User can log out from application
    Given on "Holds Dashboard" page
    When logged out
    Then on "Holds Login" page
    And not logged in

  @query @salesforce
  Scenario: Closed cases are not displayed
    Given on "Holds Dashboard" page
    When "ATL" is the current facility
    Then Hold Case "5" is not visible

  @WEL-19532 @todo @clarify
  Scenario: Sorting fields in Ascending or Descending
    Given the user is on the SmartScan Platform
    When the user is logged in to the Hold management application
    Then the Tiles of different Hold Statuses are distinguished by different color codes are displayed (Release - Green, Info required - Yellow, Returned to Customer - Red, Open - Blue or white)
    Then the User must be able to sort holds (ascending or Descending) by a particular field


  @WEL-13099 @todo @mobile
  Scenario Outline: Hold Case groups are displayed in following status order: released, info required, return to Customer, and Open
    Given on "Holds Dashboard" page
    When each Cases tiles with its Status are available on the Screen
    And The user wants to review any Hold Case Status
    Then Cases on the H.Mgmt Screen can be ordered by Status (released, info required, info required, return to Customer, and Open)
# As a System, Cases on the H.Mgmt Screen are ordered by Status (released, info required, info required, return to Customer, Open) so that the User can see the detailed Status of all the Hold cases (Hold Type, Pickup Customer name, Pickup Customer number, SF case #, and Hold ld Placard Code).

    Examples:
      | Released             |
      | Information Required |
      | Release to Customer  |
      | Open                 |


  # TODO - what is the case workflow?
  Scenario: What happens to cases that are closed?

  @WEL-13101 @todo @mobile
  Scenario: Facility workers are able to see Holds on the screen by displaying Bright-Sign
    Given the user is on the hold dashboard screen
    When any HOLD data is displayed
    Then the data displayed is in a Large and Bright Sign for Operations.

  @WEL-13075 @mobile
  Scenario: User is able to Hold cases on the Hold Management screen
    Given The user is using a Mobile device
    And SF cases are existing or added
    When User is on the Hold Mgmt Screen
    Then those Open cases for the local DC should be displayed automatically on the Hold Mgmt Screen.

  @WEL-19534 @todo @clarify
  Scenario: Hold Case tiles are colored differently
    Given on "Holds Dashboard" page
    Then the system must display all the Tiles of different Hold Statuses
    And Hold Statuses must be distinguished by different color codes in the interface.

#TODO specify what colors represent what status, split out to individual scenarios
# Released - green
# Info Required - yellow
# Returned to Customer - red
# Open - white


  @WEL-19747 @todo
  Scenario Outline: Hold Cases by service statistics
    Given logged in
    When on "Hold Dashboard" page
    Then "<service>" displays "<sumcases>" cases
    Examples:
      | service | sumcases |
      | MAX     | 49       |
      | EXP     | 14       |
      | GND     | 70       |
      | INTL    | 21       |
      | PALLETS | 70       |


  @clarify
  Scenario: Cases can be exported to CSV
    Given on "Holds Dashboard" page
#    When "Export to CSV" is selected
    When Export to "CSV" is selected
    Then CSV file is generated with following content
      | Hold ID | BOL/DSM Number   | Customer Name   | Customer Number | Hold Type       | Status               | MAX | EXP | GND | INTL | Pallets | Time on Hold                 |
      | 4       | 5566             | David Miller    | 6543            | NO SLOT FOUND   | Released             | 7   | 2   | 10  | 3    | 10      | 2024-07-09T11:34:58.000+0000 |
      | 6       | 3344             | James Anderson  | 2345            | MISSING IAC     | Released             | 7   | 2   | 10  | 3    | 10      | 2024-04-02T15:10:17.000+0000 |
      | 2       | 5678             | Bob Smith       | 1234            | SORT CODE ISSUE | Info Required        | 7   | 2   | 10  | 3    | 10      | 2024-03-12T13:01:02.000+0000 |
      | 7       | 5566             | Olivia Martinez | 8765            | SORT CODE ISSUE | Info Required        | 7   | 2   | 10  | 3    | 10      | 2024-04-03T12:44:27.000+0000 |
      | 3       | 1122             | Emily Brown     | 9876            | LATE ARRIVAL    | Returned to Customer | 7   | 2   | 10  | 3    | 10      | 2024-03-28T11:28:30.000+0000 |
      | 8       | 7788             | Lucas Taylor    | 3456            | MISSING IAC     | Returned to Customer | 7   | 2   | 10  | 3    | 10      | 2024-04-04T14:55:01.000+0000 |
      | 1       | 9876             | Alice Johnson   | 5678            | BARCODE QUALITY | Open                 | 7   | 2   | 10  | 3    | 10      | 2024-03-07T13:23:34.000+0000 |

  #TODO: add BOL/DSM field

  Scenario: Filtered and sorted cases can be exported to CSV
    Given on "Holds Dashobard" page
#    And TODO add filters here
    When sorting cases by "Status" in "ascending"
    And Export to "CSV" is selected
    Then CSV file is generated with following content
#      | case no | customer | status | facility | pallets | hold type | MAX | EXP | GND | INTL | timeonhold | BOL/DSM Number |
#      |       1 |   123445 |   Open |      ATL |      10 |   No Data |   7 |   2 |  10 |    3 |       0min | 12345          |

      | Hold ID | BOL/DSM Number   | Customer Name   | Customer Number | Hold Type       | Status               | MAX | EXP | GND | INTL | Pallets | Time on Hold                 |
      | 2       | 5678             | Bob Smith       | 1234            | SORT CODE ISSUE | Info Required        | 7   | 2   | 10  | 3    | 10      | 2024-03-12T13:01:02.000+0000 |
      | 7       | 5566             | Olivia Martinez | 8765            | SORT CODE ISSUE | Info Required        | 7   | 2   | 10  | 3    | 10      | 2024-04-03T12:44:27.000+0000 |
      | 1       | 9876             | Alice Johnson   | 5678            | BARCODE QUALITY | Open                 | 7   | 2   | 10  | 3    | 10      | 2024-03-07T13:23:34.000+0000 |
      | 4       | 5566             | David Miller    | 6543            | NO SLOT FOUND   | Released             | 7   | 2   | 10  | 3    | 10      | 2024-03-29T11:34:58.000+0000 |
      | 6       | 3344             | James Anderson  | 2345            | MISSING IAC     | Released             | 7   | 2   | 10  | 3    | 10      | 2024-04-02T15:10:17.000+0000 |
      | 3       | 1122             | Emily Brown     | 9876            | LATE ARRIVAL    | Returned to Customer | 7   | 2   | 10  | 3    | 10      | 2024-03-28T11:28:30.000+0000 |
      | 8       | 7788             | Lucas Taylor    | 3456            | MISSING IAC     | Returned to Customer | 7   | 2   | 10  | 3    | 10      | 2024-04-04T14:55:01.000+0000 |




