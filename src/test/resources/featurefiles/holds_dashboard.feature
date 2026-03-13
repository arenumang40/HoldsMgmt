Feature: Hold Dashboard Page
  where Hold Cases are listed.

  Background:
    Given on "Holds Login" page
    And logged in
    And page "Holds Dashboard" is loaded
    And selects "BWI" from facility modal
    And "BWI" is the current facility
    And Hold Cases detail exist
      | Case Number | BOL/DSM Number | Pickup Customer Name             | Pickup Customer Number | Hold Type           | Status             | MAX | EXP | GND | INTL | Pallets | Time on Hold                 |
      | 06483312    | 1987           | POSH BOUTIQUE                    | 5332740                | NO SLOT FOUND       | Released           | —   | 4   | 3   | 2    | 6       | 2024-10-08T14:18:06.000+0000 |
      | 06483313    | 93576900       | DAVE'S COFFEE                    | 5337100                | LATE ARRIVAL        | Released           | 6   | 7   | 45  | 7    | —       | 2024-10-08T14:18:48.000+0000 |
      | 06483314    | 4688           | BLUEBIRD PAPERIE                 | 5332988                | DIMMING             | Released           | 45  | 32  | 12  | 9    | 65      | 2024-10-08T14:19:22.000+0000 |
      | 06483315    | 98123          | JUICE BEAUTY INC C/O RETURN DEPT | 5331782                | MIXED SERVICES      | Info Required      | 5   | 6   | 78  | 6    | 7       | 2024-10-08T14:38:10.000+0000 |
      | 06483316    | 10101          | soldto_3Pickup3                  | 5355643                | MISSING DSM/BOL     | Return to Customer | 7   | 3   | 9   | 98   | 8       | 2024-10-08T14:41:51.000+0000 |
      | 06483317    | 8587           | SKIN CARE BY SUZIE               | 5337052                | NO ACTIVE AGREEMENT | Return to Customer | 45  | 4   | 5   | 8    | 56      | 2024-10-08T14:44:43.000+0000 |
      | 06482988    | 81004          | —                                | —                      | CREDIT HOLD         | Open               | 3   | 2   | 4   | 1    | 5       | 2024-08-06T08:50:57.000+0000 |
      | 06483309    | 1234           | 3veni PUL 1                      | 5355567                | NO ACTIVE AGREEMENT | Open               | 3   | 2   | 1   | 7    | 6       | 2024-10-08T14:10:14.000+0000 |
      | 06483310    | 3124           | SIT2 business                    | 987000                 | BARCODE QUALITY     | Open               | 4   | 3   | —   | 1    | 5       | 2024-10-08T14:12:42.000+0000 |
      | 06483311    | 9872           | BETHANY LANE                     | 5332433                | TSA NOT APPROVED    | Open               | —   | —   | 1   | 5    | 6       | 2024-10-08T14:14:47.000+0000 |

  @All @UI @Smoke @WEL-13288
  Scenario: Verify Headers displayed on Dashboard
    When page "Holds Dashboard" is loaded
    Then page contains "<table header>" list
      | Case Number            |
      | BOL/DSM Number         |
      | Pickup Customer Name   |
      | Pickup Customer Number |
      | Hold Type              |
      | Status                 |
      | MAX                    |
      | EXP                    |
      | GND                    |
      | INTL                   |
      | Pallets                |
      | Time on Hold           |

  @All @UI @WEL-13288
  Scenario: Verify toolbar Options 'Columns, Filters, Density & Export' displayed on Dashboard
    When page "Holds Dashboard" is loaded
    Then page contains "<toolbar>" list
      | COLUMNS |
      | FILTERS |
      | DENSITY |
      | EXPORT  |

  @All @UI  @Mobile @WEL-13288
  Scenario: Verify Dashboard page Elements
    When page "Holds Dashboard" is loaded
    Then "DASHBOARD" header is visible on page
    And "Refresh Data" button is visible
    And "Create Hold" button is visible
    And "Filters" button is visible
    And "Search" textbox is visible
    And "Total rows" is visible

  @All @UI @WEL-13087
  Scenario Outline: User is able to sort holds - for '<sortField> in <order> Order'
    When sorting cases by "<sortField>" in "<order>"
    Then Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | sortField            | order      | caseNumber                                                                                         |
      | BOL/DSM Number       | ascending  | 06483316, 06483309, 06483312, 06483310, 06483314, 06482988, 06483317, 06483313, 06483315, 06483311 |
