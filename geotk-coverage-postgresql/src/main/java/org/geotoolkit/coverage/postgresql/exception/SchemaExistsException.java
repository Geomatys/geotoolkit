/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.postgresql.exception;

import java.sql.SQLException;


/**
 * Exception to throw when trying to create a schema in a database and it is already
 * present in there.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public final class SchemaExistsException extends SQLException {

    public SchemaExistsException(final String schema) {
        super("Schema already exists in the database: "+ schema);
    }
}
