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

package org.geotoolkit.util.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.geotoolkit.internal.sql.ScriptRunner;

/**
 * An sql script runner replacing the boolean used in postgres by small Interger used by derby.
 *
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DerbySqlScriptRunner extends ScriptRunner {

    public DerbySqlScriptRunner(Connection c) throws SQLException {
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
    protected int execute(StringBuilder sb) throws SQLException, IOException {
        String query = sb.toString();
        query        = query.replace("'false'", "'FALSE'");
        query        = query.replace("'true'", "'TRUE'");
        query        = query.replace("false", "0");
        query        = query.replace("true", "1");

        if (!query.startsWith("SET check_function_bodies")
                && !query.startsWith("SET client_min_messages")
                && !query.startsWith("SET search_path")
                && !query.startsWith("SET client_encoding")
                && !query.startsWith("SET standard_conforming_strings")
                && !query.startsWith("SET escape_string_warning")
                && !query.startsWith("SET default_tablespace")
                && !query.startsWith("SET default_with_oids")
                && !query.startsWith("CREATE INDEX")
                && !query.startsWith("CREATE SEQUENCE")
                && !query.startsWith("SELECT pg_catalog.setval")
                && !query.equals("")) {
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
