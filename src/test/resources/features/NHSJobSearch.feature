Feature: NHS Job Search Functionality

  Scenario Outline: Search jobs with What and Where, sort by date posted newest
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
