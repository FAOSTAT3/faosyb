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
package org.fao.fenix.faosyb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class FAOSYBUtils {

    /**
     * @param years         A string passed to the REST, must be in the 'YYYY-YYYY' format
     * @return              A list of years
     * @throws Exception
     *
     * This function parse the years parameter passed to the REST and creates a list of years.
     */
    public List<Integer> buildYearsList(String years) throws Exception {
        if (!years.contains("-"))
            throw new Exception("Please check your syntax. The string must be in the format '2010-2020'.");
        if (years.length() != 9)
            throw new Exception("Please check your syntax. The string must be in the format '2010-2020'.");
        List<Integer> l = new ArrayList<Integer>();
        List<Integer> tmp = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(years, "-");
        while (st.hasMoreElements())
            tmp.add(Integer.parseInt(st.nextElement().toString()));
        if (tmp.get(0) > tmp.get(tmp.size() - 1))
            throw new Exception("Please check your syntax. The final year is greater than the starting one.");
        if (tmp.get(0) == tmp.get(tmp.size() - 1)) {
            l.add(tmp.get(0));
        } else {
            for (int i = tmp.get(0) ; i <= tmp.get(tmp.size() - 1) ; i++)
                l.add(i);
        }
        return l;
    }

    /**
     * @param l A list of values to be converted in the CSV format
     * @return
     *
     * Converts a list of values in the CSV format. The last element of the list is supposed
     * to be a value and it is not wrapped with quotes.
     */
    public String convertToCSV(List<String> l) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            if (i < l.size() - 1) {
                sb.append("\"").append(l.get(i)).append("\"");
                sb.append(",");
            } else {
                sb.append(l.get(i));
            }
        }
        return sb.toString();
    }

}