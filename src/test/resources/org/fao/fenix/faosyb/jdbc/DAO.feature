Feature: The system stores and retrieve data from a PostgreSQL DBMS.

  Scenario Outline: Fetch data from the DB
    Given a JDBC connection
    When  I pass <tablename>, <years> and <indicators> as parameters
    Then  I have an iterator with <size> values

    Examples: Feed different combination of parameters to test the DAO
    | tablename      | years       | indicators                        | size  |
    | "foodsec_2013" | "2010-2012" | "DA.DET.AV.IN.NO"                 | 756   |
    | "foodsec_2013" | "2010-2012" | "DA.DET.AV.IN.NO,DA.DET.PS.GR.NO" | 1512  |
    | "others_2013"  | "2010-2012" | "SP.POP.TOTL"                     | 501   |