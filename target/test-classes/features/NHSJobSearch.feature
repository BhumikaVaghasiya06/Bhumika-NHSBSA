Feature: NHS Job Search Functionality

  @happy
  Scenario Outline: Successful search and sort by Date Posted newest
    Given I am on the NHS jobs search page
    When I enter job title "<jobTitle>"
    And I enter location "<location>"
    And I click on search button
    Then I wait for search results to be displayed
    Then I wait for sort by dropdown to show default Best Match
    When I select Date Posted newest from sort by dropdown
    Then the sort by dropdown should have Date Posted newest selected

    Examples:
      | jobTitle | location |
      | Nurse    | London   |
      | Doctor   | London   |

  @unhappy
  Scenario Outline: Search jobs with invalid job title or location
    Given I am on the NHS jobs search page
    When I enter job title "<jobTitle>"
    And I enter location "<location>"
    And I click on search button
    Then I should see error message "<expectedMessage>"

    Examples:
      | jobTitle      | location    | expectedMessage      |
      | RandomFakeJob | London     | No result found      |
      | Nurse         | InvalidCity | Location not found   |
      | !@#$%^&*()    | London     | No result found      |
      | Doctor        | 1234567890  | Location not found   |
      | !@#$%^&*()    | 1234567890 | Location not found   |

  @unhappy
  Scenario: Search with empty job title and location
    Given I am on the NHS jobs search page
    When I click on search button
    Then I should see validation errors or results page with no filters

  @unhappy
  Scenario: Sorting dropdown does not change with invalid option
    Given I am on the NHS jobs search page
    When I enter job title "Nurse"
    And I enter location "London"
    And I click on search button
    Then I wait for search results to be displayed
    When I try to select an invalid option from sort by dropdown
    Then the dropdown should remain unchanged
