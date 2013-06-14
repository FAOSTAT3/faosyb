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
package org.fao.fenix.faosyb.jdbc;

import org.fao.fenix.faosyb.datasource.DATASOURCE;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class JDBCIterable implements Iterator<List<String>> {

    private Connection connection;

    private Statement statement;

    private ResultSet resultSet;

    private boolean hasNext;

    /**
     * @param datasource    Datasource for the query
     * @param sql           SQL query
     * @throws ClassNotFoundException
     * @throws SQLException
     *
     * Execute a SQL query and makes the results available through an <code>Iterator</code>
     */
    public void query(DATASOURCE datasource, String sql) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        this.setConnection(DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(), datasource.getPassword()));
        this.setStatement(this.getConnection().createStatement());
        this.getStatement().executeQuery(sql);
        this.setResultSet(this.getStatement().getResultSet());
    }

    public void insert(DATASOURCE datasource, String sql) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        this.setConnection(DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(), datasource.getPassword()));
        this.setStatement(this.getConnection().createStatement());
        this.getStatement().executeUpdate(sql);
        this.setResultSet(this.getStatement().getResultSet());
    }

    @Override
    public boolean hasNext() {
        return this.isHasNext();
    }

    @Override
    public List<String> next() {

        List<String> l = null;

        if (this.isHasNext()) {
            l = new ArrayList<String>();
            try {
                for (int i = 1 ; i <= this.getResultSet().getMetaData().getColumnCount() ; i++) {
                    try {
                        l.add(this.getResultSet().getString(i).trim());
                    } catch (NullPointerException e) {
                        l.add("NA");
                    }
                }
                this.setHasNext(this.getResultSet().next());
            } catch(SQLException ignored) {

            }
        }

        if (!this.isHasNext()) {
            try {
                this.getResultSet().close();
                this.getStatement().close();
                this.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return l;
    }

    @Override
    public void remove() {

    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        try {
            this.setHasNext(this.getResultSet().next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

}