#      | BOL/DSM Number       | descending | 06483311, 06483315, 06483313, 06483317, 06482988, 06483314, 06483310, 06483312, 06483309, 06483316 |
#      | Pickup Customer Name | ascending  | 06482988, 06483309, 06483311, 06483314, 06483313, 06483315, 06483312, 06483310, 06483317, 06483316 |
#      | Pickup Customer Name | descending | 06483316, 06483317, 06483310, 06483312, 06483315, 06483313, 06483314, 06483311, 06483309, 06482988 |
#      | Case Number          | descending | 06483317, 06483316, 06483315, 06483314, 06483313, 06483312, 06483311, 06483310, 06483309, 06482988 |
#      | MAX                  | ascending  | 06483312, 06483311, 06482988, 06483309, 06483310, 06483315, 06483313, 06483316, 06483314, 06483317 |
#      | INTL                 | descending | 06483316, 06483314, 06483317, 06483313, 06483309, 06483315, 06483311, 06483312, 06482988, 06483310 |
#      | Time on Hold         | ascending  | 06482988, 06483309, 06483310, 06483311, 06483312, 06483313, 06483314, 06483315, 06483316, 06483317 |
      | Time on Hold         | descending | 06483317, 06483316, 06483315, 06483314, 06483313, 06483312, 06483311, 06483310, 06483309, 06482988 |

  @All @UI @Smoke @WEL-13099
  Scenario: user must be able to see Hold cases in these order (Released, Info Required, Return to Customer, and Open)
    When on "Holds Dashboard" page
    Then Hold Cases are sorted by status in order
      | Released | Info Required | Return to Customer | Open |
    And Hold Cases displayed in this order as sorted by TimeOneHold
      | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311 |

  @All @UI @WEL-13089
  Scenario: User must be able to scroll through all the Hold displayed on dashboard
    When on "Holds Dashboard" page
    Then Hold Cases displayed in this order as sorted by TimeOneHold
      | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311 |
    And it is possible to scroll to see all Hold Cases

  @All @UI
  Scenario Outline: User is able to search holds - for '<searchText>' value in search textbox.
    When text "<searchText>" is entered in "Search" box
    Then Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | searchText          | caseNumber                                                                               |
#      | Release             | 06483312, 06483313, 06483314                                                             |
#      | Info                | 06483315                                                                                 |
#      | Open                | 06482988, 06483309, 06483310, 06483311                                                   |
      | Return              | 06483315, 06483316, 06483317                                                             |
#      | BARCODE QUALITY     | 06483310                                                                                 |
#      | NO ACTIVE AGREEMENT | 06483317, 06483309                                                                       |
#      | LATE ARRIVAL        | 06483313                                                                                 |
#      | NO                  | 06483312, 06483317, 06483309, 06483311                                                   |
#      | OL                  | 06483316, 06482988                                                                       |
#      | RE                  | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309           |
#      | MI                  | 06483314, 06483315, 06483316                                                             |
#      | 06482               | 06482988                                                                                 |
#      | 06483               | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483309, 06483310, 06483311 |
#      | 87                  | 06483312, 06483317, 06483310, 06483311                                                   |
#      | 00                  | 06483313, 06482988, 06483310                                                             |
      | Account             |                                                                                          |
#      | 000                 | 06483310                                                                                 |
#      | 98                  | 06483312, 06483314, 06483315, 06483316, 06482988, 06483310, 06483311                     |
#      | 81                  | 06483315, 06482988                                                                       |
#      | 1234                | 06483309                                                                                 |
#      | _                   | 06483316                                                                                 |
#      | 987                 | 06483312, 06483310, 06483311                                                             |
#      | 3veni               | 06483309                                                                                 |
#      | 5332                | 06483312, 06483314, 06483311                                                             |
#      | 5332988             | 06483314                                                                                 |

  @All @UI
  Scenario Outline: User should get message "No results found." - for no matched Holds with '<searchText>' value in search textbox.
    When text "<searchText>" is entered in "Search" box
    Then message "No results found." display
    Examples:
      | searchText    |
#      | Releases      |
#      | SORT          |
      | MISSING IAC   |
#      | 9876          |
#      | 5678          |
#      | Alice Johnson |
#      | Bob Smith     |
      | Close         |
