package org.fao.fenix.faosyb.utils.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.TestCase;
import org.fao.fenix.faosyb.utils.FAOSYBUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class FAOSYBStepsDefinition {

    FAOSYBUtils u;

    List<Integer> l;

    List<String> csvList;

    String csv;

    @Given("^a library of utilities$")
    public void a_library_of_utilities() throws Throwable {
        u = new FAOSYBUtils();
        TestCase.assertNotNull(u);
    }

    @When("^I send \"([^\"]*)\" as a parameter$")
    public void I_send_as_a_parameter(String years) throws Throwable {
        try {
            l = u.buildYearsList(years);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Then("^I retrieve a list of (\\d+) elements$")
    public void I_retrieve_a_list_of_elements(int listSize) throws Throwable {
        assertEquals(listSize, l.size());
    }

    @When("^I send a list of elements$")
    public void I_send_a_list_of_elements() throws Throwable {
        csvList = new ArrayList<String>();
        csvList.add("2010");
        csvList.add("Afghanistan");
        csvList.add("2");
        csvList.add("POP.TOT");
        csvList.add("3456729");
        csv = u.convertToCSV(csvList);
        System.out.println(csv);
    }

    @Then("^I retrieve a CSV string$")
    public void I_retrieve_a_CSV_string() throws Throwable {
        assertTrue(csv.length() > 0);
    }

}