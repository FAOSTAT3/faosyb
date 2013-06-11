Feature: A library of utils to be used by the REST service

  Scenario Outline: Parse the input to create a list of years
    Given a library of utilities
    When  I send <years> as a parameter
    Then  I retrieve a list of <size> elements

    Examples: Years
    |    years    | size |
    | "2000-2012" |  13  |
    | "2000-2000" |   1  |

  Scenario: Parse the input to create a CSV
    Given a library of utilities
    When  I send a list of elements
    Then  I retrieve a CSV string

  Scenario: Parse the input to create a list of indicators
    Given a library of utilities
    When  I send "12,14,28"
    Then  I get a list of 3 elements