#      | BWI           |
#      | 0648332       |
#      | Seconds       |
#      | Minute        |

  @All @UI
  Scenario: User is able to find service (MAX, EXP, GND, INTL , PALLETS) at Dashboard Footer.
    When on "Holds Dashboard" page
    And page "Holds Dashboard" is loaded
    Then page contains "<footer services>" list
      | MAX     |
      | EXP     |
      | GND     |
      | INTL    |
      | PALLETS |

  @All @UI
  Scenario Outline: User is able to find the Service '<service>' with its Sum Values '<sumcases>'.
    When on "Holds Dashboard" page
    And page "Holds Dashboard" is loaded
    Then "<service>" displays "<sumcases>" cases
    Examples:
      | service         | sumcases |
      | max             | 118      |
      | exp             | 63       |
      | gnd             | 158      |
      | intl            | 144      |
      | numberOfPallets | 164      |

  @All @UI
  Scenario: User is able to see all columns with checkbox, once clicked on Columns option
    When click on "Columns" button
    Then column should display with checkbox as
      | columnName             | isEnable | isSelected |
      | Case Number            | false    | true       |
      | BOL/DSM Number         | true     | true       |
      | Soldto Customer Name   | true     | false      |
      | Soldto Customer Number | true     | false      |
      | Pickup Customer Name   | true     | true       |
      | Pickup Customer Number | true     | true       |
      | Hold Type              | true     | true       |
      | Status                 | true     | true       |
      | MAX                    | true     | true       |
      | EXP                    | true     | true       |
      | GND                    | true     | true       |
      | INTL                   | true     | true       |
      | Pallets                | true     | true       |
      | Time on Hold           | true     | true       |
      | Show/Hide All          | true     | false      |

  @All @UI
  Scenario Outline: User should not be able to see removed header at dashboard ( Unchecked - '<columnsForUncheck>' columns)
    When click on "Columns" button
    And uncheck the "<columnsForUncheck>" from column list
    Then only "<headers>" headers display on Page
    Examples:
      | columnsForUncheck                                                                                                           | headers                                                                                                                                |
#      | BOL/DSM Number, Pickup Customer Number, Status, MAX                                                                         | Case Number, Pickup Customer Name, Hold Type, EXP, GND, INTL, Pallets, Time on Hold                                                    |
      | BOL/DSM Number, Soldto Customer Name, Pickup Customer Number, Hold Type, Status, MAX, EXP, GND, INTL, Pallets, Time on Hold | Case Number, Soldto Customer Name, Pickup Customer Name                                                                                |
#      | BOL/DSM Number, Pickup Customer Name, Pickup Customer Number                                                                | Case Number, Hold Type, Status, MAX, EXP, GND, INTL, Pallets, Time on Hold                                                             |
#      | Soldto Customer Name, MAX, GND, Time on Hold                                                                                | Case Number, BOL/DSM Number, Soldto Customer Name, Pickup Customer Name, Pickup Customer Number, Hold Type, Status, EXP, INTL, Pallets |
#      | BOL/DSM Number, Pickup Customer Number, Status, EXP, INTL, Time on Hold                                                     | Case Number, Pickup Customer Name, Hold Type, MAX, GND, Pallets                                                                        |
#      | Show/Hide All, Soldto Customer Name, Soldto Customer Number, Pickup Customer Name, Status, MAX, GND, INTL, Time on Hold     | Case Number, BOL/DSM Number, Pickup Customer Number, Hold Type, EXP, Pallets                                                           |
#      | BOL/DSM Number, Pickup Customer Name, Pickup Customer Number, MAX, EXP, GND                                                 | Case Number, Hold Type, Status, INTL, Pallets, Time on Hold                                                                            |
#      | Show/Hide All, Show/Hide All                                                                                                | Case Number                                                                                                                            |
      | Show/Hide All, Show/Hide All, Hold Type, EXP, INTL                                                                          | Case Number, Hold Type, EXP, INTL                                                                                                      |

  @All @UI
  Scenario Outline: User is able to see holds as per Density selected ('<densityOption>') at dashboard
    When click on "Density" button
    And click on "<densityOption>" button
    Then holds should display as "<densityStyle>"
    Examples:
      | densityOption | densityStyle                                        |
      | Compact       | max-height: 36px; min-height: 36px; --height: 36px; |
      | Standard      | max-height: 52px; min-height: 52px; --height: 52px; |
      | Comfortable   | max-height: 67px; min-height: 67px; --height: 67px; |

  @All @UI @Smoke
  Scenario: User is able to export hold records displayed on dashboard.
    When click on "Export" button
    And click on "Export to CSV" button
    Then CSV file is generated with content displayed on screen

  @All @UI @Smoke
  Scenario: User is able to print pdf of hold records displayed on dashboard.
    When click on "Export" button
    And click on "Print" button
    Then PDF file is generated with holds displayed on screen

  @All @UI
  Scenario Outline: User is able to export the sorted holds ('<sortField>' in <order> order) displayed on dashboard.
    When sorting cases by "<sortField>" in "<order>"
    And click on "Export" button
    And click on "Export to CSV" button
    Then CSV file is generated with content displayed on screen
    Examples:
      | sortField              | order      |
#      | BOL/DSM Number         | ascending  |
#      | BOL/DSM Number         | descending |
      | Pickup Customer Name   | ascending  |
