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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.fao.fenix.faosyb.datasource.FAOSYB;
import org.fao.fenix.faosyb.jdbc.JDBCIterable;
import org.fao.fenix.faosyb.utils.FAOSYBUtils;

import java.util.List;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class DAOStepsDefinition {

    FAOSYB datasource;

    JDBCIterable it;

    FAOSYBUtils u;

    @Given("^a JDBC connection$")
    public void a_JDBC_connection() throws Throwable {
        datasource = new FAOSYB();
        u = new FAOSYBUtils();
        it = new JDBCIterable();
        TestCase.assertNotNull(datasource);
        TestCase.assertNotNull(u);
        TestCase.assertNotNull(it);
    }

    @When("^I pass \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" as parameters$")
    public void I_pass_and_as_parameters(String tablename, String years, String indicators) throws Throwable {
        List<Integer> yearsList = u.buildYearsList(years);
        List<String> indicatorsList = u.buildIndicatorsList(indicators);
        String sql = u.buildSQL(tablename, yearsList, indicatorsList);
        System.out.println(sql);
        it.query(datasource, sql);
    }

    @Then("^I have an iterator with (\\d+) values$")
    public void I_have_an_iterator_with_values(int outputSize) throws Throwable {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        Assert.assertEquals(outputSize, count);
    }

}