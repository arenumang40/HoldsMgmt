Feature: Allow placard label to be printed

  @All @UI @WEL-19620 @pconly
  Scenario: Printing Placard is enabled on Hold Case creation when using Tablet and PC
    Given using PC or tablet device
    And on "Hold Creation" page
    When Hold Case is successfully created
      |  | Open | ... |
    #Then page "Hold Success" is loaded
    Then "Print Placard" button is displayed
    And "Print Placard" button is enabled

  @All @UI @WEL-19620 @mobile
  Scenario: Printing Placard is disabled on Hold Case creation when using mobile device
    Given using mobile device
    And on "Hold Creation" page
    When Hold Case is successfully created
    Then "Print Placard" button is displayed
    And "Print Placart" button is disabled

  @All @UI
  Scenario Outline: Print Placard Label for a Hold
    Given Hold Case exists
      | <Hold Type> | <Customer Number> | <User> | <Facility> |
    When placard label is printed
    Then the printed placard label should display the following details
      | <Hold Type> | <Hold Placard Code> | <Customer Number> | <Customer Name> | <Hold Method> | <User> | <Facility> |
    And printed placard label displays "HOLD"
    And printed placard label displays Hold Case number
    And printed placard label displays day of week when it was printed
    And printed placard label displays time and date when Hold Case was created in format "MM/DD/YYYY HH:MIN AM/PM"
    # TODO: verify the date format and modify if it could be different

    Examples:
      | Hold Type    | Hold Placard Code | Customer Number | Customer Name   | Hold Method     | User     | Facility |
      | No Data      | ????              | 0001234567      | Sample Customer | Manual          | driley   | ATL      |
      | Late Arrival | ????              | 0001234567      | Sample Customer | Sampling        | driley   | ATL      |

    # TODO clarify where is Hold Placard Code coming from
    # TODO clarify where is the Hold Method coming from

#  @WEL-19710
#  Scenario: entering BOL/DSM value
#    Given the user is on Hold Creation page
#    When they are creating holds
#    Then they can enter the "BOL/DSM" value
#    And  that value can be searched or tracked in SF and must reflect on the Placard.
    # TODO break down - move placard check - applicable to below scenarios too