#      | Pickup Customer Name   | descending |
#      | Pickup Customer Number | ascending  |
#      | Pickup Customer Number | descending |
#      | Hold Type              | ascending  |
#      | Hold Type              | descending |
#      | Status                 | ascending  |
#      | Status                 | descending |
#      | Pallets                | ascending  |
#      | Pallets                | descending |
#      | Case Number            | ascending  |
#      | Case Number            | descending |
#      | MAX                    | ascending  |
#      | MAX                    | descending |
#      | EXP                    | ascending  |
#      | EXP                    | descending |
#      | GND                    | ascending  |
#      | GND                    | descending |
#      | INTL                   | ascending  |
#      | INTL                   | descending |
#      | Time on Hold           | ascending  |
      | Time on Hold           | descending |

  @All @UI
  Scenario Outline: User is able to export the searched holds (by adding '<searchText>' in search textbox) displayed at dashboard
    When text "<searchText>" is entered in "Search" box
    And click on "Export" button
    And click on "Export to CSV" button
    Then CSV file is generated with content displayed on screen
    Examples:
      | searchText          |
#      | Release             |
      | Return              |
#      | NO ACTIVE AGREEMENT |
#      | LATE ARRIVAL        |
#      | RE                  |
#      | MI                  |
      | 06483               |
#      | 00                  |
#      | Account             |
#      | Umang               |
#      | 1234                |
#      | _                   |

  @All @Smoke @UI
  Scenario: Background color should be as per Hold Case Status
    When on "Holds Dashboard" page
    And page "Holds Dashboard" is loaded
    Then Holds Background color should be as per status
      | status             | background-color       |
      | Released           | rgba(76, 175, 80, 0.2) |
      | Info Required      | rgba(255, 152, 0, 0.2) |
      | Return to Customer | rgba(239, 83, 80, 0.2) |
      | Open               | rgba(0, 0, 0, 0)       |

  @All @UI @WEL-13087
  Scenario Outline: User should be able to filter the holds for '<column>' <operator> '<value>'
    When click on "Filters" button
    And  Select "<column>" after click on "Case Number" field
    And  Select "<operator>" after click on "<defaultOperator>" field
    And  text "<value>" is entered in "Filter value" box
    Then Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | column                 | defaultOperator | operator    | value            | caseNumber                                                                               |
      | Pallets                | =               | =           | 6                | 06483312, 06483309, 06483311                                                             |
#      | Pallets                | =               | !=          | 5                | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483309, 06483311           |
#      | Pallets                | =               | >           | 50               | 06483314, 06483317                                                                       |
#      | Pallets                | =               | >=          | 7                | 06483314, 06483315, 06483316, 06483317                                                   |
#      | Pallets                | =               | <           | 7                | 06483312, 06482988, 06483309, 06483310, 06483311                                         |
#      | Pallets                | =               | <=          | 5                | 06482988, 06483310                                                                       |
#      | INTL                   | =               | =           | 6                | 06483315                                                                                 |
      | INTL                   | =               | !=          | 5                | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310 |
#      | INTL                   | =               | >           | 50               | 06483316                                                                                 |
#      | INTL                   | =               | >=          | 7                | 06483313, 06483314, 06483316, 06483317, 06483309                                         |
#      | INTL                   | =               | <           | 7                | 06483312, 06483315, 06482988, 06483310, 06483311                                         |
#      | INTL                   | =               | <=          | 5                | 06483312, 06482988, 06483310, 06483311                                                   |
#      | GND                    | =               | =           | 4                | 06482988                                                                                 |
#      | GND                    | =               | !=          | 5                | 06483312, 06483313, 06483314, 06483315, 06483316, 06482988, 06483309, 06483310, 06483311 |
      | GND                    | =               | >           | 50               | 06483315                                                                                 |
#      | GND                    | =               | >=          | 7                | 06483313, 06483314, 06483315, 06483316                                                   |
#      | GND                    | =               | <           | 7                | 06483312, 06483317, 06482988, 06483309, 06483311                                         |
#      | GND                    | =               | <=          | 5                | 06483312, 06483317, 06482988, 06483309, 06483311                                         |
#      | EXP                    | =               | =           | 4                | 06483312, 06483317                                                                       |
#      | EXP                    | =               | !=          | 2                | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483310, 06483311           |
#      | EXP                    | =               | >           | 8                | 06483314                                                                                 |
      | EXP                    | =               | >=          | 7                | 06483313, 06483314                                                                       |
#      | EXP                    | =               | <           | 7                | 06483312, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310                     |
#      | EXP                    | =               | <=          | 5                | 06483312, 06483316, 06483317, 06482988, 06483309, 06483310                               |
#      | MAX                    | =               | =           | 4                | 06483310                                                                                 |
#      | MAX                    | =               | !=          | 3                | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483310, 06483311           |
#      | MAX                    | =               | >           | 7                | 06483314, 06483317                                                                       |
#      | MAX                    | =               | >=          | 7                | 06483314, 06483316, 06483317                                                             |
      | MAX                    | =               | <           | 7                | 06483313, 06483315, 06482988, 06483309, 06483310                                         |
