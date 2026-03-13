Feature: Hold status indicates progress of the hold
  As a System, When the SF case is updated by a Ramp or SF user, the change details are displayed on the Hold mgmt detail screen for that hold case so that the User can see the updated Status of the Hold Case

  #Available States (and colors)
  # Released - green
  # Info Required - yellow
  # Returned to Customer - red
  # Open - white
  # Closed - not visible in Holds Management application, only in Salesforce

  # Workflow
  # Open -> Information Required (SF only) -> Released/Return to Customer -> Close -> Reopen
  # Open -> Released
  # Open -> Return to Customer
  # Released -> Close
  # Return to Customer -> Close
  # Open -> Close
  # Released -> Close
  # Released -> Open
  # Close -> Open (only in SF)

  @All @UI @WEL-13091
  Scenario: Default Hold Case status is Open
    Given on "Create Hold" page
    When new Hold Case is created
    Then Hold Case status is "Open"

  @All @UI @WEL-13094 @WEL-13095 @WEL-13113 @WEL-13096
  Scenario Outline: Status changes in Salesforce are reflected
    Given following Hold Cases exist
      | <Hold ID> | <current status> |
    When case <Case ID> status changes to <new status> in Salesforce
    Then <Case ID> is in <new status>

    Examples:
      | Hold ID | current status       | new status           |
      |       1 | Open                 | Information Required |
      |       2 | Open                 | Released             |
      |       3 | Open                 | Return to Customer   |
      |       4 | Open                 | Closed               |
      |       5 | Information Required | Open                 |
      |       6 | Information Required | Closed               |
      |       7 | Released             | Open                 |
      |       8 | Released             | Closed               |
      |       9 | Return to Customer   | Closed               |
      |      10 | Return to Customer   | Open                 |
      |      11 | Closed               | Open                 |
      |      12 | Closed               | Information Required |

  @All @UI
  Scenario: When OPS adds comment to Information Required Hold Case the case transitions to Open
    Given Hold Case exists in status "Information Required"
    When comment is added
    Then Hold Case is in "Open" status

  @All @UI
  Scenario: When Closed Hold Case is reopened it transitions to Open status
    Given Hold Case exists in status "Closed"
    And on "Hold Detail" page
    When button "Reopen" is selected
    Then Hold Case is in "Open" status

  @All @UI @WEL-19541 @WEL-13110
  Scenario Outline: Close button is enabled only from Open and Information Required states
    Given Hold Case is in <status>
    When on "Hold Detail" page
    Then the "Close" button is <enabled>

  Examples:
    | status               | enabled |
    | Open                 | true    |
    | Information Required | true    |
    | Released             | false   |
    | Return to Customer   | false   |

  @All @UI @WEL-19541
  Scenario Outline: Reopen button is enabled only from Released or Returned to Customer states
    Given Hold Case is in <status>
    When on "Hold Detail" page
    Then the "Reopen" button is <enabled>

  Examples:
    | status               | enabled |
    | Open                 | false   |
    | Information Required | false   |
    | Released             | true    |
    | Return to Customer   | true    |

  @All @UI @WEL-19541 @WEL-13110
  Scenario Outline: Closing Hold Case updates case status
    Given Hold Case exists in status <initial status>
    When the "Close" button is clicked
    And user clicks <option> button
    Then Hold Case is in <target status>

  Examples:
    | initial status       | target status        | option |
    | Open                 | Closed               | Yes    |
    | Information Required | Closed               | Yes    |
    | Open                 | Open                 | No     |
    | Information Required | Information Required | No     |

  @All @UI @WEL-19541 @WEL-13110
  Scenario Outline: Reopening Hold Case updates case status
    Given Hold Case is in <initial status>
    When the "Reopen" button is clicked
    And user clicks <option> button
    Then Hold Case is in <target status>

    Examples:
      | initial status       | target status      | option |
      | Released             | Open               | Yes    |
      | Return to Customer   | Open               | Yes    |
      | Released             | Released           | No     |
      | Return to Customer   | Return to Customer | No     |

  @All @UI @WEL-13111
  Scenario: Confirmation message is displayed when closing Hold Case
    Given on "Hold Details" page
    And Hold Case is in status "Open"
    When the "Close" button is clicked
    Then a message "Was the Volume able to be processed successfully" is displayed

  @All @UI @WEL-13111
  Scenario: Confirmation message is displayed when reopening Hold Case
    Given on "Hold Details" page
    And Hold Case is in status "Closed"
    When the "Reopen" button is clicked
    Then a message "Was the Volume able to be processed successfully" is displayed