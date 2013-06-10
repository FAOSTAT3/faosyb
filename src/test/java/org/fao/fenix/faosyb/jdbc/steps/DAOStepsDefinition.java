/**
 *
 * FENIX (Food security and Early warning Network and Information Exchange)
 *
 * Copyright (c) 2011, by FAO of UN under the EC-FAO Food Security
 Information for Action Programme
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.fao.fenix.faosyb.jdbc.steps;

import com.google.gson.Gson;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.fao.fenix.faosyb.datasource.FAOSYB;
import org.fao.fenix.faosyb.jdbc.JDBCIterable;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class DAOStepsDefinition {

    FAOSYB datasource;

    JDBCIterable it;

    Gson g;

    @Given("^a JDBC connection$")
    public void a_JDBC_connection() throws Throwable {
        datasource = new FAOSYB();
        it = new JDBCIterable();
        g = new Gson();
        TestCase.assertNotNull(datasource);
        TestCase.assertNotNull(it);
        TestCase.assertNotNull(g);
    }

    @When("^I create the \"([^\"]*)\" table for the year (\\d+)$")
    public void I_create_the_table_for_the_year(String tablename, int year) throws Throwable {
        String sql_select = "SELECT * FROM TEST LIMIT 1";
        it.query(datasource, sql_select);
    }

    @Then("^there the DB has a table called \"([^\"]*)\" with (\\d+) columns$")
    public void there_the_DB_has_a_table_called_with_columns(String tablename, int numberOfColumns) throws Throwable {
        while (it.hasNext())
            Assert.assertEquals(numberOfColumns, it.next().size());
    }

}