#      | MAX                    | =               | <=          | 7                | 06483313, 06483315, 06483316, 06482988, 06483309, 06483310                               |
      | Pickup Customer Name   | contains        | contains    | POSH             | 06483312                                                                                 |
#      | Pickup Customer Name   | contains        | equals      | BLUEBIRD PAPERIE | 06483314                                                                                 |
#      | Pickup Customer Name   | contains        | starts with | S                | 06483316, 06483317, 06483310                                                             |
#      | Pickup Customer Name   | contains        | ends with   | E                | 06483312, 06483313, 06483314, 06483317, 06483311                                         |
#      | Pickup Customer Number | contains        | contains    | 535              | 06483316, 06483309                                                                       |
      | Pickup Customer Number | contains        | equals      | 5337100          | 06483313                                                                                 |
#      | Pickup Customer Number | contains        | starts with | 5337             | 06483313, 06483317                                                                       |
#      | Pickup Customer Number | contains        | ends with   | 2                | 06483315, 06483317                                                                       |
#      | Soldto Customer Name   | contains        | contains    | Test             | 06483312, 06483313, 06483314, 06483315, 06483317, 06483309, 06483311                     |
#      | Soldto Customer Name   | contains        | equals      | 000909_soldto3   | 06483316                                                                                 |
      | Soldto Customer Name   | contains        | starts with | C                | 06483310                                                                                 |
#      | Soldto Customer Name   | contains        | ends with   | Account          | 06483312, 06483313, 06483314, 06483315, 06483317, 06483309, 06483311                     |
#      | Soldto Customer Number | contains        | contains    | 236              | 06483316                                                                                 |
#      | Soldto Customer Number | contains        | equals      | 5123523          | 06483312, 06483313, 06483314, 06483315, 06483317, 06483309, 06483311                     |
#      | Soldto Customer Number | contains        | starts with | 51236            | 06483316                                                                                 |
      | Soldto Customer Number | contains        | ends with   | 4                | 06483316                                                                                 |
#      | BOL/DSM Number         | contains        | contains    | 12               | 06483315, 06483309, 06483310                                                             |
#      | BOL/DSM Number         | contains        | equals      | 8587             | 06483317                                                                                 |
#      | BOL/DSM Number         | contains        | starts with | 1                | 06483312, 06483316, 06483309                                                             |
#      | BOL/DSM Number         | contains        | ends with   | 7                | 06483312, 06483317                                                                       |
      | Case Number            | contains        | contains    | 0648331          | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483310, 06483311           |
#      | Case Number            | contains        | equals      | 06483313         | 06483313                                                                                 |
#      | Case Number            | contains        | starts with | 0648331          | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483310, 06483311           |
#      | Case Number            | contains        | ends with   | 11               | 06483311                                                                                 |

  @All @UI @WEL-13087
  Scenario Outline: User should be able to filter holds for '<column>' <operator>
    When click on "Filters" button
    And  Select "<column>" after click on "Case Number" field
    And  Select "<operator>" after click on "<defaultOperator>" field
    Then Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | column                 | defaultOperator | operator     | caseNumber                                                                                         |
      | Pallets                | =               | is empty     | 06483313                                                                                           |
#      | Pallets                | =               | is not empty | 06483312, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311           |
      | INTL                   | =               | is empty     |                                                                                                    |
#      | INTL                   | =               | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311 |
#      | GND                    | =               | is empty     | 06483310                                                                                           |
#      | GND                    | =               | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483311           |
#      | EXP                    | =               | is empty     | 06483311                                                                                           |
#      | EXP                    | =               | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310           |
#      | MAX                    | =               | is empty     | 06483312, 06483311                                                                                 |
      | MAX                    | =               | is not empty | 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310                     |
      | Pickup Customer Number | contains        | is empty     | 06482988                                                                                           |
