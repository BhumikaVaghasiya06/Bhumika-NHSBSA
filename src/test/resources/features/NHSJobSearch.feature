Feature: NHS Job Search Functionality
  As a jobseeker on the NHS Jobs website
  I want to search for jobs with my preferences
  So that I can see relevant and recent job listings

  Background:
    Given I am on the NHS jobs search page

  @happy
  Scenario Outline: Successful search and sort by Date Posted (newest)
    When I enter job title "<jobTitle>"
    And I enter location "<location>"
    And I click on search button
    Then I wait for search results to be displayed
    And I wait for sort by dropdown to show default Best Match
    When I select "Date Posted (newest)" from sort by dropdown
    Then the sort by dropdown should have "Date Posted (newest)" selected

    Examples:
      | jobTitle | location |
      | Nurse    | London   |
      | Doctor   | London   |

  @ui
  Scenario: Verify all options in Sort By dropdown
    When I enter job title "Nurse"
    And I enter location "London"
    And I click on search button
    Then I wait for search results to be displayed
    Then the sort by dropdown should contain the following options:
      | Best Match                |
      | Closing Date             |
      | Date Posted (newest)     |
      | Salary lowest to highest |
      | Salary highest to lowest |

  @unhappy
  Scenario Outline: Search jobs with invalid job title or location
    When I enter job title "<jobTitle>"
    And I enter location "<location>"
    And I click on search button
    Then I should see error message "<expectedMessage>"

    Examples:
      | jobTitle      | location     | expectedMessage     |
      | RandomFakeJob | London       | No result found     |
      | Nurse         | InvalidCity  | Location not found  |
      | !@#$%^&*()    | London       | No result found     |
      | Doctor        | 1234567890   | Location not found  |
      | !@#$%^&*()    | 1234567890   | Location not found  |

  @unhappy
  Scenario: Search with empty job title and location
    When I click on search button
    Then I should see validation errors or results page with no filters
