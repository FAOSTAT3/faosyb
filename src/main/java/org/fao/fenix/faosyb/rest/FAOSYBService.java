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

import org.fao.fenix.faosyb.datasource.FAOSYB;
import org.fao.fenix.faosyb.jdbc.JDBCIterable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
@Path("/get")
public class FAOSYBService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{version}/{years}/{indicators}")
    public Response get(@PathParam("version") final String version, @PathParam("years") final String years, @PathParam("indicators") final String indicators) {

        // Initiate the stream
        StreamingOutput stream = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                // Initiate utilities
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));

                // compute result
                JDBCIterable it = new JDBCIterable();
                String sql = "";

                try {

                    // Query DB
                    it.query(new FAOSYB(), sql);

                } catch (SQLException e) {
                    streamException(os, ("Method 'getDomains' thrown an error: " + e.getMessage()));
                } catch (ClassNotFoundException e) {
                    streamException(os, ("Method 'getDomains' thrown an error: " + e.getMessage()));
                }

                // write the result of the query
                while(it.hasNext()) {
                    List<String> l = it.next();
                    for (int i = 0 ; i < l.size() ; i++) {
                        writer.write(l.get(i));
                        if (i < l.size() - 1)
                            writer.write(",");
                    }
                    if (it.hasNext())
                        writer.write("\n");
                }

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

        // Stream result
        return builder.build();

    }

    private void streamException(OutputStream os, String msg) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(os));
        writer.write(msg);
        writer.flush();
    }

}