Feature: The system stores and retrieve data from a PostgreSQL DBMS.

  Scenario: Create the DB structure
    Given a JDBC connection
    When  I create the "FOOD_SEC_INDICATORS" table for the year 2013
    Then  there the DB has a table called "FOOD_SEC_IND_2013" with 4 columns