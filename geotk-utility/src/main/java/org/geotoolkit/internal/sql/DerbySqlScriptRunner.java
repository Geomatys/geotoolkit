/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * An sql script runner replacing the boolean used in postgres by small Interger used by derby.
 *
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DerbySqlScriptRunner extends ScriptRunner {

    public DerbySqlScriptRunner(final Connection c) throws SQLException {
        super(c);
    }

    /**
     * execute SQL code.
     * escape the postgres specific operation and replace the boolean by small int.
     *
     * the code of this method must be review.
     *
     * @param sb
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @Override
    protected int execute(final StringBuilder sb) throws SQLException, IOException {
        String query = sb.toString();

        /*
         * we transform the boolean value in smallint value
         */
        query        = query.replace("'false'", "'FALSE'");
        query        = query.replace("'true'", "'TRUE'");
        // use a regex to replace the 2 line above
        query        = query.replace("false", "0");
        query        = query.replace("true", "1");
        // remove the 2 line under
        query        = query.replace("'FALSE'", "'false'");
        query        = query.replace("'TRUE'", "'true'");

        /*
         * we transform the boolean column in smallint one
         */
        query        = query.replace("boolean", "smallint");

        /*
         * we remove the sequence part in column
         */
        if (query.contains("nextval(")) {
            query = query.replaceAll("DEFAULT nextval\\(.*regclass\\)", "");
        }

        /*
         * timestamp type can not be without timezone
         */
        query = query.replace("timestamp without time zone", "timestamp");

        /*
         * Postgis type does not work with derby
         * This is a dirty hack
         */
        query = query.replace("postgis.geometry", "character varying(40)");

        if (!query.startsWith("SET check_function_bodies")
                && !query.startsWith("SET client_min_messages")
                && !query.startsWith("SET search_path")
                && !query.startsWith("SET client_encoding")
                && !query.startsWith("SET standard_conforming_strings")
                && !query.startsWith("SET escape_string_warning")
                && !query.startsWith("SET default_tablespace")
                && !query.startsWith("SET default_with_oids")
                && !query.startsWith("CREATE INDEX")
                && !query.startsWith("SELECT pg_catalog.setval")
                && !"".equals(query)) {
            try {
                return super.execute(new StringBuilder(query));
            } catch (SQLException ex) {
                Logger.getAnonymousLogger().warning("SQL exception while executing:" + query);
                throw ex;
            }
        } else {
            return 0;
        }
    }




}
