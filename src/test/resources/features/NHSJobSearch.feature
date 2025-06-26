Feature: NHS Job Search Functionality
  As a jobseeker on the NHS Jobs website
  I want to search for jobs using my preferences
  So that I can see relevant and recent job listings

  Background:
    Given I am on the NHS jobs search page

  @ui
  Scenario: Verify placeholder text and default dropdown values after expanding more search options
    When I click on the More search options link
    Then the What field should display hint text "For example, job title or skills"
    And the Distance dropdown should be disabled with "All locations" selected by default
    And the Pay Range dropdown should have "Please select" option selected by default


  @happy
  Scenario Outline: Fill out NHS job search form and verify field selections
    Given I am on the NHS jobs search page
    When I enter job title "<jobTitle>"
    And I enter location "<location>"
    And I select "<distance>" in the Distance dropdown
    And I click on More search options
    And I enter job reference "<jobReference>"
    And I enter employer "<employer>"
    And I select "<payRange>" from the Pay Range dropdown
    And I click on the Search button
    Then I wait for search results to be displayed
    And the sort by dropdown should have "Best Match" selected by default
    When I select "Date Posted (newest)" from the sort by dropdown
    Then the sort by dropdown should have "Date Posted (newest)" selected
    Then the Job Title field should contain "<jobTitle>"
    And the Location field should contain "<location>"
    And the Job Reference field should contain "<jobReference>"
    And the Employer field should contain "<employer>"

    Examples:
      | jobTitle | location   | distance  | jobReference      | employer  | payRange           |
      | Nurse    | London     | +10 Miles | 200-7220253-GO-LP | NHS Trust | £20,000 to £30,000 |
      | Doctor   | Manchester | +20 Miles |                   |           | £30,000 to £40,000 |
  @ui
  Scenario: Verify all options in Distance dropdown
    Then the Distance dropdown should contain the following options:
      | +5 Miles   |
      | +10 Miles  |
      | +20 Miles  |
      | +30 Miles  |
      | +50 Miles  |
      | +100 Miles |

  @ui
  Scenario: Distance field is disabled initially and enabled after entering Location
    Given the Distance field is disabled
    When I enter location "London"
    Then the Distance field should be enabled

  @ui
  Scenario: Verify all options in Pay Range dropdown
    Then the Pay Range dropdown should contain the following options:
      | Please select      |
      | £0 to £10,000      |
      | £10,000 to £20,000 |
      | £20,000 to £30,000 |
      | £30,000 to £40,000 |
      | £40,000 to £50,000 |
      | £50,000 to £60,000 |
      | £60,000 to £70,000 |
      | £70,000 to £80,000 |
      | £80,000 to £90,000 |
      | £90,000 to £100,000|
      | £100,000 plus      |

  @ui
  Scenario: Verify all options in Sort By dropdown
    When I enter job title "Nurse"
    And I enter location "London"
    And I click on More search options
    And I enter job reference ""
    And I enter employer ""
    And I click on search button
    Then I wait for search results to be displayed
    Then the sort by dropdown should contain the following options:
      | Best Match                |
      | Closing Date             |
      | Date Posted (newest)     |
      | Salary lowest to highest |
      | Salary highest to lowest |

  @unhappy
  Scenario Outline: Search with invalid inputs and verify error messages
    When I enter job title "<jobTitle>"
    And I enter location "<location>"
    And I select "<distance>" in the Distance dropdown
    And I click on More search options
    And I enter job reference "<jobReference>"
    And I enter employer "<employer>"
    And I select "<payRange>" from the Pay Range dropdown
    And I click on search button
    Then I should see error message "<expectedMessage>"

    Examples:
      | jobTitle      | location   | distance   | jobReference      | employer      | payRange            | expectedMessage    |
      | Nurse         | Londn      | +10 Miles  | 200-7220253-GO-LP | NHS Trust     | £20,000 to £30,000  | Location not found |
      | Test Manager  | Bristol    | +50 Miles  | FAKE-REF-999      | NHS Trust     | £70,000 to £80,000  | No result found    |
      | Nurse         | London     | +10 Miles  | 200-7220253-123   | Fake Employer | £20,000 to £30,000  | No result found    |
      | Nurse         | 12345678   | +10 Miles  | 200-7220253-GO-LP | NHS Trust     | £90,000 to £100,000 | Location not found |
      | Doctor        | Mnachester | +20 Miles  |                   |               | £30,000 to £40,000  | Location not found |
      | Doctor        | Manchester | +20 Miles  | INVALID-REF-123   | NHS Trust     | £30,000 to £40,000  | No result found    |
      | Doctor        | Manchester | +20 Miles  |                   | Fake NHS      | £30,000 to £40,000  | No result found    |
      | RandomFakeJob | Bath       | +100 Miles | 1234              |               | £100,000 plus       | No result found    |
      | !@#$%^&*      | London     | +10 Miles  |                   | NHS Trust     | £20,000 to £30,000  | No result found    |
      | Nurse123      | London     | +10 Miles  |                   |               | £20,000 to £30,000  | No result found    |
      | Doctor        | London123  | +10 Miles  |                   | NHS Trust     | £20,000 to £30,000  | Location not found |


  @happy
  Scenario: Search with all fields empty
    When I enter job title ""
    And the Distance dropdown should be disabled with "All locations" selected by default
    And I click on More search options
    And I enter job reference ""
    And I enter employer ""
    And I select "Please select" from the Pay Range dropdown
    And I click on search button
    Then I wait for search results to be displayed
    And I should see all available job results

  @happy
  Scenario: Clear filters resets all search fields to default
    When I enter job title "Nurse"
    And I enter location "London"
    And I select "+10 Miles" in the Distance dropdown
    And I click on More search options
    And I enter job reference "12345"
    And I enter employer "NHS Trust 1"
    And I select "£40,000 to £50,000" from the Pay Range dropdown
    And I click on the Clear filters button
    Then the Job Title field should be empty
    And the Location field should be empty
    And the Distance dropdown should be disabled and have "All locations" selected

    @happy
  Scenario: Happy path - Valid input returns suggestions
    When I enter "Developer" into the what field
    And I enter "Lon" into the location field
    Then I should see suggestions containing "Lon"
    And I select "London" from the suggestions

      @unhappy
  Scenario: Unhappy path - Invalid input returns no suggestions
    When I enter "Tester" into the what field
    And I enter "XYZ1234567" into the location field
    Then I should see no suggestions


