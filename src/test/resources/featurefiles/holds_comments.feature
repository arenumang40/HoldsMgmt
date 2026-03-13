Feature: Holds Comments
  Comments are displayed in comment box.

  Background:
    Given logged in

  @WEL-13307
  Scenario: Comments can be added to Hold Case
    Given on "Hold Detail" page
    When comment "I added Comment" is added
    Then comment "I added Comment" is displayed

  Scenario: Empty comments cannot be added to Hold Case
    Given on "Hold Detail" page
    When adding empty comment is attempted
    Then empty comment cannot be created

  # minimum, maximum length of a comment - Salesforce

  @WEL-13313
  Scenario: Comments are persisted when adding more images to the Hold Case
    Given Hold Case exists
    And there is comment "Comment is here"
    And on "Hold Detail" page
    When image "picture.jpg" is attached
    Then comment "Comment is here" is displayed
    And image "picture.jpg" is displayed

  @WEL-13313
  Scenario: Images are persisted when adding more images to the Hold Case
    Given Hold Case exists
    And image "photo.jpg" is attached
    And on "Hold Detail" page
    When image "picture.jpg" is attached
    And image "photo.jpg" is displayed
    And image "picture.jpg" is displayed

  @salesforce
  Scenario: Comment in Salesforce shows Hold Case comment creator
    Given logged in as "Joe"
    And Hold Case exists
    When comment "Just added a new comment" is added to Hold Case
    Then in Salesforce comment "Just added a new comment" displays creator name "Joe"
