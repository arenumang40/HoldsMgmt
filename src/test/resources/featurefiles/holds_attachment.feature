Feature: Holds Attachments
  Images and other files can be attached to Hold Cases.

  Background:
    Given logged in
    And selects "PHX" from facility modal
    And No Hold Cases detail exist
    And click on "Create Hold" button
    And "NEW HOLD" header is visible on page

  @All @UI @WEL-13130 @WEL-13308 @WEL-19547
  Scenario Outline: Button "BROWSE FILES" display at 'New Hold' page to attach files (Desktop)
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When user is on "Enter Hold Details" selection step
    And "Browse files" button is visible
    Then able to add "<attachment>" file(s)
    And thumbnails of "<attachment>" file(s) is displayed on Screen
    Examples:
      | attachment                                 |
      | DOC_file.doc, JPEG_file.jpeg, PDF_file.pdf |

  @All @UI
  Scenario Outline: System should displayed a validation message for unsupported file formats.
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When upload the "<attachment>" file(s)
    Then validation message "<message>" should be displayed for uploaded "<attachment>" file(s)
    Examples:
      | attachment                                                                                 | message                                                                                                                                                                                      |
      | Invalid_GIF_file.gif, Invalid_JPE_file.jpe, Invalid_HEIC_file.heic, Invalid_JFIF_file.jfif | The file type of file \"%s\" is not allowed. The following file types are allowed: \"png\", \"jpg\", \"jpeg\", \"bmp\", \"tiff\", \"txt\", \"pdf\", \"doc\", \"docx\", \"xls\", \"xlsx\" |

  @UI @WEL-13127
  Scenario Outline: System should displayed a validation message for file "<attachment>" having Size > 20 MB.
    Given selects a Pickup by clicking the dropdown icon
    And selects any Hold Type
    When upload the "<attachment>" file(s)
    Then validation message "<message>" should be displayed for uploaded "<attachment>" file(s)
    Examples:
      | attachment          | message                                                    |
      | 20mb_DOCX_file.docx | The file \"%s\" is larger than the allowed limit 20 Mb |
      | 20mb_PNG_file.png   | The file \"%s\" is larger than the allowed limit 20 Mb |

  @All @UI @Smoke @WEL-13128 @WEL-13317 @WEL-13318 @WEL-13320
  Scenario Outline: Thumbnail display, case creation confirmation, and picture visibility on Hold creation in SF
    Given fills in the details on the new case creation page
    And upload the "<attachment>" file(s)
    When thumbnails of "<attachment>" file(s) is displayed on Screen
    And click on CREATE HOLD button
    And waits until the files are uploaded
    Then "Case Number / SalesForce Case Number" is visible
    And newly generated case number is visible
    And "<attachment>" file(s) is displayed against the Case on Salesforce
    Examples:
      | attachment                                 |
      | JPG_file.jpg, JPEG_file.jpeg, PNG_file.png |

  @All @UI @WEL-19547 @Mobile
  Scenario: Icon for Attachment or Camera, as per device during Hold Creation
    Given switch to mobile device
    When fills in the details on the new case creation page
    Then "Browse files" button is not visible
    And "Take a picture" button is visible

  @All @UI @Mobile @WEL-13125
  Scenario: User can capture the picture using the mobile camera during Hold Creation
    Given switch to mobile device
    When fills in the details on the new case creation page
    And "Take a picture" button is visible
    Then able to click the picture from camera

  @All @UI @Mobile @WEL-13125
  Scenario: User can capture the picture using the camera during Hold Detail
    Given switch to mobile device
    And fills in the details on the new case creation page
    And click on CREATE HOLD button
    And newly generated case number is visible
    And click on "Open hold details" button
    And camera icon is visible
    When click on camera icon
    Then able to click the picture from camera