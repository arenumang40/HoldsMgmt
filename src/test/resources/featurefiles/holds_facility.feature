@facility
Feature: Facility selection
  Holds belong to a facility and so users have to have one selected

  @All @UI @WEL-19537
  Scenario: Display current facility
    Given logged in
    When on "Holds Dashboard" page
    Then Facility button is displayed on the top right corner

  @All @UI
  Scenario: Default facility is displayed on first login
    Given logged in
    When on "Holds Dashboard" page
    Then "ATL" facility is displayed

  @All @UI @Smoke @WEL-19510
  Scenario: Users can list facilities
    Given logged in
    And on "Holds Dashboard" page
    When unrolling facility list
    Then a dynamic list of facilities is shown from masterdata
      | ATL | BWI | MCO | RDU | BOS | EWR | CAK | CVG | MCI | ORD | DFW | IAH | PHX | DEN | SLC | LAX | SEA | SFO |

  @All @UI
  Scenario: Selecting default facility
    Given logged in
    And on "Holds Dashboard" page
    When selects "ORD" from facility modal
    Then "ORD" is the current facility

  @All @UI @WEL-19533
  Scenario: Persisting default facility after log out
    Given logged in
    And on "Holds Dashboard" page
    And "ATL" is the current facility
    When user logs out and logs in
    Then "ATL" is the current facility