#      | Pickup Customer Number | contains        | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483309, 06483310, 06483311           |
#      | Pickup Customer Name   | contains        | is empty     | 06482988                                                                                           |
#      | Pickup Customer Name   | contains        | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483309, 06483310, 06483311           |
#      | Soldto Customer Number | contains        | is empty     | 06482988, 06483310                                                                                 |
#      | Soldto Customer Number | contains        | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483309, 06483311                     |
#      | Soldto Customer Name   | contains        | is empty     | 06482988                                                                                           |
#      | Soldto Customer Name   | contains        | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06483309, 06483310, 06483311           |
#      | BOL/DSM Number         | contains        | is empty     |                                                                                                    |
#      | BOL/DSM Number         | contains        | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311 |
#      | Case Number            | contains        | is empty     |                                                                                                    |
      | Case Number            | contains        | is not empty | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311 |

  @All @UI @WEL-13087
  Scenario Outline: User is able to filter holds for '<column>' <operator> '<value>'
    When click on "Filters" button
    And  Select "<column>" after click on "Case Number" field
    And  Select "<operator>" after click on "<defaultOperator>" field
    And  text "<value>" is entered in "Filter value" box
    Then message "No results found." display
    Examples:
      | column                 | defaultOperator | operator    | value |
      | Pickup Customer Number | contains        | equals      | 535   |
      | Soldto Customer Name   | contains        | equals      | Test  |
      | Pallets                | =               | =           | 698   |
      | INTL                   | =               | <           | 0     |
      | GND                    | =               | <=          | 0     |
      | Pickup Customer Number | contains        | ends with   | 2222  |
      | BOL/DSM Number         | contains        | starts with | 1111  |

  @All @UI @WEL-13087
  Scenario Outline: User is able to filter holds for '<column>' <operator> '<value>'
    When click on "Filters" button
    And  Select "<column>" after click on "Case Number" field
    And  Select "<operator>" after click on "<defaultOperator>" field
    And  Select "<value>" after click on "Value" field
    Then Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | column    | defaultOperator | operator | value               | caseNumber                                                                                         |
      | Status    | is              | is       | Open                | 06482988, 06483309, 06483310, 06483311                                                             |
#      | Status    | is              | is       | Closed              |                                                                                                    |
#      | Status    | is              | is       | Released            | 06483312, 06483313, 06483314                                                                       |
#      | Status    | is              | is       | Info Required       | 06483315                                                                                           |
#      | Status    | is              | is       | Return to Customer  | 06483316, 06483317                                                                                 |
#      | Status    | is              | is not   | Open                | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317                                         |
      | Status    | is              | is not   | Closed              | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311 |
#      | Status    | is              | is not   | Released            | 06483315, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311                               |
#      | Status    | is              | is not   | Info Required       | 06483312, 06483313, 06483314, 06483316, 06483317, 06482988, 06483309, 06483310, 06483311           |
#      | Status    | is              | is not   | Return to Customer  | 06483312, 06483313, 06483314, 06483315, 06482988, 06483309, 06483310, 06483311                     |
      | Hold Type | is              | is       | MISSING DSM/BOL     | 06483316                                                                                           |
#      | Hold Type | is              | is       | NO ACTIVE AGREEMENT | 06483317, 06483309                                                                                 |
      | Hold Type | is              | is not   | NO ACTIVE AGREEMENT | 06483312, 06483313, 06483314, 06483315, 06483316, 06482988, 06483310, 06483311                     |

  @All @UI @WEL-13087
  Scenario Outline: User is able to filter holds for '<column>' <operator> '<value>'
    When click on "Filters" button
    And  Select "<column>" after click on "Case Number" field
    And  Select "<operator>" after click on "<defaultOperator>" field
    And  "<column>" list "<value>" is entered in Value field
    Then Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | column                 | defaultOperator | operator  | value                           | caseNumber                             |
      | Pallets                | =               | is any of | 56, 6                           | 06483312, 06483317, 06483309, 06483311 |
#      | INTL                   | =               | is any of | 1                               | 06482988, 06483310                     |
#      | GND                    | =               | is any of | 1                               | 06483309, 06483311                     |
      | Case Number            | contains        | is any of | 06483309,06483310               | 06483309, 06483310                     |
#      | EXP                    | =               | is any of | 32                              | 06483314                               |
#      | MAX                    | =               | is any of | 4                               | 06483310                               |
#      | Status                 | is              | is any of | Released, Info Required         | 06483312, 06483313, 06483314, 06483315 |
      | Hold Type              | is              | is any of | MIXED SERVICES, CREDIT HOLD     | 06483315, 06482988                     |
#      | Pickup Customer Number | contains        | is any of | 987000, 5332433                 | 06483310, 06483311                     |
      | Soldto Customer Name   | contains        | is any of | UmangAgrawal AC, 000909_soldto3 | 06483316                               |
