Feature: Hold Case can be created
  Hold cases can be created from the dashboard page or in Salesforce

  @WEL-19538 @Automated
  Scenario: Create a new hold Case
    Given user "Name" is logged in
    And on "Hold Management" page
    When "+ Create Hold" is selected
    #When "+ Create Hold" sign is displayed on the Left most on the top of the page
    #Then the user can create a hold case by clicking on the "+ Create Hold" sign
    Then "Create Hold" page opens

    # TODO: improve to detail Open "create hold case" form

    #TODO go through the process of creating hold case

  @WEL-13288  @COVERinDASHBOARD @Automated
  Scenario: Hold Case process can be triggered from Hold Management page
    Given user "Name" is logged in
    When on "Hold Management" page
    Then "+ Create Hold" button is visible
    And "+ Create Hold" button on top of the page

    # it is possible to navigate fields (especially on Detail section) by hitting TAB
  @WEL-19711 @Automated
  Scenario: complete the form by tabbing
    Given on "Hold Creation" page
    When they are creating holds
    Then they can tab through the different data entry fields in the correct order

  @WEL-19582 @Cancelled
  #Searchable customer account list by DC
  # field can filter when searching and only shows pickup locations associated with selected facility
  Scenario: Filtering Pickup Customer
    Given user is on Hold Management application
    When they are on Hold Creation screen
    And when they on 1st step Select Customer (Pickup customer)
    Then they can filter Pickup customers by the Default DC value by using scrolling bar or by key in pickup number in search bar.
    And once they find the Pickup customer they are looking for
    Then user can select that Pickup Customer.

    # What does refresh do?

  @WEL-19710  @Automated @PLACARD
  Scenario: entering BOL/DSM value
    Given the user is on Hold Creation page
    When they are creating holds
    Then they can enter the "BOL/DSM" value
    And  that value can be searched/ tracked in SF and must reflect on the Placard.
    # TODO break down - move placard check - applicable to below scenarios too

  @WEL-19709 @Automated @PLACARD
  Scenario: entering the total number of pallets
    Given the user is on Hold Creation page
    When they are creating holds
    Then they can enter the total number of pallets being placed on Hold in the field "Total number of Pallets"
    And  the number is tracked in SF and must reflect on the Placard.

  @WEL-19708 @Automated
  Scenario: entering specific counts for each individual service
    Given the user is logged into the Hold Management
    When they are on the Hold Creation page
    And creating holds
    Then they can enter specific counts for each individual service

  @WEL-19706 @Automated
  Scenario: traverse back in the process
    Given User is on Hold Management app and is on Hold Creation Screen
    When they have selected the Hold type
    And have entered the hold Information
    Then they must see all the entered information on Subsequent Screens.
  #If they want to edit any information
    Then they can traverse back in the process to make any changes or edit by selecting the "Back" button.
    Then the user must be able to go back to the previous screen.

  @WEL-19620 @Automated
  #	Print Placard Button on Hold Creation
  Scenario: Printing Placard on Tablet/PC and not on Mobile
    Given User is on the Hold Management app and is on the Hold Creation Screen on a PC/Tablet
    And Hold was Created Successfully
    Then system must display the message "Hold Successfully Created" with SF case #
    And Print Placard Button functionality will work accordingly
    Then the user has the option to print the hold placard at the time of case creation
  #If an App is used on a mobile
    Then the button is disabled

    # confirmation screen is displayed when hold is created

  @WEL-19619 #CANCELLED_STORY
  #	Piece Count Field
  Scenario: User entering in the number of holds and reflecting in SF
    Given User is on the Hold Management app and is on the Hold Creation Screen
    When they are working through the Hold Creation Process
    And after selecting the Service level

  @WEL-19615 @Automated
  #Service Level Fields Multi-Select
  Scenario: User selecting one or more service level
    Given User is on Hold Management app and is on Hold Creation Screen
    When they are working through the Hold Creation Process
    Then they must be able to select  many service Levels, but only One hold Type to include in the created case

  @WEL-19613 @Automated
  # Accumulating Hold Data
  Scenario User able to see data on subsequent screen from previous screens
    Given User is on Hold Management app and is on Hold Creation Screen
    When they have selected the Hold type
    And are keying / entering in the hold Information
    Then they must see all the entered information on Subsequent Screens.

  @WEL-19612 @AUTOMATED
  #Hold Type Selection
  Scenario: User selecting Hold Type from Type selection
    Given User is on Hold Management app and is on Hold Creation Screen
    When all the Hold types are listed on the screen
    Then they must be able to select One of the full list of Hold Types.
    Then User can click on Confirm and work on the next portion of the Holds

  @WEL-19611 @Automated
  #Holds Creation Cancel Button
  Scenario: Canceling a Holds Creation
    Given on "Hold Management" application
    When they are on the Hold Creation screen
    Then user must be able to opt out of the screen anytime by selecting the "Cancel" button.
    And User returns to the Holds Screen
    And The new Holds does not get created
  # TODO check cancel from all steps

  @WEL-19583 @Automated
  #Holds Creation Process Stepper
  Scenario: Which step user is on when creating Holds
    Given the user is on the Hold Management application
    When they are on the Hold Creation screen
    Then they can see the Graphic indicator which step of the process the user is on.

  @WEL-13320 @Automated
  Scenario: Case creation confirmation
    Given The user is in the process of Hold Creation
    When User has completed Hold Creation & submit the Hold Case
    Then the system should display the Confirmation message "Hold Successfully Created" before resetting.

  @WEL-13319 @salesforce @Automated
  Scenario: SF Case Creation
    Given the user is on the HOLD Creation Screen
    When User has completed Hold Creation
    Then Hold Case Created message populates on the Screen
    And Hold case is created in SF

  @WEL-13308 @Automated
  Scenario: Add Attachment Button
    Given a user is creating a hold
    And they are on the Hold Type Selection screen
    When they want to add an image
    Then They can click 'Add Attachments' button to capture or attach files to the Hold Case

  @WEL-13302 @Automated @Pickup
  Scenario: Pickup Customer Result List
    Given User is prompted to enter the Pickup Customer
    When they have entered the Pickup Customer detail info on the search string to filter the list.
    Then the system should display a list of pickup customers(including the details: Pickup name, pickup number, and mailing address) based on the search string.

  @WEL-13301 @Automated @Pickup
  Scenario: Auto Fill Search by Customer Number or Name
    Given The user is on the Manual Hold Creation screen
    And they are in process of creation Holds
    When they are prompted to enter the Pickup customer
    Then the User should be able to type a search string by entering the Pickup Number, Pickup name, or both to filter the list of Pickup Customers

  @WEL-19760 @Automated @PLACARD
  Scenario: listing the total number of Pallets on hold Placard
    Given  Holds are being created
    When the total number of Pallets on hold is added
    Then this given number must be listed in the Details of Hold Placard.

  @WEL-19756 @Automated
  Scenario: Storing Item counts values for each service level
    Given Holds are being created
    When entering specific counts for each individual service
    Then this given item count values for each service level are stored separately in SF

  @todo @Automated
  Scenario: Image can be attached on PC

  @mobile @Automated
  Scenario: Image can be captured on mobile

  @Automated
    # TODO: Fields all option, numeric 15 characters max
    # BOL/DSM - more than 6 digits
    # Description 2000 characters
    # all fields blank on arrival