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

import org.fao.fenix.faosyb.datasource.FAOSYB;
import org.fao.fenix.faosyb.jdbc.JDBCIterable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class FAOSYBUtils {

    public String buildStatisticsSQL(String version, String tablename, String years, List<String> indicators) {
        StringBuilder sbi = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indicators.size(); i++) {
            sbi.append(indicators.get(i));
            if (i < indicators.size() - 1)
                sbi.append(",");
        }
        sb.append("INSERT INTO statistics VALUES('" + version + "', '" + tablename + "', '" + years + "', '" + sbi.toString() + "')");
        return sb.toString();

    }

    public List<List<String>> buildWideTable(List<List<String>> l, List<Integer> years, List<String> indicators) {
        List<List<String>> w = new ArrayList<List<String>>();
        List<String> row = new ArrayList<String>();
        for (int i = 1; i <= l.size(); i++) {
            String tmp = l.get(i - 1).get(4);
            tmp = tmp.contains("NA") ? "NA" : tmp.replaceAll("\"", " ").trim();
            row.add(tmp);
            if (i % indicators.size() == 0) {
                row.add(0, l.get(i - 1).get(2));
                row.add(0, l.get(i- 1).get(1));
                row.add(0, l.get(i - 1).get(0));
                w.add(row);
                row = new ArrayList<String>();
            }
        }
        return w;
    }

    public List<String> buildLabelsList(List<List<String>> l) {
        List<String> out = new ArrayList<String>();
        for (List<String> row : l)
            if (!out.contains(row.get(3)))
                out.add(row.get(3));
        return out;
    }

    public List<List<String>> buildLongTable(String sql) throws SQLException, ClassNotFoundException {
        List<List<String>> l = new ArrayList<List<String>>();
        JDBCIterable it = new JDBCIterable();
        it.query(new FAOSYB(), sql);
        while (it.hasNext())
            l.add(it.next());
        return l;
    }

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
     * @return  A <code>String</code> in the CSV format
     *
     * Converts a list of values in the CSV format. The last element of the list is supposed
     * to be a value and it is not wrapped with quotes.
     */
    public String convertToCSV(List<String> l) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            sb.append((i == l.size() - 1) ? clean(l.get(i)) : l.get(i));
            if (i < l.size() - 1)
                sb.append(",");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String clean(String v) {
        v = v.replaceAll("\"", " ").trim();
        if (v.contains("NA")) {
            return "NA";
        } else {
            return v;
        }
    }

    /**
     * @param indicators    A string passed to the REST, must be in the 'I1,I2,...,In' format
     * @return              A list of indicators
     */
    public List<String> buildIndicatorsList(String indicators) {
        List<String> l = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(indicators, ",");
        while (st.hasMoreElements())
            l.add(st.nextElement().toString());
        return l;
    }

    /**
     * @param tablename     There are two tables on the db: 'foodsec' and 'others'
     * @param years         A string passed to the REST, must be in the 'YYYY-YYYY' format
     * @param indicators    A string passed to the REST, must be in the 'I1,I2,...,In' format
     * @return              The SQL query
     *
     * Create the SQL query to be sent to the DB.
     */
    public String buildSQL(String tablename, List<Integer> years, List<String> indicators) {
        StringBuilder sb = new StringBuilder();
//        sb.append("SELECT un_code, \"Year\", substring(official_fao_name from 2 for (length(official_fao_name) - 2)), series_name, value ");
        sb.append("SELECT un_code, \"Year\", official_fao_name, series_name, value ");
        sb.append("FROM ").append(tablename).append(", labels ");
        sb.append("WHERE \"Year\" IN (");
        for (int i = 0; i < years.size(); i++) {
            sb.append(years.get(i));
            if (i < years.size() - 1)
                sb.append(",");
        }
        sb.append(")");
        sb.append(" AND variable IN (");
        for (int i = 0; i < indicators.size(); i++) {
            sb.append("'\"").append(indicators.get(i)).append("\"'");
            if (i < indicators.size() - 1)
                sb.append(",");
        }
        sb.append(") ");
        sb.append("AND un_code IN (SELECT un_code FROM ").append(tablename).append(" WHERE \"Year\" IN (");
        for (int i = 0; i < years.size(); i++) {
            sb.append(years.get(i));
            if (i < years.size() - 1)
                sb.append(",");
        }
        sb.append(") AND variable IN (");
        for (int i = 0; i < indicators.size(); i++) {
            sb.append("'\"").append(indicators.get(i)).append("\"'");
            if (i < indicators.size() - 1)
                sb.append(",");
        }
        sb.append(") ");
        sb.append("GROUP BY un_code HAVING count(*) = ").append(years.size() * indicators.size()).append(") ");
        sb.append("AND variable = ('\"' || data_key || '\"')");
        sb.append("ORDER BY official_fao_name, \"Year\", variable");
        return sb.toString();
    }

    public String buildCSVHeadersWide(List<String> indicators) {
        StringBuilder sb = new StringBuilder();
        sb.append("UN Code,");
        sb.append("Year,");
        sb.append("Official FAO Name,");
        for (int i = 0; i < indicators.size(); i++) {
            sb.append(indicators.get(i));
            if (i < indicators.size() - 1)
                sb.append(",");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String buildCSVHeaders() {
        StringBuilder sb = new StringBuilder();
        sb.append("UN Code,");
        sb.append("Year,");
        sb.append("Official FAO Name,");
        sb.append("Variable,");
        sb.append("Value");
        sb.append("\n");
        return sb.toString();
    }

    public String buildCredits(String version) {
        StringBuilder sb = new StringBuilder();
        Date d = new Date();
        sb.append("Downloaded from the FAO Statistical Year Book ").append(version).append(", ");
        if (d.getDate() < 10)
            sb.append("0");
        sb.append(d.getDate()).append("-");
        if (d.getMonth() < 10)
            sb.append("0");
        sb.append(d.getMonth()).append("-");
        sb.append(1900 + d.getYear());
        sb.append(",,\n");
        sb.append("\n");
        return sb.toString();
    }

    public void print(List<List<String>> l) {
        for (int i = 0; i < l.size(); i++) {
            List<String> strings =  l.get(i);
            for (int j = 0; j < strings.size(); j++) {
                String s =  strings.get(j);
                System.out.print(s);
                if (j < strings.size() - 1)
                    System.out.print(", ");
            }
            System.out.println();
        }
    }

}