#      | BOL/DSM Number         | contains        | is any of | 1234,3123                       | 06483309                               |

  @All @Mobile
  Scenario: In Mobile - Hold Case should be displayed as per Status with Case Count
    Given on mobile device
    When on "Holds Dashboard" page
    And page "Holds Dashboard" is loaded
    Then Status should display with Case Count and Color
      | status_caseCount       | background-color       |
      | Released (3)           | rgba(76, 175, 80, 0.3) |
      | Info Required (1)      | rgba(255, 152, 0, 0.3) |
      | Return to Customer (2) | rgba(239, 83, 80, 0.3) |
      | Open (4)               | rgba(0, 0, 0, 0)       |

  @All @Mobile
  Scenario Outline: In Mobile - User is able to click on status Tile '<MobileStatus>'
    Given on mobile device
    When on "Holds Dashboard" page
    And page "Holds Dashboard" is loaded
    Then "<MobileStatus>" Tile should be clickable
    Examples:
      | MobileStatus       |
      | Released           |
      | Info Required      |
      | Return to Customer |
      | Open               |

  @All @Mobile
  Scenario: In Mobile - Verify page elements on dashboard
    Given on mobile device
    When page "Holds Dashboard" is loaded
    Then "DASHBOARD" header is visible on page
    And "Search" textbox is visible
    And "Refresh" icon is visible
    And "Add" icon is visible

  @All @Mobile
  Scenario: In Mobile - User is able to see holds having status as 'Released', once clicked on 'Released' Status Tile
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When click on "Released (3)" status tab
    Then "3" subdivision get open
    And Hold Cases detail display on Mobile
      | Case Number | Pickup Customer Number | BOL/DSM Number | Pickup Customer Name | Type          | Time on Hold                 | MAX | EXP | GND | INTL | PALLETS |
      | 06483312    | 5332740                | 1987           | POSH BOUTIQUE        | NO SLOT FOUND | 2024-10-08T14:18:06.000+0000 | -   | 4   | 3   | 2    | 6       |
      | 06483313    | 5337100                | 93576900       | DAVE'S COFFEE        | LATE ARRIVAL  | 2024-10-08T14:18:48.000+0000 | 6   | 7   | 45  | 7    | -       |
      | 06483314    | 5332988                | 4688           | BLUEBIRD PAPERIE     | DIMMING       | 2024-10-08T14:19:22.000+0000 | 45  | 32  | 12  | 9    | 65      |

  @All @Mobile
  Scenario: In Mobile - User is able to see holds having status as 'Info Required', once clicked on 'Info Required' Status Tile
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When click on "Info Required (1)" status tab
    Then "1" subdivision get open
    And Hold Cases detail display on Mobile
      | Case Number | Pickup Customer Number | BOL/DSM Number | Pickup Customer Name             | Type           | Time on Hold                 | MAX | EXP | GND | INTL | PALLETS |
      | 06483315    | 5331782                | 98123          | JUICE BEAUTY INC C/O RETURN DEPT | MIXED SERVICES | 2024-10-08T14:38:10.000+0000 | 5   | 6   | 78  | 6    | 7       |

  @All @Mobile
  Scenario: In Mobile - User is able to see holds having status as 'Return to Customer', once clicked on 'Return to Customer' Status Tile
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When click on "Return to Customer (2)" status tab
    Then "2" subdivision get open
    And Hold Cases detail display on Mobile
      | Case Number | Pickup Customer Number | BOL/DSM Number | Pickup Customer Name | Type                | Time on Hold                 | MAX | EXP | GND | INTL | PALLETS |
      | 06483316    | 5355643                | 10101          | soldto_3Pickup3      | MISSING DSM/BOL     | 2024-10-08T14:41:51.000+0000 | 7   | 3   | 9   | 98   | 8       |
      | 06483317    | 5337052                | 8587           | SKIN CARE BY SUZIE   | NO ACTIVE AGREEMENT | 2024-10-08T14:44:43.000+0000 | 45  | 4   | 5   | 8    | 56      |

  @All @Mobile
  Scenario: In Mobile - User is able to see holds having status as 'Open', once clicked on 'Open' Status Tile
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When click on "Open (4)" status tab
    Then "4" subdivision get open
    And Hold Cases detail display on Mobile
      | Case Number | Pickup Customer Number | BOL/DSM Number | Pickup Customer Name | Type                | Time on Hold                 | MAX | EXP | GND | INTL | PALLETS |
      | 06482988    | -                      | 81004          | -                    | CREDIT HOLD         | 2024-08-06T08:50:57.000+0000 | 3   | 2   | 4   | 1    | 5       |
      | 06483309    | 5355567                | 1234           | 3veni PUL 1          | NO ACTIVE AGREEMENT | 2024-10-08T14:10:14.000+0000 | 3   | 2   | 1   | 7    | 6       |
      | 06483310    | 987000                 | 3124           | SIT2 business        | BARCODE QUALITY     | 2024-10-08T14:12:42.000+0000 | 4   | 3   | -   | 1    | 5       |
      | 06483311    | 5332433                | 9872           | BETHANY LANE         | TSA NOT APPROVED    | 2024-10-08T14:14:47.000+0000 | -   | -   | 1   | 5    | 6       |

  @All @Mobile @Smoke
  Scenario: In Mobile - User is able to see all holds for which user clicked on respective status tiles, last clicked status should not be collapsed
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When click on "Return to Customer (2)" status tab
    And click on "Info Required (1)" status tab
    Then "3" subdivision get open
    And Hold Cases detail display on Mobile
      | Case Number | Pickup Customer Number | BOL/DSM Number | Pickup Customer Name             | Type                | Time on Hold                 | MAX | EXP | GND | INTL | PALLETS |
      | 06483315    | 5331782                | 98123          | JUICE BEAUTY INC C/O RETURN DEPT | MIXED SERVICES      | 2024-10-08T14:38:10.000+0000 | 5   | 6   | 78  | 6    | 7       |
      | 06483316    | 5355643                | 10101          | soldto_3Pickup3                  | MISSING DSM/BOL     | 2024-10-08T14:41:51.000+0000 | 7   | 3   | 9   | 98   | 8       |
      | 06483317    | 5337052                | 8587           | SKIN CARE BY SUZIE               | NO ACTIVE AGREEMENT | 2024-10-08T14:44:43.000+0000 | 45  | 4   | 5   | 8    | 56      |

  @All @Mobile
  Scenario Outline: In Mobile - User is able to see 'No results found.' message, once user find no matched holds for '<searchText>' value in search textbox
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When text "<searchText>" is entered in "Search" box
    Then message "No results found." display
    Examples:
      | searchText    |
