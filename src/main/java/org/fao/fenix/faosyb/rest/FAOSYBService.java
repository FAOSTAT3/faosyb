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
package org.fao.fenix.faosyb.rest;

import org.fao.fenix.faosyb.utils.FAOSYBUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
@Path("get")
public class FAOSYBService {

    @GET
    @Path("{version}/{tablename}/{years}/{indicators}")
//    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@PathParam("version") final String version, @PathParam("tablename") final String tablename, @PathParam("years") final String years, @PathParam("indicators") final String indicators) {

        // Initiate the stream
        StreamingOutput stream = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                // Initiate utilities
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                FAOSYBUtils u = new FAOSYBUtils();
                List<Integer> yearsList = null;
                List<String> indicatorsList = null;

                // Parse the parameters
                try {
                    yearsList = u.buildYearsList(years);
                    indicatorsList = u.buildIndicatorsList(indicators);
                } catch (Exception e) {
                    streamException(os, ("Method 'get' thrown an error: " + e.getMessage()));
                }

                // Build the SQL query
                String sql = u.buildSQL(tablename + "_" + version, yearsList, indicatorsList);
                List<List<String>> l = null;

                try {

                    // Query DB
                    l = u.buildLongTable(sql);

                } catch (SQLException e) {
                    streamException(os, ("Method 'get' thrown an error: " + e.getMessage()));
                } catch (ClassNotFoundException e) {
                    streamException(os, ("Method 'get' thrown an error: " + e.getMessage()));
                }

                List<String> labels = u.buildLabelsList(l);
                List<List<String>> w = u.buildWideTable(l, yearsList, indicatorsList);

                // write the result of the query
                writer.write(u.buildCredits(version));
                writer.write(u.buildCSVHeadersWide(labels));
                for (List<String> row : w)
                    writer.write(u.convertToCSV(row));

                // Convert and write the output on the stream
                writer.flush();

            }

        };

        // Wrap result
        Response.ResponseBuilder builder = Response.ok(stream);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "POST");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With, Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Accept-Charset, Keep-Alive, Connection, Referer,Origin");
        builder.header("Content-Disposition", "attachment; filename=FAOSYB" + version + ".csv");

        // Stream result
        return builder.build();

    }

    private void streamException(OutputStream os, String msg) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(os));
        writer.write(msg);
        writer.flush();
    }

}