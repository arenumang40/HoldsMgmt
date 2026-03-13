Feature: Application authentication
  Users have to authenticate to access the holds management application

  @All @UI @WEL-19509
  Scenario: Valid user can log in and gets to the landing page
    Given on "Holds Login" page
    When user logs in
    Then page "Holds Dashboard" is loaded
    And "DASHBOARD" header is visible on page

  @All @UI @WEL-13288
  Scenario: Holds Dashboard is the application landing page
    Given on "Holds Login" page
    When logged in
    Then page "Holds Dashboard" is loaded