#      | Releases      |
#      | SORT          |
      | MISSING IAC   |
#      | 9876          |
#      | 5678          |
#      | Alice Johnson |
#      | Bob Smith     |
#      | Close         |
      | BWI           |
#      | 0648332       |
#      | Seconds       |
#      | Minute        |

  @All @Mobile
  Scenario Outline: In Mobile - User is able to see only matched holds for '<searchText>' value in search textbox.
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When text "<searchText>" is entered in "Search" box
    Then mobile Hold Cases are displayed in "<caseNumber>" order
    Examples:
      | searchText          | caseNumber                                                                     |
#      | Release             | 06483312, 06483313, 06483314                                                   |
#      | Info                | 06483315                                                                       |
      | Open                | 06482988, 06483309, 06483310, 06483311                                         |
#      | Return              | 06483315, 06483316, 06483317                                                   |
#      | BARCODE QUALITY     | 06483310                                                                       |
#      | NO ACTIVE AGREEMENT | 06483317, 06483309                                                             |
#      | LATE ARRIVAL        | 06483313                                                                       |
#      | NO                  | 06483312, 06483317, 06483309, 06483311                                         |
#      | OL                  | 06483316, 06482988                                                             |
#      | RE                  | 06483312, 06483313, 06483314, 06483315, 06483316, 06483317, 06482988, 06483309 |
#      | MI                  | 06483314, 06483315, 06483316                                                   |
      | 06482               | 06482988                                                                       |
#      | 87                  | 06483312, 06483317, 06483310, 06483311                                         |
#      | 00                  | 06483313, 06482988, 06483310                                                   |
#      | 000                 | 06483310                                                                       |
#      | 98                  | 06483312, 06483314, 06483315, 06483316, 06482988, 06483310, 06483311           |
#      | 81                  | 06483315, 06482988                                                             |
#      | 1234                | 06483309                                                                       |
#      | _                   | 06483316                                                                       |
#      | 987                 | 06483312, 06483310, 06483311                                                   |
#      | 3veni               | 06483309                                                                       |
#      | 5332                | 06483312, 06483314, 06483311                                                   |
#      | 5332988             | 06483314                                                                       |

  @All @UI  @Smoke @WEL-13288
  Scenario: User is able to redirect on 'New Hold' Page once user clicked on 'Create Hold' button displayed on dashboard.
    Given page "Holds Dashboard" is loaded
    And "Create Hold" button is visible
    When click on "Create Hold" button
    Then page "Holds Create" is loaded
    And "NEW HOLD" header is visible on page

  @All @Mobile @Smoke @WEL-13288
  Scenario: In Mobile - User is able to redirect on 'New Hold' Page once user clicked on '+' icon displayed on dashboard.
    Given on mobile device
    And page "Holds Dashboard" is loaded
    And "Add" icon is visible
    When click on "Add" icon
    Then page "Holds Create" is loaded
    And "NEW HOLD" header is visible on page

  @All @UI @Smoke
  Scenario: User is able to redirect on 'Hold detail' page, once user clicked on any holds.
    Given page "Holds Dashboard" is loaded
    When click on any holdCase display on "dashboard"
    Then page "Holds Detail" is loaded for clicked holdCase
    And "CASE #" header is visible with clicked holdCase detail on page

  @All @Mobile
  Scenario: In Mobile - User is able to redirect on 'Hold detail' page, once user clicked on any holds.
    Given on mobile device
    And page "Holds Dashboard" is loaded
    When click on any status tab display on mobile dashboard
    And click on any holdCase display on "Mobile dashboard"
    Then page "Holds Detail" is loaded for clicked holdCase
    And "CASE #" header is visible with clicked holdCase detail on page

  @All @UI @Smoke
  Scenario: User should be able to logged out from dashboard
    When user logged out by click on MyAccount icon
    Then page "Holds Logout" is loaded