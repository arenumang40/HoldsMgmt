Feature: Images and other files can be attached to Hold Cases

  @WEL-13125 @mobile #Automated
  Scenario: User can capture an image using camera
    Given on "Holds Detail" page
    And current device has a camera
    When button "Use Camera" is clicked
    And user takes a picture using camera
    Then the image is attached to the Hold Case

#  @WEL-13126 @unrealistic
#  Scenario: Images are automatically removed from device local storage
#    Given The App Uses the Device camera to capture a pic
#    When the captured image of the Mail item is attached to SF
#    Then the Pic is auto-deleted from the local device storage

  # Limit on size of attachment in Salesforce?
#  @WEL-13127 @todo #UnRealistic
#  Scenario: TODO
#    Given The user is on the Hold Details Screen
#    When The App Uses the Device camera to capture a pic
#    Then Pic captured must have a higher resolution
#    And must be small enough to be easily communicated over the network

  @WEL-13129 @salesforce
  Scenario: Images uploaded in Salesforce are visible on Hold Case
    Given Hold Case exists in Salesforce
    And has an image "photo.jpg" attached in Salesforce
    When on "Holds Detail" page
    Then the Hold Case displays the "photo.jpg" image

  @WEL-13130 #Automated
  Scenario: Images can be uploaded from local device storage
    Given Hold Case exists
    And file "localphoto.jpg" is stored locally
    When on "Holds Detail" page
    And user attaches "localphoto.jpg" file
    Then the file is attached to the Hold Case

  # on Holds Detail
  @WEL-13133 @todo #Cancelled
  Scenario: View image details
    Given logged in as "john
    And Hold Case exists
    And on "Holds Detail" page
    When user attaches file "imagedetails.jpg"
    Then file name "imagedetails.jpg" is visible
    And user "john" is displayed as uploader
    And correct upload timestamp is visible
    # This is when uploading files from PC, not needed in history, just show time stamp

  @WEL-13127 #UnRealistic
  Scenario Outline: Attachments up to allowed size of 2 GB in Salesforce can be uploaded
    Given Hold Case exists
    When user attaches <file> of <size>
    Then the attachment <file> is uploaded

    Examples:
      | file      | size   |
      | file1.jpg | 100 MB |
      | file2.jpg | 500 MB |
      | file3.jpg | 1 GB   |
      | file4.jpg | 1.9 GB |

  @WEL-13127 #Automated
  Scenario Outline: Attachments over 2 GB are too big for Salesforce and are rejected
    Given Hold Case exists
    When user attaches <file> of <size>
    Then the attachment is rejected
    And message "" is displayed
    # Daniel: Maximum upload size is 2 GB.

    Examples:
      | file      | size   |
      | file1.jpg | 2.1 GB |

  @WEL-13131 #Cancelled
  Scenario: Confirm upload of captured image
    Given Hold Case exists
    And on "Hold Detail" page
    When user attaches "photoconfirmation.jpg" file
    Then user can preview the attached image
    And image name "photoconfirmation.jpg" is displayed
    #And other details?

    #TODO on holds detail confirmation before attaching image to history

  # TODO: attachments cannot be deleted in Salesforce? Only uploader
  # what happens when user tries to delete an attachment?
  @WEL-13134 @todo #Cancelled
  Scenario: Users can remove image from Hold Case
    Given all the captured images are attached to the SF cases
    When user is Viewing the Case Pics
    Then the User can remove images from the Case
    And SF Case is Updated mentioning the User, Filename, and Timestamp of the removal event

  @WEL-13134 @todo #Cancelled
  Scenario: Users can remove multiple images from Hold Case at once
    Given Hold Case exists
    And 2 images are attached
    When user selects both images
    And selects "Delete"
    Then both images are removed
    And Hold Case is updated with username, filename and timestamp

  @WEL-19547 @pconly #Automated
  Scenario: Upload file button is only on PC and tablet
    Given Hold Case exists
    And user uses PC or tablet device
    When on "Hold Detail" page
    Then "Upload File" button is displayed
    And "Take Picture" button is not displayed

  @WEL-19547 @mobile #Automated
  Scenario: Capture image button is only on mobile device
    Given Hold Case exists
    And user uses mobile device
    When on "Hold Detail" page
    Then "Take Picture" button is displayed
    And "Upload File" button is not displayed

  @WEL-13318 @salesfore #Automated
  Scenario: Hold case Images stored in SF
    Given Hold cases are Created
    When Images are attached to the Hold case
    Then they are stored in SF Case.

  @WEL-13317 #Automated
  Scenario: Case creation confirmation
    Given The user has captured Hold Item Image
    When images are Uploaded and attached to the case
    Then The system should display the Thumbnails of the already added Images on the "Add an Image" screen

  # TODO clarify if this meant to allow attaching multiple images at once
  @WEL-13316 #Cancelled
  Scenario: List of attached Images
    Given The user has captured Hold Item Image
    When they are Uploading to the case
    Then they should be able to see a list of attached images on the "Add an Image" screen
    And System should also display the message "Add another Image" with a yes and No confirmation message.
  If they want to add more images, they can by hitting the "Yes" button.
    And if they choose "No" they can hit the "Skip" Button.
    And The user can Press/Hit the "Submit" Button.
